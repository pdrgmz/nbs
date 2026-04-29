<script setup>
import { ref, watch } from 'vue'
import { fetchStats } from '../api/modules/stats'
import YearlyWeekComparisonChart from './YearlyWeekComparisonChart.vue'

const props = defineProps({
	selectedWeekKey: {
		type: String,
		default: '',
	},
})

const emit = defineEmits(['select-week'])

const yearlyWeekSeries = ref([])
const yearlyWeekSeriesLoading = ref(false)
const cachedWeeklyStatsByYear = ref({})

const getMondayFromDate = (dateValue) => {
	const date = new Date(dateValue)
	date.setHours(0, 0, 0, 0)

	const jsDay = date.getDay()
	const mondayOffset = jsDay === 0 ? -6 : 1 - jsDay
	date.setDate(date.getDate() + mondayOffset)

	return date
}

const formatDateKey = (dateValue) => dateValue.toLocaleDateString('sv-SE')

const parseDateKey = (value) => {
	if (typeof value !== 'string' || !value) {
		return null
	}

	const parsed = new Date(`${value}T00:00:00`)
	if (Number.isNaN(parsed.getTime())) {
		return null
	}

	return parsed
}

const getIsoWeekNumber = (dateValue) => {
	const date = new Date(Date.UTC(dateValue.getFullYear(), dateValue.getMonth(), dateValue.getDate()))
	const dayNumber = date.getUTCDay() || 7
	date.setUTCDate(date.getUTCDate() + 4 - dayNumber)
	const yearStart = new Date(Date.UTC(date.getUTCFullYear(), 0, 1))
	return Math.ceil((((date - yearStart) / 86400000) + 1) / 7)
}

const getFirstIsoMondayOfYear = (year) => {
	const jan4 = new Date(year, 0, 4)
	return getMondayFromDate(jan4)
}

const getLastIsoMondayOfYear = (year) => {
	const dec28 = new Date(year, 11, 28)
	return getMondayFromDate(dec28)
}

const addDays = (dateValue, days) => {
	const next = new Date(dateValue)
	next.setDate(next.getDate() + days)
	return next
}

const extractWeeklyActivities = (statsPayload) => {
	const responseStats = Array.isArray(statsPayload)
		? statsPayload
		: []

	const weeklyStat = responseStats.find((stat) => stat?.type === 'WEEKLY')
	return Array.isArray(weeklyStat?.activities) ? weeklyStat.activities : []
}

const summarizeDistanceKm = (activities) => {
	const totalDistance = activities.reduce((sum, activity) => sum + (Number(activity?.distance) || 0), 0)
	return totalDistance / 1000
}

const getYearFromDateKey = (dateKey) => {
	const parsed = parseDateKey(dateKey)
	return parsed ? parsed.getFullYear() : new Date().getFullYear()
}

const normalizeDistanceKm = (item) => {
	if (!item || typeof item !== 'object') {
		return 0
	}

	const kmValue = Number(item.distanceKm ?? item.totalDistanceKm ?? item.kilometers ?? item.totalDistance)
	if (Number.isFinite(kmValue) && kmValue >= 0) {
		return kmValue
	}

	const metersValue = Number(item.distance ?? item.meters)
	if (Number.isFinite(metersValue) && metersValue >= 0) {
		return metersValue / 1000
	}

	if (Array.isArray(item.activities)) {
		return summarizeDistanceKm(item.activities)
	}

	return 0
}

const normalizeWeekEntry = (item) => {
	if (!item || typeof item !== 'object') {
		return null
	}

	const rawDate = item.date
		?? item.weekStart
		?? item.startDate
		?? item.startDateLocal
		?? item.weekDate
		?? item.key

	const parsedDate = rawDate ? new Date(rawDate) : null
	const mondayDate = parsedDate && !Number.isNaN(parsedDate.getTime())
		? getMondayFromDate(parsedDate)
		: null

	const rawWeek = Number(item.week ?? item.weekNumber ?? item.isoWeek)
	const weekNumber = Number.isFinite(rawWeek) && rawWeek > 0
		? rawWeek
		: (mondayDate ? getIsoWeekNumber(mondayDate) : null)

	if (!mondayDate || !weekNumber) {
		return null
	}

	return {
		key: formatDateKey(mondayDate),
		week: weekNumber,
		value: normalizeDistanceKm(item),
	}
}

const fillMissingWeeks = (series, targetYear) => {
	const normalizedSeries = Array.isArray(series) ? series : []
	const existingByKey = new Map(normalizedSeries.map((entry) => [entry.key, entry]))
	const currentYear = new Date().getFullYear()
	const upperBound = targetYear === currentYear
		? getMondayFromDate(new Date())
		: getLastIsoMondayOfYear(targetYear)

	const completedSeries = []
	for (
		let cursor = getFirstIsoMondayOfYear(targetYear);
		cursor <= upperBound;
		cursor = addDays(cursor, 7)
	) {
		const key = formatDateKey(cursor)
		const existing = existingByKey.get(key)

		completedSeries.push(existing || {
			key,
			week: getIsoWeekNumber(cursor),
			value: 0,
		})
	}

	return completedSeries
}

const extractWeeklyYearSeries = (statsPayload, targetYear) => {
	const responseStats = Array.isArray(statsPayload)
		? statsPayload
		: []

	const weeklyStats = responseStats.filter((stat) => String(stat?.type || '').toUpperCase() === 'WEEKLY')

	if (weeklyStats.length) {
		return fillMissingWeeks(weeklyStats
			.map((stat) => normalizeWeekEntry(stat))
			.filter((entry) => entry && parseDateKey(entry.key)?.getFullYear() === targetYear)
			.sort((left, right) => left.key.localeCompare(right.key)), targetYear)
	}

	const annualStat = responseStats.find((stat) => String(stat?.type || '').toUpperCase() === 'ANNUAL' && Number(stat?.year ?? targetYear) === Number(targetYear))
	if (Array.isArray(annualStat?.activities) && annualStat.activities.length) {
		const grouped = new Map()

		for (const activity of annualStat.activities) {
			const rawDate = activity?.startDateLocal || activity?.startDate
			const parsed = rawDate ? new Date(rawDate) : null
			if (!parsed || Number.isNaN(parsed.getTime())) {
				continue
			}

			if (parsed.getFullYear() !== targetYear) {
				continue
			}

			const mondayDate = getMondayFromDate(parsed)
			const key = formatDateKey(mondayDate)
			const current = grouped.get(key) || {
				key,
				week: getIsoWeekNumber(mondayDate),
				value: 0,
			}

			current.value += (Number(activity?.distance) || 0) / 1000
			grouped.set(key, current)
		}

		return fillMissingWeeks(
			Array.from(grouped.values()).sort((left, right) => left.key.localeCompare(right.key)),
			targetYear,
		)
	}

	return fillMissingWeeks([], targetYear)
}

const loadCurrentYearWeeks = async (dateKey) => {
	const baseDate = parseDateKey(dateKey) || new Date()
	const mondayDate = getMondayFromDate(baseDate)
	const targetYear = getYearFromDateKey(dateKey)

	yearlyWeekSeriesLoading.value = true
	yearlyWeekSeries.value = []

	try {
		const cacheKey = String(targetYear)

		if (!cachedWeeklyStatsByYear.value[cacheKey]) {
			const payload = await fetchStats(formatDateKey(mondayDate), 'WEEKLY').catch(() => null)
			cachedWeeklyStatsByYear.value = {
				...cachedWeeklyStatsByYear.value,
				[cacheKey]: payload,
			}
		}

		const yearlyPayload = cachedWeeklyStatsByYear.value[cacheKey]
		const annualSeries = extractWeeklyYearSeries(yearlyPayload, targetYear)

		if (annualSeries.length) {
			yearlyWeekSeries.value = annualSeries
			return
		}

		const fallbackActivities = extractWeeklyActivities(yearlyPayload)
		yearlyWeekSeries.value = [{
			key: formatDateKey(mondayDate),
			week: getIsoWeekNumber(mondayDate),
			value: summarizeDistanceKm(fallbackActivities),
		}]
	} finally {
		yearlyWeekSeriesLoading.value = false
	}
}

const forwardSelectWeek = (weekKey) => {
	emit('select-week', weekKey)
}

watch(
	() => props.selectedWeekKey,
	(selectedWeekKey) => {
		loadCurrentYearWeeks(selectedWeekKey)
	},
	{ immediate: true },
)
</script>

<template>
	<YearlyWeekComparisonChart
		:weeks="yearlyWeekSeries"
		:year="getYearFromDateKey(props.selectedWeekKey)"
		:selected-week-key="props.selectedWeekKey"
		:is-loading="yearlyWeekSeriesLoading"
		@select-week="forwardSelectWeek"
	/>
</template>
