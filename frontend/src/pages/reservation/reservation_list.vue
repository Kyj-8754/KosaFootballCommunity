<template>
  <div>
    <h2>내 예약 목록</h2>
    <ReservationList :user_no ="userNo"/>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import ReservationList from '@/components/reservation/reservationlist.vue'

const userNo = inject('userNo')  
</script>