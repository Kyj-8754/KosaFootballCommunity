<template>
  <div>
    <h2>결제가 실패했거나 취소되었습니다.</h2>
  </div>
</template>

<script setup>
onMounted(() => {
  alert("결제가 실패하거나 취소되었습니다.");
  setTimeout(() => {
    window.close();
  }, 1500);
});
</script>