<template>
  <div id="noAuthPage">
    <a-result
      status="403"
      title="403"
      sub-title="Sorry, you are not authorized to access this page."
    >
      <template #extra>
        <a-button type="primary" @click="$router.push('/')">Back Home</a-button>
      </template>
    </a-result>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
#noAuthPage {
  padding: 80px 0;
}
</style>
