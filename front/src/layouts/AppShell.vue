<script setup>
import AppHeader from '../components/AppHeader.vue'
import TokenPanel from '../components/TokenPanel.vue'

const links = [
  { to: '/', label: 'Resumen' },
  { to: '/activities', label: 'Actividades' },
  { to: '/api', label: 'API' },
]
</script>

<template>
  <div class="shell-bg">
    <div class="shell-grid">
      <main class="content-grid">
        <RouterView />
        <!--<TokenPanel />-->
      </main>
    </div>
  </div>
</template>
