<script setup>
defineProps({
  links: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <header class="app-header">
    <div>
      <p class="eyebrow">NBS Front</p>
   
    </div>

    <nav class="app-nav" aria-label="Navegacion principal">
      <RouterLink
        v-for="link in links"
        :key="link.to"
        :to="link.to"
        class="nav-link"
      >
        {{ link.label }}
      </RouterLink>
    </nav>
  </header>
</template>
