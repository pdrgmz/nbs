<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ActivityRouteCanvas from '../components/ActivityRouteCanvas.vue'
import WeeklySummaryComparison from '../components/WeeklySummaryComparison.vue'
import YearlyWeekComparisonSection from '../components/YearlyWeekComparisonSection.vue'
import { fetchStats } from '../api/modules/stats'
import { useAsyncState } from '../composables/useAsyncState'

const activitiesRequest = useAsyncState(fetchStats)
const previousWeekRequest = useAsyncState(fetchStats)
const router = useRouter()
const dayNames = ['Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo']

const getMondayFromDate = (dateValue) => {
  const date = new Date(dateValue)
  date.setHours(0, 0, 0, 0)

  const jsDay = date.getDay()
  const mondayOffset = jsDay === 0 ? -6 : 1 - jsDay
  date.setDate(date.getDate() + mondayOffset)

  return date
}

const formatDateKey = (dateValue) => dateValue.toLocaleDateString('sv-SE')

const addDays = (dateValue, days) => {
  const next = new Date(dateValue)
  next.setDate(next.getDate() + days)
  return next
}

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

const currentWeekMonday = getMondayFromDate(new Date())
const selectedWeekMonday = ref(new Date(currentWeekMonday))

const extractWeeklyActivities = (statsPayload) => {
  const responseStats = Array.isArray(statsPayload)
    ? statsPayload
    : []

  const weeklyStat = responseStats.find((stat) => stat?.type === 'WEEKLY')
  return Array.isArray(weeklyStat?.activities) ? weeklyStat.activities : []
}

const normalizeDateKey = (value) => {
  if (!value) {
    return ''
  }

  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    return ''
  }

  return parsed.toLocaleDateString('sv-SE')
}

const weekDays = computed(() => {
  const monday = selectedWeekMonday.value

  return dayNames.map((name, index) => {
    const current = addDays(monday, index)

    return {
      name,
      date: current,
      dateKey: formatDateKey(current),
      label: current.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
      }),
    }
  })
})

const activitiesByWeekday = computed(() => {
  const weekActivities = extractWeeklyActivities(activitiesRequest.data.value)

  return weekDays.value.map((weekday) => {
    const activities = weekActivities.filter((activity) => {
      const dateKey = normalizeDateKey(activity.startDateLocal || activity.startDate)
      return dateKey === weekday.dateKey
    })

    return {
      ...weekday,
      activities,
    }
  })
})

const flatActivities = computed(() => {
  return activitiesByWeekday.value.flatMap((day) => day.activities)
})

const previousWeekActivities = computed(() => {
  return extractWeeklyActivities(previousWeekRequest.data.value)
})

const formatDistance = (distance) => {
  if (typeof distance !== 'number') {
    return '-'
  }

  return `${(distance / 1000).toFixed(1)}km`
}

const formatDuration = (seconds) => {
  if (typeof seconds !== 'number' || seconds <= 0) {
    return '-'
  }

  const totalMinutes = Math.round(seconds / 60)
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60

  if (hours === 0) {
    return `${minutes}m`
  }

  return `${hours}h${minutes.toString().padStart(2, '0')}m`
}

const formatPace = (speed) => {
  if (typeof speed !== 'number' || speed <= 0) {
    return '-'
  }

  const paceSeconds = 1000 / speed
  const minutes = Math.floor(paceSeconds / 60)
  const seconds = Math.round(paceSeconds % 60)

  return `${minutes}:${seconds.toString().padStart(2, '0')}/km`
}

const isCurrentWeek = computed(() => {
  return formatDateKey(selectedWeekMonday.value) === formatDateKey(currentWeekMonday)
})

const selectedWeekKey = computed(() => formatDateKey(selectedWeekMonday.value))

const loadWeek = (mondayDate) => {
  const dateParam = formatDateKey(mondayDate)
  const previousWeekDate = formatDateKey(addDays(mondayDate, -7))

  Promise.all([
    activitiesRequest.execute(dateParam),
    previousWeekRequest.execute(previousWeekDate),
  ]).catch(() => {})
}

const openActivityDetail = (activity) => {
  if (!activity?.id) {
    return
  }

  router.push(`/activities/${activity.id}`)
}

const goToPreviousWeek = () => {
  selectedWeekMonday.value = addDays(selectedWeekMonday.value, -7)
  loadWeek(selectedWeekMonday.value)
}

const goToNextWeek = () => {
  if (isCurrentWeek.value) {
    return
  }

  selectedWeekMonday.value = addDays(selectedWeekMonday.value, 7)
  loadWeek(selectedWeekMonday.value)
}

const jumpToWeek = (weekKey) => {
  const parsed = parseDateKey(weekKey)
  if (!parsed) {
    return
  }

  const monday = getMondayFromDate(parsed)
  selectedWeekMonday.value = monday
  loadWeek(monday)
}

onMounted(() => {
  loadWeek(selectedWeekMonday.value)
})
</script>

<template>
  <section class="week-view">
    <article class="week-shell">
    

      <p v-if="activitiesRequest.isLoading.value" class="muted-copy">Cargando actividades...</p>
      <p v-else-if="activitiesRequest.error.value" class="error-text">{{ activitiesRequest.error.value }}</p>

      <div v-else class="activities-layout">
        <WeeklySummaryComparison
          :current-activities="flatActivities"
          :previous-activities="previousWeekActivities"
        />

        <div class="week-rows">
          <div class="week-days-track">
            <button class="week-nav week-nav-inline" type="button" @click="goToPreviousWeek" aria-label="Semana anterior">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M14.5 5.5L8 12l6.5 6.5" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" />
              </svg>
            </button>

            <section
              v-for="day in activitiesByWeekday"
              :key="day.dateKey"
              class="day-row"
            >
             

              <div v-if="!day.activities.length" class="day-empty">
                
              </div>

              <ul v-else class="day-activities">
                <li
                  v-for="activity in day.activities"
                  :key="activity.id"
                  class="activity-card"
                  @click="openActivityDetail(activity)"
                >
                  <div class="activity-route">
                    <ActivityRouteCanvas 
                    :polyline-points="activity.polylinePoints"
                     />
                    <h2 class="activity-metrics">
                      {{ formatDistance(activity.distance) }}
                      <br>
                      {{ formatPace(activity.averageSpeed) }}
                      <br>
                      {{ formatDuration(activity.movingTime || activity.elapsedTime) }}
                    </h2>
                  </div>
                </li>
              </ul>
              <header class="day-column">
                <p class="day-date">{{ day.label }}</p>
              </header>
            </section>

            <button
              class="week-nav week-nav-inline"
              type="button"
              :disabled="isCurrentWeek"
              :aria-disabled="isCurrentWeek"
              @click="goToNextWeek"
              aria-label="Semana siguiente"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M9.5 5.5L16 12l-6.5 6.5" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" />
              </svg>
            </button>
          </div>
        </div>

        <YearlyWeekComparisonSection
          :selected-week-key="selectedWeekKey"
          @select-week="jumpToWeek"
        />
      </div>
    </article>
  </section>
</template>
