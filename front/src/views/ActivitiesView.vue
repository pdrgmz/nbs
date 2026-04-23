<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import ActivityRouteCanvas from '../components/ActivityRouteCanvas.vue'
import ActivityRouteMap from '../components/ActivityRouteMap.vue'
import { fetchActivityStream } from '../api/modules/activities'
import { fetchStats } from '../api/modules/stats'
import { useAsyncState } from '../composables/useAsyncState'

const activitiesRequest = useAsyncState(fetchStats)
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

const currentWeekMonday = getMondayFromDate(new Date())
const selectedWeekMonday = ref(new Date(currentWeekMonday))
const selectedActivityId = ref(null)
const paceEnabled = ref(false)
const cadenceEnabled = ref(false)
const heartRateEnabled = ref(false)
const altitudeStreamType = 'altitude'
const selectedStreamType = ref('')
const streamCache = ref({})

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
  const responseStats = Array.isArray(activitiesRequest.data.value)
    ? activitiesRequest.data.value
    : []

  const weeklyStat = responseStats.find((stat) => stat?.type === 'WEEKLY')

  const weekActivities = Array.isArray(weeklyStat?.activities)
    ? weeklyStat.activities
    : []

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

const getActivityTimestamp = (activity) => {
  const rawDate = activity?.startDateLocal || activity?.startDate
  const parsed = new Date(rawDate)
  const timestamp = parsed.getTime()
  return Number.isFinite(timestamp) ? timestamp : -Infinity
}

const mostRecentActivity = computed(() => {
  if (!flatActivities.value.length) {
    return null
  }

  return flatActivities.value.reduce((latest, current) => {
    if (!latest) {
      return current
    }

    return getActivityTimestamp(current) > getActivityTimestamp(latest) ? current : latest
  }, null)
})

const selectedActivity = computed(() => {
  if (!flatActivities.value.length) {
    return null
  }

  const explicitSelection = flatActivities.value.find((activity) => activity.id === selectedActivityId.value)
  return explicitSelection || mostRecentActivity.value
})

const streamTypeMap = {
  pace: 'velocity_smooth',
  cadence: 'cadence',
  hr: 'heartrate',
}

const streamCacheKey = (activityId, streamType) => `${activityId}:${streamType}`

const currentStream = computed(() => {
  const activityId = selectedActivity.value?.id
  const streamType = selectedStreamType.value

  if (!activityId || !streamType) {
    return []
  }

  return streamCache.value[streamCacheKey(activityId, streamType)] || []
})

const currentAltitudeStream = computed(() => {
  const activityId = selectedActivity.value?.id
  if (!activityId) {
    return []
  }

  return streamCache.value[streamCacheKey(activityId, altitudeStreamType)] || []
})

const setSingleToggle = (toggleKey) => {
  paceEnabled.value = toggleKey === 'pace'
  cadenceEnabled.value = toggleKey === 'cadence'
  heartRateEnabled.value = toggleKey === 'hr'
}

const clearToggles = () => {
  paceEnabled.value = false
  cadenceEnabled.value = false
  heartRateEnabled.value = false
}

const normalizeStreamPayload = (payload) => {
  if (Array.isArray(payload)) {
    return payload
  }

  if (Array.isArray(payload?.data)) {
    return payload.data
  }

  if (typeof payload?.data === 'string') {
    try {
      const parsed = JSON.parse(payload.data)
      if (Array.isArray(parsed)) {
        return parsed
      }
    } catch {
      return []
    }
  }

  if (Array.isArray(payload?.stream)) {
    return payload.stream
  }

  return []
}

const loadStreamForCurrentSelection = async () => {
  const activityId = selectedActivity.value?.id
  const streamType = selectedStreamType.value

  if (!activityId || !streamType) {
    return
  }

  const cacheKey = streamCacheKey(activityId, streamType)
  const cachedStream = streamCache.value[cacheKey]
  if (Array.isArray(cachedStream) && cachedStream.length) {
    return
  }

  try {
    const payload = await fetchActivityStream(activityId, streamType)

    const normalizedStream = normalizeStreamPayload(payload)
    streamCache.value = {
      ...streamCache.value,
      [cacheKey]: normalizedStream,
    }
  } catch {
    // Keep cache unchanged so a future click can retry the request.
  }
}

const loadAltitudeForCurrentSelection = async () => {
  const activityId = selectedActivity.value?.id
  if (!activityId) {
    return
  }

  const cacheKey = streamCacheKey(activityId, altitudeStreamType)
  const cachedStream = streamCache.value[cacheKey]
  if (Array.isArray(cachedStream) && cachedStream.length) {
    return
  }

  try {
    const payload = await fetchActivityStream(activityId, altitudeStreamType)
    const normalizedStream = normalizeStreamPayload(payload)
    streamCache.value = {
      ...streamCache.value,
      [cacheKey]: normalizedStream,
    }
  } catch {
    // Keep cache unchanged so a future selection can retry the request.
  }
}

const handleStreamToggle = async (toggleKey, isChecked) => {
  if (!selectedActivity.value?.id) {
    clearToggles()
    selectedStreamType.value = ''
    return
  }

  if (!isChecked) {
    clearToggles()
    selectedStreamType.value = ''
    return
  }

  setSingleToggle(toggleKey)
  const streamType = streamTypeMap[toggleKey]
  selectedStreamType.value = streamType

  await loadStreamForCurrentSelection()
}

watch(
  () => [selectedActivity.value?.id, selectedStreamType.value],
  () => {
    loadStreamForCurrentSelection()
  },
)

watch(
  () => selectedActivity.value?.id,
  () => {
    loadAltitudeForCurrentSelection()
  },
  { immediate: true },
)

const formatDistance = (distance) => {
  if (typeof distance !== 'number') {
    return '-'
  }

  return `${(distance / 1000).toFixed(1)}km`
}

const formatTime = (value) => {
  if (!value) {
    return '-'
  }

  return new Intl.DateTimeFormat('es-ES', {
    timeStyle: 'short',
  }).format(new Date(value))
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

const loadWeek = (mondayDate) => {
  const dateParam = formatDateKey(mondayDate)
  activitiesRequest.execute(dateParam)
    .then(() => {
      selectedActivityId.value = null
    })
    .catch(() => {})
}

const selectActivity = (activity) => {
  selectedActivityId.value = activity.id
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
        <aside class="activity-detail">
          <div class="activity-detail-hero">
            <ActivityRouteMap
              :polyline-points="selectedActivity?.polylinePoints || ''"
              :stream="currentStream"
              :altitude-stream="currentAltitudeStream"
              :stream-type="selectedStreamType"
              :color-gamma="3"
            />

            <div class="activity-detail-overlay">
              <template v-if="selectedActivity">
                <p>{{ formatDistance(selectedActivity.distance) }}</p>
                <p>{{ selectedActivity.name || `Actividad ${selectedActivity.id}` }}</p>
                <p>{{ formatPace(selectedActivity.averageSpeed) }}</p>
              </template>
              <template v-else>
  
              </template>
            </div>
          </div>
        </aside>

        <section class="route-switches-shell" aria-label="Opciones de color de ruta">
          
          <div class="route-switches-grid">
            <label class="route-switch-item">
              <span>PACE</span>
              <input
                type="checkbox"
                :checked="paceEnabled"
                @change="handleStreamToggle('pace', $event.target.checked)"
              />
            </label>
            <label class="route-switch-item">
              <span>CADENCE</span>
              <input
                type="checkbox"
                :checked="cadenceEnabled"
                @change="handleStreamToggle('cadence', $event.target.checked)"
              />
            </label>
            <label class="route-switch-item">
              <span>HR</span>
              <input
                type="checkbox"
                :checked="heartRateEnabled"
                @change="handleStreamToggle('hr', $event.target.checked)"
              />
            </label>
          </div>
        </section>

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
              :class="{ 'is-selected-day': day.activities.some((activity) => activity.id === selectedActivity?.id) }"
            >
             

              <div v-if="!day.activities.length" class="day-empty">
                
              </div>

              <ul v-else class="day-activities">
                <li
                  v-for="activity in day.activities"
                  :key="activity.id"
                  class="activity-card"
                  :class="{ 'is-selected': selectedActivity?.id === activity.id }"
                  @click="selectActivity(activity)"
                >
                  <div class="activity-route">
                    <ActivityRouteCanvas 
                    :polyline-points="activity.polylinePoints"
                     />
                    <h2 class="activity-metrics">{{ formatDistance(activity.distance) }} <br> {{ formatPace(activity.averageSpeed) }}</h2>
                  </div>
                </li>
              </ul>
              <header class="day-column">
                <p class="day-date">{{ day.label }}</p>
              </header>
            </section>

            <button v-if="!isCurrentWeek" class="week-nav week-nav-inline" type="button" @click="goToNextWeek" aria-label="Semana siguiente">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M9.5 5.5L16 12l-6.5 6.5" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </article>
  </section>
</template>
