<template>
  <div>
    <div class="page-header">
      <h1>Simulations</h1>
      <div class="header-actions">
        <button class="btn-primary" @click="onStartAll">Start All</button>
        <button class="btn-danger" @click="onStopAll">Stop All</button>
      </div>
    </div>
    <table class="data-table">
      <thead>
        <tr>
          <th>Station</th>
          <th>Simulation</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="station in stations" :key="station.id">
          <td>{{ station.name }}</td>
          <td>
            <SimulationControl
              :status="simStatus(station.id)"
              @start="onStart(station.id)"
              @stop="onStop(station.id)"
            />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Station, SimulationStatus } from '@/api/types'
import { fetchStations, fetchSimulations, startSimulation, stopSimulation, startAll, stopAll } from '@/api/http'
import { SimulationControl } from '@yarkivaev/simulation-web'

const stations = ref<Station[]>([])
const simulations = ref<SimulationStatus[]>([])

onMounted(async () => {
  stations.value = await fetchStations()
  simulations.value = await fetchSimulations()
})

function simStatus(stationId: string): string {
  return simulations.value.find(s => s.target === stationId)?.status ?? 'STOPPED'
}

async function onStart(stationId: string) {
  await startSimulation(stationId)
  simulations.value = await fetchSimulations()
}

async function onStop(stationId: string) {
  await stopSimulation(stationId)
  simulations.value = await fetchSimulations()
}

async function onStartAll() {
  await startAll()
  simulations.value = await fetchSimulations()
}

async function onStopAll() {
  await stopAll()
  simulations.value = await fetchSimulations()
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.header-actions { display: flex; gap: 0.75rem; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.data-table th, .data-table td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.btn-primary { background: #3b82f6; color: #fff; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #ef4444; color: #fff; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
</style>
