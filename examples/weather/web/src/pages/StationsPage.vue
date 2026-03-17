<template>
  <div>
    <div class="page-header">
      <h1>Stations</h1>
      <button class="btn-primary" @click="showDialog = true">New Station</button>
    </div>
    <table class="data-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Scenario</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="st in stations" :key="st.id">
          <td>{{ st.name }}</td>
          <td>{{ scenarioName(st.scenario) }}</td>
          <td>
            <button class="btn-danger" @click="remove(st.id)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h2>New Station</h2>
        <label>Name<input v-model="form.name" /></label>
        <label>Scenario
          <select v-model="form.scenario">
            <option v-for="sc in scenarios" :key="sc.id" :value="sc.id">{{ sc.name }}</option>
          </select>
        </label>
        <div class="dialog-actions">
          <button class="btn-secondary" @click="closeDialog">Cancel</button>
          <button class="btn-primary" @click="save">Save</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { Station, Scenario } from '@/api/types'
import { fetchStations, createStation, deleteStation, fetchScenarios } from '@/api/http'

const stations = ref<Station[]>([])
const scenarios = ref<Scenario[]>([])
const showDialog = ref(false)
const form = reactive({ name: '', scenario: '' })

onMounted(async () => {
  stations.value = await fetchStations()
  scenarios.value = await fetchScenarios()
})

function scenarioName(id: string): string {
  return scenarios.value.find(s => s.id === id)?.name ?? id
}

function closeDialog() {
  showDialog.value = false
  form.name = ''
  form.scenario = ''
}

async function save() {
  const st = await createStation({ name: form.name, scenario: form.scenario })
  stations.value.push(st)
  closeDialog()
}

async function remove(id: string) {
  await deleteStation(id)
  stations.value = stations.value.filter(s => s.id !== id)
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.data-table th, .data-table td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.dialog { background: #fff; border-radius: 12px; padding: 2rem; min-width: 400px; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }
.dialog h2 { margin-bottom: 1.5rem; }
.dialog label { display: block; margin-bottom: 1rem; color: #475569; }
.dialog input, .dialog select { display: block; width: 100%; margin-top: 0.25rem; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; }
.dialog-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem; }
.btn-primary { background: #3b82f6; color: #fff; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #ef4444; color: #fff; border: none; padding: 0.25rem 0.75rem; border-radius: 4px; cursor: pointer; font-size: 0.85rem; }
</style>
