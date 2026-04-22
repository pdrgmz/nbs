<script setup>
import { ref } from 'vue'
import { getStoredToken, setStoredToken } from '../api/http'

const token = ref(getStoredToken())
const feedback = ref('')

const saveToken = () => {
  setStoredToken(token.value)
  feedback.value = token.value.trim()
    ? 'Token guardado en localStorage para las siguientes peticiones.'
    : 'Token eliminado.'
}
</script>

<template>
  <section class="panel token-panel">
    <div class="section-heading">
      <div>
        <p class="eyebrow">profile</p>
        <h2>usuario</h2>
      </div>
      <span class="status-chip muted">VE</span>
    </div>

    <p class="panel-copy">
      El OpenAPI declara `bearerAuth`. Guarda aqui tu JWT para que el cliente Axios lo anada automaticamente en cada request.
    </p>

    
  </section>
</template>
