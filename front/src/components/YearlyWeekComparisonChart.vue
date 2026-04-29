<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  weeks: {
    type: Array,
    default: () => [],
  },
  year: {
    type: [Number, String],
    default: '',
  },
  selectedWeekKey: {
    type: String,
    default: '',
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
})
const emit = defineEmits(['select-week'])

const plotRef = ref(null)
const tooltip = ref({
  visible: false,
  text: '',
  x: 0,
  y: 0,
})

const maxValue = computed(() => {
  const peak = props.weeks.reduce((max, item) => {
    const current = Number(item?.value) || 0
    return current > max ? current : max
  }, 0)

  return peak > 0 ? peak : 1
})

const bars = computed(() => {
  return props.weeks.map((item) => {
    const value = Number(item?.value) || 0
    const height = Math.max((value / maxValue.value) * 100, 2)

    return {
      key: item.key,
      week: item.week,
      value,
      height,
      active: item.key === props.selectedWeekKey,
    }
  })
})

const barsTrackWidth = computed(() => {
  if (!bars.value.length) {
    return '100%'
  }

  return `max(100%, ${bars.value.length * 16}px)`
})

const selectWeek = (weekKey) => {
  if (!weekKey) {
    return
  }

  emit('select-week', weekKey)
}

const updateTooltip = (bar, event) => {
  if (!plotRef.value) {
    return
  }

  const plotRect = plotRef.value.getBoundingClientRect()
  const targetRect = event?.currentTarget?.getBoundingClientRect()
  const pointX = event?.clientX ?? (targetRect ? targetRect.left + (targetRect.width / 2) : plotRect.left)
  const pointY = event?.clientY ?? (targetRect ? targetRect.top : plotRect.top)

  tooltip.value = {
    visible: true,
    text: `W${bar.week} | ${bar.value.toFixed(1)} km`,
    x: Math.max(pointX - plotRect.left, 20),
    y: Math.max(pointY - plotRect.top - 12, 10),
  }
}

const hideTooltip = () => {
  tooltip.value.visible = false
}
</script>

<template>
  <section class="year-weeks-shell" aria-label="Year weeks comparison">
    <p class="year-weeks-head" :title="String(props.year || '')">{{ props.year }}</p>

    <div v-if="isLoading" class="year-weeks-loading">...</div>

    <div v-else ref="plotRef" class="year-weeks-plot">
      <div class="year-weeks-scroll">
        <div class="year-weeks-bars" :style="{ width: barsTrackWidth }" role="img" aria-label="Weekly distance bars">
          <button
            v-for="bar in bars"
            :key="bar.key"
            type="button"
            class="year-weeks-bar-hit"
            :title="`W${bar.week} | ${bar.value.toFixed(1)} km`"
            @click="selectWeek(bar.key)"
            @mouseenter="updateTooltip(bar, $event)"
            @mousemove="updateTooltip(bar, $event)"
            @mouseleave="hideTooltip"
            @focus="updateTooltip(bar, $event)"
            @blur="hideTooltip"
          >
            <span
              class="year-weeks-bar"
              :class="{ 'is-active': bar.active }"
              :style="{ height: `${bar.height}%` }"
            />
          </button>
        </div>
      </div>

      <div
        v-if="tooltip.visible"
        class="year-weeks-tooltip"
        :style="{ left: `${tooltip.x}px`, top: `${tooltip.y}px` }"
      >
        {{ tooltip.text }}
      </div>
    </div>
  </section>
</template>
