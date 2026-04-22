<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const props = defineProps({
  polylinePoints: {
    type: String,
    default: '',
  },
  stream: {
    type: Array,
    default: () => [],
  },
  altitudeStream: {
    type: Array,
    default: () => [],
  },
  colorGamma: {
    type: Number,
    default: 0.6,
  },
  streamType: {
    type: String,
    default: '',
  },
})

const mapRef = ref(null)
let mapInstance = null
let routeLayer = null

const normalizeAltitudeValues = (stream) => {
  if (!Array.isArray(stream)) {
    return []
  }

  return stream
    .map((value) => Number(value))
    .filter((value) => Number.isFinite(value))
}

const altitudePolylinePoints = computed(() => {
  const values = normalizeAltitudeValues(props.altitudeStream)
  if (values.length < 2) {
    return ''
  }

  const minValue = Math.min(...values)
  const maxValue = Math.max(...values)
  const span = maxValue - minValue

  return values
    .map((value, index) => {
      const x = (index / Math.max(1, values.length - 1)) * 100
      const normalized = span > 0 ? (value - minValue) / span : 0.5
      const y = 100 - (Math.max(0, Math.min(1, normalized)) * 100)
      return `${x.toFixed(2)},${y.toFixed(2)}`
    })
    .join(' ')
})

const altitudeAreaPoints = computed(() => {
  if (!altitudePolylinePoints.value) {
    return ''
  }

  return `0,100 ${altitudePolylinePoints.value} 100,100`
})

const defaultRouteStyle = {
  color: '#2b2621',
  weight: 4,
  opacity: 0.85,
}

const normalizeStreamValues = (stream) => {
  if (!Array.isArray(stream)) {
    return []
  }

  return stream
    .map((value) => Number(value))
    .filter((value) => Number.isFinite(value))
}

const toSegmentColor = (value, minValue, maxValue, streamType) => {
  const span = maxValue - minValue
  const normalized = span > 0 ? (value - minValue) / span : 0.5
  const clamped = Math.max(0, Math.min(1, normalized))
  const safeGamma = Number.isFinite(props.colorGamma) && props.colorGamma > 0 ? props.colorGamma : 1
  const curved = Math.pow(clamped, safeGamma)

  const normalizedType = String(streamType || '').toLowerCase()
  const isCadenceType = normalizedType === 'cadence'
  const colorPosition = isCadenceType ? 1 - curved : curved

  // velocity_smooth/heart*: min -> green, max -> red
  // cadence: min -> red, max -> green
  const red = Math.round(40 + (215 * colorPosition))
  const green = Math.round(190 - (150 * colorPosition))
  const blue = 55

  return `rgb(${red}, ${green}, ${blue})`
}

const getSegmentValues = (pointCount, values) => {
  const segmentCount = Math.max(0, pointCount - 1)

  if (!segmentCount || !values.length) {
    return []
  }

  if (values.length === segmentCount) {
    return values
  }

  if (values.length === pointCount) {
    return Array.from({ length: segmentCount }, (_, index) => {
      const next = Math.min(index + 1, values.length - 1)
      return (values[index] + values[next]) / 2
    })
  }

  return Array.from({ length: segmentCount }, (_, index) => {
    const mappedIndex = Math.round((index / Math.max(1, segmentCount - 1)) * Math.max(0, values.length - 1))
    return values[mappedIndex]
  })
}

const parseCoordinatePairs = (rawValue) => {
  if (!rawValue) {
    return []
  }

  return rawValue
    .trim()
    .split(/\s+/)
    .map((entry) => entry.trim())
    .filter(Boolean)
    .map((entry) => {
      const [latRaw, lngRaw] = entry.split(',')
      const lat = Number(latRaw)
      const lng = Number(lngRaw)

      if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
        return null
      }

      return { lat, lng }
    })
    .filter(Boolean)
}

const decodePolyline = (encoded) => {
  if (!encoded) {
    return []
  }

  const points = []
  let index = 0
  let lat = 0
  let lng = 0

  while (index < encoded.length) {
    let shift = 0
    let result = 0
    let byte

    do {
      byte = encoded.charCodeAt(index++) - 63
      result |= (byte & 0x1f) << shift
      shift += 5
    } while (byte >= 0x20)

    const deltaLat = result & 1 ? ~(result >> 1) : result >> 1
    lat += deltaLat

    shift = 0
    result = 0

    do {
      byte = encoded.charCodeAt(index++) - 63
      result |= (byte & 0x1f) << shift
      shift += 5
    } while (byte >= 0x20)

    const deltaLng = result & 1 ? ~(result >> 1) : result >> 1
    lng += deltaLng

    points.push({ lat: lat / 1e5, lng: lng / 1e5 })
  }

  return points
}

const ensureMap = async () => {
  await nextTick()

  if (mapInstance || !mapRef.value) {
    return
  }

  mapInstance = L.map(mapRef.value, {
    zoomControl: true,
    attributionControl: true,
  }).setView([0, 0], 2)

  L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.png', {
    maxZoom: 20,
    /*
    attribution:
      '&copy; <a href="https://stadiamaps.com/">Stadia Maps</a>, ' +
      '&copy; <a href="https://openmaptiles.org/">OpenMapTiles</a>, ' +
      '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',*/
  }).addTo(mapInstance)
}

const renderRoute = async () => {
  await ensureMap()

  if (!mapInstance) {
    return
  }

  let points = parseCoordinatePairs(props.polylinePoints)

  if (points.length < 2) {
    points = decodePolyline(props.polylinePoints)
  }

  if (routeLayer) {
    mapInstance.removeLayer(routeLayer)
    routeLayer = null
  }

  if (!points.length) {
    mapInstance.setView([0, 0], 2)
    return
  }

  const latLngs = points.map((point) => [point.lat, point.lng])
  const streamValues = normalizeStreamValues(props.stream)
  const segmentValues = getSegmentValues(latLngs.length, streamValues)

  if (!segmentValues.length) {
    routeLayer = L.polyline(latLngs, {
      color: defaultRouteStyle.color,
      weight: defaultRouteStyle.weight,
      opacity: defaultRouteStyle.opacity,
      lineJoin: 'round',
    }).addTo(mapInstance)
  } else {
    const minValue = Math.min(...segmentValues)
    const maxValue = Math.max(...segmentValues)
    const segments = []

    for (let index = 0; index < latLngs.length - 1; index += 1) {
      const value = segmentValues[index]
      segments.push(
        L.polyline([latLngs[index], latLngs[index + 1]], {
          color: toSegmentColor(value, minValue, maxValue, props.streamType),
          weight: defaultRouteStyle.weight,
          opacity: defaultRouteStyle.opacity,
          lineJoin: 'round',
        }),
      )
    }

    routeLayer = L.featureGroup(segments).addTo(mapInstance)
  }

  mapInstance.fitBounds(routeLayer.getBounds(), {
    padding: [16, 16],
  })

  mapInstance.invalidateSize()
}

watch(
  () => [props.polylinePoints, props.stream, props.colorGamma, props.streamType],
  () => {
    renderRoute()
  },
)



onMounted(() => {
  renderRoute()
})

onBeforeUnmount(() => {
  if (!mapInstance) {
    return
  }

  mapInstance.remove()
  mapInstance = null
  routeLayer = null
})
</script>

<template>
  <div class="activity-detail-map-shell">
    <div ref="mapRef" class="activity-detail-map" />

    <div v-if="altitudePolylinePoints" class="altitude-profile-overlay" aria-hidden="true">
      <svg viewBox="0 0 100 100" preserveAspectRatio="none" class="altitude-profile-svg">
        <polygon :points="altitudeAreaPoints" class="altitude-profile-fill" />
        <polyline :points="altitudePolylinePoints" class="altitude-profile-line" />
      </svg>
    </div>
  </div>
</template>
