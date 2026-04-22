<script setup>
import { computed, onMounted } from 'vue'
import { fetchHealth } from '../api/modules/health'
import { API_BASE_URL, API_DOCS_URL, SWAGGER_URL } from '../config/env'
import { useAsyncState } from '../composables/useAsyncState'

const endpoints = [
  { name: 'Health', path: '/health', note: 'Comprobacion de disponibilidad' },
  { name: 'Activities', path: '/activities', note: 'Listado de actividades' },
  { name: 'Stats', path: '/stats', note: 'Estadisticas agregadas' },
  { name: 'Streams', path: '/streams', note: 'Series por actividad o tipo' },
  { name: 'Athlete', path: '/athletes/{id}', note: 'Detalle de deportista' },
]

const healthRequest = useAsyncState(fetchHealth)

const healthText = computed(() => {
  if (typeof healthRequest.data.value === 'string') {
    return healthRequest.data.value
  }

  if (healthRequest.data.value) {
    return JSON.stringify(healthRequest.data.value)
  }

  return 'Sin comprobar'
})

onMounted(() => {
  healthRequest.execute().catch(() => {})
})
</script>

<template>
  <section class="stack-lg">
    <article class="hero-card">
      <div class="hero-copy">
        <p class="eyebrow">Arquitectura inicial</p>
        <h2>Frontend preparado para consumir la API de NBS</h2>
        <p>
          Base URL configurada en entorno, cliente HTTP centralizado, router listo y una capa de integracion separada por modulos.
        </p>
      </div>

      <dl class="hero-metrics">
        <div>
          <dt>Base URL</dt>
          <dd>{{ API_BASE_URL }}</dd>
        </div>
        <div>
          <dt>Swagger</dt>
          <dd><a :href="SWAGGER_URL" target="_blank" rel="noreferrer">Abrir UI</a></dd>
        </div>
        <div>
          <dt>OpenAPI</dt>
          <dd><a :href="API_DOCS_URL" target="_blank" rel="noreferrer">Ver spec</a></dd>
        </div>
      </dl>
    </article>

    <div class="dashboard-grid">
      <article class="panel">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Salud del servicio</p>
            <h2>Estado de la API</h2>
          </div>
          <span class="status-chip" :class="healthRequest.error.value ? 'danger' : 'success'">
            {{ healthRequest.error.value ? 'Con incidencia' : 'Operativa' }}
          </span>
        </div>

        <p class="panel-copy">
          Se prueba automaticamente `GET /health` al entrar en esta vista.
        </p>

        <div class="callout">
          <strong>Respuesta</strong>
          <code>{{ healthText }}</code>
        </div>

        <p v-if="healthRequest.error.value" class="error-text">
          {{ healthRequest.error.value }}
        </p>

        <button class="button secondary" type="button" @click="healthRequest.execute().catch(() => {})">
          Reintentar
        </button>
      </article>

      <article class="panel">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Mapa funcional</p>
            <h2>Endpoints base</h2>
          </div>
        </div>

        <ul class="endpoint-list">
          <li v-for="endpoint in endpoints" :key="endpoint.path">
            <div>
              <strong>{{ endpoint.name }}</strong>
              <p>{{ endpoint.note }}</p>
            </div>
            <code>{{ endpoint.path }}</code>
          </li>
        </ul>
      </article>
    </div>
  </section>
</template>
