<template>
  <SimulationLayout title="Weather Simulator" :nav="nav">
    <router-view />
  </SimulationLayout>
</template>

<script setup lang="ts">
import { SimulationLayout } from '@yarkivaev/simulation-web'

const nav = [
  { label: 'Stations', to: '/' },
  { label: 'Scenarios', to: '/scenarios' },
  { label: 'Simulations', to: '/simulations' },
]
</script>
