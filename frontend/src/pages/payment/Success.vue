<!-- PaymentSuccess.vue -->
<template>
  <div>
    <h2>결제가 완료되었습니다.</h2>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

onMounted(() => {
  window.opener.postMessage("paymentSuccess", "*");
  setTimeout(() => {
    window.close();  // 1~2초 후 자동 종료
  }, 1500);
});
</script>