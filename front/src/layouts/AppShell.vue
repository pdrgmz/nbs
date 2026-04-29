<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'
import TokenPanel from '../components/TokenPanel.vue'

const links = [
  { to: '/', label: 'Actividades' },
]

const route = useRoute()
const isFullScreenRoute = computed(() => route.name === 'activity-detail')
</script>

<template>
  <div class="shell-bg">
    <div class="shell-grid" :class="{ 'shell-grid-full': isFullScreenRoute }">
      <main class="content-grid" :class="{ 'content-grid-full': isFullScreenRoute }">
        <RouterView />
        <!--<TokenPanel />-->
      </main>
    </div>
  </div>
</template>
