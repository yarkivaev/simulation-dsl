import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  build: {
    lib: {
      entry: resolve(__dirname, 'src/index.ts'),
      formats: ['es'],
      fileName: 'simulation-web',
    },
    rollupOptions: {
      external: ['vue', 'vue-router', 'monaco-editor'],
    },
  },
})
