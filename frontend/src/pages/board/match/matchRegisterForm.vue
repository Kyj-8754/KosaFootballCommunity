<template>
  <div class="container mt-4">
    <h2 class="mb-4 text-center">⚽ 매치 등록</h2>

    <form @submit.prevent="onSubmit">
      <!-- 매치 제목 -->
      <div class="mb-3">
        <label class="form-label">매치 제목</label>
        <input type="text" v-model="title" class="form-control" placeholder="예: 7:7 풋살 매치 모집" />
      </div>

      <!-- 상세 설명 -->
      <div class="mb-3">
        <label class="form-label">상세 설명</label>
        <textarea v-model="description" class="form-control" rows="5" placeholder="매치에 대한 설명을 작성해주세요."></textarea>
      </div>

      <!-- 성별 제한 -->
      <div class="mb-3">
        <label class="form-label">성별 제한</label>
        <select v-model="gender" class="form-select">
          <option value="all">전체</option>
          <option value="male">남성만</option>
          <option value="female">여성만</option>
        </select>
      </div>

      <!-- (임시) 제출 버튼 -->
      <div class="text-center">
        <button type="submit" class="btn btn-success" disabled>등록하기 (미구현)</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const title = ref('')
const description = ref('')
const gender = ref('all')

const onSubmit = () => {
  alert('아직 등록 기능은 구현되지 않았습니다.')
}
</script>