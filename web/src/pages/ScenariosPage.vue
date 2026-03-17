<template>
  <div>
    <div class="page-header">
      <h1>Scenarios</h1>
      <button class="btn-primary" @click="showDialog = true">New Scenario</button>
    </div>
    <table class="data-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sc in scenarios" :key="sc.id">
          <td>{{ sc.name }}</td>
          <td>
            <button class="btn-secondary" @click="editScenario(sc)">Edit</button>
            <button class="btn-danger" @click="remove(sc.id)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog wide">
        <h2>{{ editing ? 'Edit' : 'New' }} Scenario</h2>
        <label>Name<input v-model="form.name" /></label>
        <label>DSL</label>
        <DslEditor v-model="form.dsl" />
        <div v-if="validation" class="validation" :class="{ error: !validation.valid }">
          {{ validation.valid ? 'Valid' : validation.error }}
        </div>
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
import type { Scenario, ValidationResult } from '../api/types'
import { fetchScenarios, createScenario, deleteScenario, validateScenario } from '../api/http'
import DslEditor from '../components/DslEditor.vue'

const scenarios = ref<Scenario[]>([])
const showDialog = ref(false)
const editing = ref(false)
const validation = ref<ValidationResult | null>(null)
const form = reactive({ name: '', dsl: '' })

onMounted(async () => {
  scenarios.value = await fetchScenarios()
})

function editScenario(sc: Scenario) {
  form.name = sc.name
  form.dsl = sc.dsl
  editing.value = true
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
  editing.value = false
  validation.value = null
  form.name = ''
  form.dsl = ''
}

async function save() {
  const sc = await createScenario({ name: form.name, dsl: form.dsl })
  scenarios.value.push(sc)
  closeDialog()
}

async function remove(id: string) {
  await deleteScenario(id)
  scenarios.value = scenarios.value.filter(s => s.id !== id)
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.data-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.data-table th, .data-table td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #e2e8f0; }
.data-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.dialog { background: #fff; border-radius: 12px; padding: 2rem; min-width: 400px; box-shadow: 0 8px 30px rgba(0,0,0,0.12); }
.dialog.wide { min-width: 700px; max-width: 900px; }
.dialog h2 { margin-bottom: 1.5rem; }
.dialog label { display: block; margin-bottom: 1rem; color: #475569; }
.dialog input { display: block; width: 100%; margin-top: 0.25rem; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; }
.dialog-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem; }
.validation { padding: 0.5rem; border-radius: 4px; margin-top: 0.5rem; background: #f0fdf4; color: #15803d; }
.validation.error { background: #fef2f2; color: #dc2626; }
.btn-primary { background: #3b82f6; color: #fff; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
.btn-secondary { background: #e2e8f0; color: #475569; border: none; padding: 0.5rem 1.25rem; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #ef4444; color: #fff; border: none; padding: 0.25rem 0.75rem; border-radius: 4px; cursor: pointer; font-size: 0.85rem; margin-left: 0.5rem; }
</style>
