<template>
  <div class="home-page">
    <HeroSection id="home" />
    <SkillsSection id="skills" />
    <ContactSection id="contact" />
  </div>
</template>

<script setup>
import HeroSection from '../components/HeroSection.vue'
import SkillsSection from '../components/SkillsSection.vue'
import ContactSection from '../components/ContactSection.vue'
</script>

<style scoped>
.home-page {
  padding-top: 80px;
}
</style>