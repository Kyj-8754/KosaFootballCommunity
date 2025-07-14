<template>
  <div class="update-container">
    <form @submit.prevent="onSubmit" class="update-form">
      <h3>회원 프로필 수정</h3>

      <input type="hidden" v-model="form.userNo" />

      <div class="mb-3">
        <label for="name" class="form-label">이름</label>
        <input type="text" id="name" v-model="form.userName" required class="form-control" />
      </div>

      <div class="mb-3">
        <label for="comment" class="form-label">소개</label>
        <textarea id="comment" v-model="form.userComment" class="form-control" rows="3" />
      </div>

      <!-- 좋아하는 스타일 -->
      <div class="mb-3">
        <select v-model="form.styleCode" class="form-select">
          <option value="">스타일을 선택해주세요</option>
          <option value="1">공격</option>
          <option value="2">밸런스</option>
          <option value="3">수비</option>
        </select>
      </div>
      

      <!-- 자신있는 능력 -->
      <div class="mb-3">
        <select v-model="form.statCode" class="form-select">
          <option value="">능력을 선택해주세요</option>
          <option value="1">슛</option>
          <option value="2">패스</option>
          <option value="3">드리블</option>
          <option value="4">체력</option>
          <option value="5">스피드</option>
          <option value="6">피지컬</option>
        </select>
      </div>


      <div class="link-area">
        <input type="submit" value="변경" class="btn btn-primary" />
        <router-link
          v-if="form.userNo"
          :to="{ name: 'Member_Profile', query: { userNo: form.userNo } }"
          class="btn btn-outline-secondary"
        >
          취소
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { useProfileUpdate } from '@/utils/script/user.js'
const { form, onSubmit } = useProfileUpdate()
</script>

<style scoped>
.update-container {
  display: flex;
  justify-content: center;
  margin-top: 60px;
}
.update-form {
  background-color: white;
  padding: 40px;
  width: 600px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.update-form h3 {
  text-align: center;
  font-weight: bold;
  margin-bottom: 30px;
}
.update-form label {
  font-weight: bold;
}
.update-form .form-control {
  margin-bottom: 15px;
}
.link-area {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
.profile-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}
</style>
