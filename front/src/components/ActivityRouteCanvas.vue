<script setup>
import { nextTick, onMounted, ref, watch } from 'vue'

const props = defineProps({
  polylinePoints: {
    type: String,
    default: '',
  },
})

const canvasRef = ref(null)
const CANVAS_SIZE = 300
const PADDING = 12

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

const drawPlaceholder = (ctx) => {
  const radius = (CANVAS_SIZE - PADDING * 2) / 2
  const center = CANVAS_SIZE / 2

  ctx.strokeStyle = 'rgba(0, 0, 0, 0.35)'
  ctx.lineWidth = 1.5
  ctx.setLineDash([4, 4])
  ctx.beginPath()
  ctx.arc(center, center, radius, 0, Math.PI * 2)
  ctx.stroke()
  ctx.setLineDash([])
}

const drawRoute = async () => {
  await nextTick()

  const canvas = canvasRef.value
  if (!canvas) {
    return
  }

  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return
  }

  ctx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE)

  let points = parseCoordinatePairs(props.polylinePoints)

  if (points.length < 2) {
    // Fallback for legacy encoded polyline strings.
    points = decodePolyline(props.polylinePoints)
  }

  if (points.length < 2) {
    drawPlaceholder(ctx)
    return
  }

  const lats = points.map((point) => point.lat)
  const lngs = points.map((point) => point.lng)

  const minLat = Math.min(...lats)
  const maxLat = Math.max(...lats)
  const minLng = Math.min(...lngs)
  const maxLng = Math.max(...lngs)

  const latRange = maxLat - minLat
  const lngRange = maxLng - minLng
  const maxRange = Math.max(latRange, lngRange)
  const drawableSize = CANVAS_SIZE - PADDING * 2

  if (maxRange === 0) {
    drawPlaceholder(ctx)
    return
  }

  const scale = drawableSize / maxRange
  const xOffset = PADDING + (drawableSize - lngRange * scale) / 2
  const yOffset = PADDING + (drawableSize - latRange * scale) / 2

  const toPoint = (point) => {
    const x = xOffset + (point.lng - minLng) * scale
    const y = yOffset + (maxLat - point.lat) * scale
    return { x, y }
  }

  const projected = points.map(toPoint)

  ctx.lineJoin = 'round'
  ctx.lineCap = 'round'
  ctx.strokeStyle = 'rgba(0, 0, 0, 0.5)'
  ctx.lineWidth = 5
  ctx.beginPath()

  projected.forEach((point, pointIndex) => {
    if (pointIndex === 0) {
      ctx.moveTo(point.x, point.y)
      return
    }

    ctx.lineTo(point.x, point.y)
  })

  ctx.stroke()

}

watch(
  () => props.polylinePoints,
  () => {
    drawRoute()
  },
)

onMounted(() => {
  drawRoute()
})
</script>

<template>
  <canvas ref="canvasRef" :width="CANVAS_SIZE" :height="CANVAS_SIZE" class="route-canvas" />
</template>
