<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentActivities: {
    type: Array,
    default: () => [],
  },
  previousActivities: {
    type: Array,
    default: () => [],
  },
})

const summarizeWeek = (activities) => {
  const totalDistance = activities.reduce((sum, activity) => sum + (Number(activity?.distance) || 0), 0)
  const totalTime = activities.reduce((sum, activity) => {
    const movingTime = Number(activity?.movingTime)
    const elapsedTime = Number(activity?.elapsedTime)
    return sum + (Number.isFinite(movingTime) && movingTime > 0 ? movingTime : (Number.isFinite(elapsedTime) ? elapsedTime : 0))
  }, 0)

  const averageSpeed = totalTime > 0 ? totalDistance / totalTime : 0

  return {
    activitiesCount: activities.length,
    totalDistance,
    totalTime,
    averageSpeed,
  }
}

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

  return `${hours}h ${minutes.toString().padStart(2, '0')}m`
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

const toPercent = (current, previous) => {
  if (!previous || previous === 0) {
    return null
  }

  return ((current - previous) / previous) * 100
}

const buildTrend = (current, previous, lowerIsBetter = false) => {
  const deltaPercent = toPercent(current, previous)
  if (deltaPercent === null) {
    return {
      tone: 'neutral',
      symbol: '∅',
      detail: '-',
    }
  }

  if (Math.abs(deltaPercent) < 0.5) {
    return {
      tone: 'neutral',
      symbol: '→',
      detail: `${deltaPercent.toFixed(1)}%`,
    }
  }

  const improved = lowerIsBetter ? deltaPercent < 0 : deltaPercent > 0

  return {
    tone: improved ? 'up' : 'down',
    symbol: improved ? '↑' : '↓',
    detail: `${deltaPercent > 0 ? '+' : ''}${deltaPercent.toFixed(1)}%`,
  }
}

const buildPaceTrend = (currentSpeed, previousSpeed) => {
  if (typeof currentSpeed !== 'number' || currentSpeed <= 0 || typeof previousSpeed !== 'number' || previousSpeed <= 0) {
    return {
      tone: 'neutral',
      symbol: '∅',
      detail: '-',
    }
  }

  const currentPaceSeconds = 1000 / currentSpeed
  const previousPaceSeconds = 1000 / previousSpeed
  const deltaSeconds = Math.round(currentPaceSeconds - previousPaceSeconds)

  if (Math.abs(deltaSeconds) < 1) {
    return {
      tone: 'neutral',
      symbol: '→',
      detail: '0s',
    }
  }

  const improved = deltaSeconds < 0

  return {
    tone: improved ? 'up' : 'down',
    symbol: improved ? '↑' : '↓',
    detail: `${deltaSeconds > 0 ? '+' : ''}${deltaSeconds}s`,
  }
}

const formatDurationDelta = (seconds) => {
  const absoluteSeconds = Math.abs(Math.round(seconds))

  if (absoluteSeconds < 60) {
    return `${absoluteSeconds}s`
  }

  const totalMinutes = Math.floor(absoluteSeconds / 60)
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60

  if (hours === 0) {
    return `${totalMinutes}m`
  }

  return `${hours}h ${minutes.toString().padStart(2, '0')}m`
}

const buildTimeTrend = (currentTime, previousTime) => {
  if (typeof currentTime !== 'number' || currentTime <= 0 || typeof previousTime !== 'number' || previousTime <= 0) {
    return {
      tone: '',
      symbol: '',
      detail: '-',
    }
  }

  const deltaSeconds = Math.round(currentTime - previousTime)

  if (Math.abs(deltaSeconds) < 1) {
    return {
      tone: '',
      symbol: '',
      detail: '0s',
    }
  }

  return {
    tone: '',
    symbol: '',
    detail: `${deltaSeconds > 0 ? '+' : '-'}${formatDurationDelta(deltaSeconds)}`,
  }
}

const currentWeekSummary = computed(() => summarizeWeek(props.currentActivities))
const previousWeekSummary = computed(() => summarizeWeek(props.previousActivities))

const weeklyComparisonCards = computed(() => {
  const current = currentWeekSummary.value
  const previous = previousWeekSummary.value

  return [
    {
      key: 'distance',
      currentValue: formatDistance(current.totalDistance),
      previousValue: formatDistance(previous.totalDistance),
      trend: buildTrend(current.totalDistance, previous.totalDistance),
    },
    {
      key: 'time',
      currentValue: formatDuration(current.totalTime),
      previousValue: formatDuration(previous.totalTime),
      trend: buildTimeTrend(current.totalTime, previous.totalTime),
    },
    {
      key: 'pace',
      currentValue: formatPace(current.averageSpeed),
      previousValue: formatPace(previous.averageSpeed),
      trend: buildPaceTrend(current.averageSpeed, previous.averageSpeed),
    },
    {
      key: 'activities',
      currentValue: String(current.activitiesCount),
      previousValue: String(previous.activitiesCount),
      trend: buildTrend(current.activitiesCount, previous.activitiesCount),
    },
  ]
})
</script>

<template>
  <section class="week-summary-shell" aria-label="Summary">
    <article
      v-for="card in weeklyComparisonCards"
      :key="card.key"
      class="week-summary-card"
    >
      
      <h3>{{ card.currentValue }}</h3>
      <p class="week-summary-meta" title="Previous">↺ {{ card.previousValue }}</p>
      <span class="week-summary-trend" :class="card.trend.tone ? `is-${card.trend.tone}` : ''">
        <template v-if="card.trend.symbol">{{ card.trend.symbol }} </template>{{ card.trend.detail }}
      </span>
    </article>
  </section>
</template>
