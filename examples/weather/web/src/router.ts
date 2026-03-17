import { createRouter, createWebHistory } from 'vue-router'
import { ScenariosPage } from '@yarkivaev/simulation-web'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: () => import('./pages/StationsPage.vue') },
    { path: '/scenarios', component: ScenariosPage },
    { path: '/simulations', component: () => import('./pages/SimulationsPage.vue') },
  ],
})

export default router
