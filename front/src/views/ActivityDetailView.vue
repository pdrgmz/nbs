<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ActivityRouteMap from '../components/ActivityRouteMap.vue'
import { fetchActivityById, fetchActivityStream } from '../api/modules/activities'
import { useAsyncState } from '../composables/useAsyncState'

const route = useRoute()
const activityRequest = useAsyncState(fetchActivityById)
const altitudeStream = ref([])

const activityId = computed(() => String(route.params.id || '').trim())
const activity = computed(() => activityRequest.data.value)

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

const loadActivity = async () => {
  if (!activityId.value) {
    return
  }

  await activityRequest.execute(activityId.value)
}

const loadAltitude = async () => {
  if (!activityId.value) {
    altitudeStream.value = []
    return
  }

  try {
    const payload = await fetchActivityStream(activityId.value, 'altitude')
    altitudeStream.value = normalizeStreamPayload(payload)
  } catch {
    altitudeStream.value = []
  }
}

const formatDistance = (distance) => {
  if (typeof distance !== 'number') {
    return '-'
  }

  return `${(distance / 1000).toFixed(1)}km`
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

watch(
  activityId,
  async () => {
    await loadActivity()
    await loadAltitude()
  },
)

onMounted(async () => {
  await loadActivity()
  await loadAltitude()
})
</script>

<template>
  <section class="activity-detail-screen">
    <p v-if="activityRequest.isLoading.value" class="muted-copy">Cargando actividad...</p>
    <p v-else-if="activityRequest.error.value" class="error-text">{{ activityRequest.error.value }}</p>

    <div v-else-if="activity" class="activity-detail-screen-map">
      <ActivityRouteMap
        :polyline-points="activity.polylinePoints || ''"
        :stream="[]"
        :altitude-stream="altitudeStream"
        stream-type=""
        :color-gamma="3"
      />

      <div class="activity-detail-overlay activity-detail-overlay-screen">
        <p>{{ formatDistance(activity.distance) }}</p>
        <p>{{ activity.name || `Actividad ${activity.id}` }}</p>
        <p>{{ formatPace(activity.averageSpeed) }}</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.activity-detail-screen {
  width: 100%;
  min-height: 100dvh;
}

.activity-detail-screen-map {
  position: relative;
  width: 100%;
  min-height: 100dvh;
  height: 100dvh;
}

.activity-detail-screen-map :deep(.activity-detail-map-shell),
.activity-detail-screen-map :deep(.activity-detail-map) {
  width: 100%;
  height: 100%;
}

.activity-detail-overlay-screen {
  position: absolute;
  right: 24px;
  bottom: 24px;
  z-index: 600;
  display: grid;
  gap: 6px;
  justify-items: end;
}
</style>
