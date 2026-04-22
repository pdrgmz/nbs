import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import ActivitiesView from '../views/ActivitiesView.vue'
import ApiReferenceView from '../views/ApiReferenceView.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: DashboardView,
    meta: {
      title: 'Resumen',
    },
  },
  {
    path: '/activities',
    name: 'activities',
    component: ActivitiesView,
    meta: {
      title: 'Actividades',
    },
  },
  {
    path: '/api',
    name: 'api-reference',
    component: ApiReferenceView,
    meta: {
      title: 'API',
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
