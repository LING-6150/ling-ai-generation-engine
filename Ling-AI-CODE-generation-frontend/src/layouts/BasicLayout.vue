<template>
  <a-layout class="basic-layout">
    <!-- Header -->
    <a-layout-header class="header">
      <GlobalHeader />
    </a-layout-header>

    <!-- Main Content -->
    <a-layout-content class="main-content">
      <router-view />
    </a-layout-content>

    <!-- Footer -->
    <a-layout-footer class="footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
}

.header {
  padding: 0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
  width: 100%;
}

.footer {
  text-align: center;
  background: #f5f5f5;
  padding: 16px;
}
</style>
