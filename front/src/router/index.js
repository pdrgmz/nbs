import { createRouter, createWebHistory } from 'vue-router'
import ActivitiesView from '../views/ActivitiesView.vue'
import ActivityDetailView from '../views/ActivityDetailView.vue'

const routes = [
  {
    path: '/',
    name: 'activities',
    component: ActivitiesView,
    meta: {
      title: 'Actividades',
    },
  },
  {
    path: '/activities',
    redirect: '/',
  },
  {
    path: '/activities/:id',
    name: 'activity-detail',
    component: ActivityDetailView,
    meta: {
      title: 'Detalle de actividad',
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
