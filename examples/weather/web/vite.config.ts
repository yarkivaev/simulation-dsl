import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@yarkivaev/simulation-web': resolve(__dirname, '../../../web/src/index.ts'),
    },
    dedupe: ['vue', 'vue-router'],
  },
  server: {
    proxy: {
      '/api': 'http://localhost:7070',
    },
  },
})
