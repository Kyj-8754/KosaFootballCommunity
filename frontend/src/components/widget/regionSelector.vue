<!-- RegionSelector.vue -->
<template>
  <select
    class="border rounded px-2 py-1 text-sm w-full"
    :value="modelValue"
    @change="$emit('update:modelValue', $event.target.value)"
  >
    <option v-for="r in regions" :key="r" :value="r">{{ r }}</option>
  </select>
</template>

<script setup>
const regions = [
  '강남구', '강동구', '강북구', '강서구', '관악구', '광진구', '구로구', '금천구',
  '노원구', '도봉구', '동대문구', '동작구', '마포구', '서대문구', '서초구',
  '성동구', '성북구', '송파구', '양천구', '영등포구', '용산구',
  '은평구', '종로구', '중구', '중랑구'
]


defineProps({
  modelValue: String
})

defineEmits(['update:modelValue'])
</script>
