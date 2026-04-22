<script setup>
import { API_BASE_URL, API_DOCS_URL, SWAGGER_URL } from '../config/env'

const modules = [
  {
    title: 'health',
    description: 'Chequeo simple de disponibilidad.',
    methods: ['GET /health'],
  },
  {
    title: 'activities',
    description: 'Listado, detalle y datos asociados de actividad.',
    methods: ['GET /activities', 'GET /activities/{id}', 'GET /activities/{id}/streams', 'GET /activities/{id}/stats'],
  },
  {
    title: 'stats',
    description: 'Agregados semanales, mensuales o anuales.',
    methods: ['GET /stats', 'GET /stats/{id}'],
  },
  {
    title: 'streams',
    description: 'Series temporales por tipo y actividad.',
    methods: ['GET /streams', 'GET /streams/{activityId}/{type}'],
  },
]
</script>

<template>
  <section class="stack-lg">
    <article class="panel">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Referencia tecnica</p>
          <h2>Como esta organizada la integracion</h2>
        </div>
      </div>

      <div class="reference-grid">
        <div class="mini-card">
          <span class="mini-card-label">baseURL</span>
          <strong>{{ API_BASE_URL }}</strong>
        </div>
        <div class="mini-card">
          <span class="mini-card-label">swagger</span>
          <a :href="SWAGGER_URL" target="_blank" rel="noreferrer">Abrir Swagger UI</a>
        </div>
        <div class="mini-card">
          <span class="mini-card-label">openapi</span>
          <a :href="API_DOCS_URL" target="_blank" rel="noreferrer">Abrir JSON</a>
        </div>
      </div>

      <div class="module-grid">
        <article v-for="module in modules" :key="module.title" class="module-card">
          <p class="eyebrow">Modulo</p>
          <h3>{{ module.title }}</h3>
          <p>{{ module.description }}</p>
          <ul class="compact-list">
            <li v-for="method in module.methods" :key="method">
              <code>{{ method }}</code>
            </li>
          </ul>
        </article>
      </div>
    </article>
  </section>
</template>
