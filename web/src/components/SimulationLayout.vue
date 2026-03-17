<template>
  <div class="app">
    <nav class="sidebar">
      <h2 class="logo">{{ title }}</h2>
      <ul class="nav-list">
        <li v-for="item in nav" :key="item.to">
          <router-link :to="item.to">{{ item.label }}</router-link>
        </li>
      </ul>
    </nav>
    <main class="content">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  nav: Array<{ label: string; to: string }>
}>()
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f4f6f9;
  color: #333;
}

.app {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: #1e293b;
  color: #fff;
  padding: 1.5rem 1rem;
  flex-shrink: 0;
}

.logo {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #334155;
}

.nav-list {
  list-style: none;
}

.nav-list li {
  margin-bottom: 0.5rem;
}

.nav-list a {
  color: #94a3b8;
  text-decoration: none;
  display: block;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  transition: background 0.2s, color 0.2s;
}

.nav-list a:hover,
.nav-list a.router-link-active {
  background: #334155;
  color: #fff;
}

.content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}
</style>
