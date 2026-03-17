import type { Station } from './types'

export {
  fetchScenarios,
  createScenario,
  deleteScenario,
  validateScenario,
  fetchSimulations,
  startSimulation,
  stopSimulation,
  startAll,
  stopAll,
} from '@yarkivaev/simulation-web'

const BASE = '/api'

async function json<T>(response: Response): Promise<T> {
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`)
  }
  return response.json()
}

export async function fetchStations(): Promise<Station[]> {
  return json(await fetch(`${BASE}/stations`))
}

export async function createStation(station: Omit<Station, 'id'>): Promise<Station> {
  return json(await fetch(`${BASE}/stations`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(station),
  }))
}

export async function deleteStation(id: string): Promise<void> {
  await fetch(`${BASE}/stations/${id}`, { method: 'DELETE' })
}
