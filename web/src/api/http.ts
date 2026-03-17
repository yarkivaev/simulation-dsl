import type { Scenario, SimulationStatus, ValidationResult } from './types'

const BASE = '/api'

async function json<T>(response: Response): Promise<T> {
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`)
  }
  return response.json()
}

export async function fetchScenarios(): Promise<Scenario[]> {
  return json(await fetch(`${BASE}/scenarios`))
}

export async function createScenario(scenario: Omit<Scenario, 'id'>): Promise<Scenario> {
  return json(await fetch(`${BASE}/scenarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(scenario),
  }))
}

export async function deleteScenario(id: string): Promise<void> {
  await fetch(`${BASE}/scenarios/${id}`, { method: 'DELETE' })
}

export async function validateScenario(id: string): Promise<ValidationResult> {
  return json(await fetch(`${BASE}/scenarios/${id}/validate`, { method: 'POST' }))
}

export async function fetchSimulations(): Promise<SimulationStatus[]> {
  return json(await fetch(`${BASE}/simulations`))
}

export async function startSimulation(id: string): Promise<void> {
  await fetch(`${BASE}/simulations/${id}/start`, { method: 'POST' })
}

export async function stopSimulation(id: string): Promise<void> {
  await fetch(`${BASE}/simulations/${id}/stop`, { method: 'POST' })
}

export async function startAll(): Promise<void> {
  await fetch(`${BASE}/simulations/start-all`, { method: 'POST' })
}

export async function stopAll(): Promise<void> {
  await fetch(`${BASE}/simulations/stop-all`, { method: 'POST' })
}
