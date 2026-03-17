export interface Scenario {
  id: string
  name: string
  dsl: string
}

export interface SimulationStatus {
  target: string
  status: string
}

export interface ValidationResult {
  valid: boolean
  error?: string
}
