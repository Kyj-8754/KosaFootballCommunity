<template>
  <div>
    <h2>내 결제 목록</h2>
    <ReservationList :user_no ="userNo"/>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import ReservationList from '@/components/reservation/paymentlist.vue'

const userNo = inject('userNo')  
</script>