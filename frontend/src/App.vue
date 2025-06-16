<template>
  <div class="container">
      <Header />
      <div class="row">
      <div class="col-md-2">
        <NavArea />
      </div>
      <div class="col-md-10">
        <router-view />
      </div>
    </div>
     <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import NavArea from './components/NavArea.vue';
import Footer from './components/Footer.vue';
</script>
