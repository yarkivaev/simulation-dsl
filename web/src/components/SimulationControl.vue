<template>
  <span class="simulation-control">
    <span class="status-badge" :class="status">{{ status }}</span>
    <button
      v-if="status !== 'RUNNING'"
      class="btn-primary btn-sm"
      @click="$emit('start')"
    >Start</button>
    <button
      v-else
      class="btn-danger btn-sm"
      @click="$emit('stop')"
    >Stop</button>
  </span>
</template>

<script setup lang="ts">
defineProps<{ status: string }>()
defineEmits<{
  start: []
  stop: []
}>()
</script>

<style scoped>
.simulation-control {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
}
.status-badge { padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; font-weight: 600; }
.status-badge.RUNNING { background: #dcfce7; color: #166534; }
.status-badge.STOPPED { background: #f1f5f9; color: #64748b; }
.btn-primary { background: #3b82f6; color: #fff; border: none; padding: 0.25rem 0.75rem; border-radius: 6px; cursor: pointer; font-size: 0.85rem; }
.btn-danger { background: #ef4444; color: #fff; border: none; padding: 0.25rem 0.75rem; border-radius: 6px; cursor: pointer; font-size: 0.85rem; }
</style>
