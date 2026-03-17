import { createApp } from 'vue'
import router from './router'
import AppRoot from './AppRoot.vue'

const app = createApp(AppRoot)
app.use(router)
app.mount('#app')
