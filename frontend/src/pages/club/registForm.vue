<template>
  <div class="container mt-5" style="max-width: 600px;">
    <h2 class="mb-3">CREATE NEW CLUB</h2>
    <p class="text-muted">클럽 이름을 작성해주세요</p>

    <form @submit.prevent="submitClub">
      <!-- 클럽 이름 + 중복 확인 -->
      <div class="mb-3">
        <label class="form-label">클럽 이름</label>
        <div class="input-group">
          <input type="text" v-model="club.club_name" class="form-control" required />
          <button class="btn btn-outline-secondary" type="button" @click="checkNameDuplicate">중복 확인</button>
        </div>
        <div v-if="nameDuplicateResult === false" class="form-text text-danger">
          이미 존재하는 클럽 이름입니다.
        </div>
        <div v-else-if="nameDuplicateResult === true" class="form-text text-success">
          사용 가능한 클럽 이름입니다.
        </div>
      </div>

      <!-- 팀 코드 + 중복 확인 -->
      <div class="mb-3">
        <label class="form-label">팀 코드</label>
        <div class="input-group">
          <input type="text" v-model="club.team_code" class="form-control" required />
          <button class="btn btn-outline-secondary" type="button" @click="checkCodeDuplicate">중복 확인</button>
        </div>
        <div v-if="codeDuplicateResult === false" class="form-text text-danger">
          이미 사용 중인 팀 코드입니다.
        </div>
        <div v-else-if="codeDuplicateResult === true" class="form-text text-success">
          사용 가능한 팀 코드입니다.
        </div>
      </div>

      <!-- 안내 메시지 -->
      <div class="mb-3">
        <p class="form-text">팀 코드 URL은 팀페이지 주소로 사용돼요</p>
        <p class="text-muted">http://localhost:5173/club/<strong>{{ club.team_code }}</strong></p>
      </div>

      <!-- 등록 버튼 -->
      <div class="d-grid">
        <button type="submit" class="btn btn-primary">다음</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Club_RegistForm',
  data() {
    return {
      club: {
        club_name: '',
        team_code: ''
      },
      codeDuplicateResult: null,
      nameDuplicateResult: null
    }
  },
  methods: {
    // ✅ 팀 코드 중복 확인
    async checkCodeDuplicate() {
      if (!this.club.team_code) {
        alert('팀 코드를 입력하세요.');
        return;
      }
      try {
        const res = await axios.get('/api/clubs/check-teamcode', {
          params: { teamCode: this.club.team_code }
        });
        this.codeDuplicateResult = res.data;
      } catch (error) {
        console.error('팀 코드 중복 확인 실패:', error);
        alert('중복 확인 중 오류가 발생했습니다.');
      }
    },

    // ✅ 클럽 이름 중복 확인
    async checkNameDuplicate() {
      if (!this.club.club_name) {
        alert('클럽 이름을 입력하세요.');
        return;
      }
      try {
        const res = await axios.get('/api/clubs/check-name', {
          params: { name: this.club.club_name }
        });
        this.nameDuplicateResult = res.data;
      } catch (error) {
        console.error('클럽 이름 중복 확인 실패:', error);
        alert('중복 확인 중 오류가 발생했습니다.');
      }
    },

    // ✅ 등록
    async submitClub() {
      if (this.codeDuplicateResult === false) {
        alert('팀 코드가 중복되었습니다. 다른 코드를 입력하세요.');
        return;
      }
      if (this.nameDuplicateResult === false) {
        alert('클럽 이름이 중복되었습니다. 다른 이름을 입력하세요.');
        return;
      }
      try {
        await axios.post('/api/clubs', this.club);
        alert('클럽이 등록되었습니다. 이어서 클럽 프로필을 작성하세요.');
        this.$router.push(`/club/${this.club.team_code}`);
      } catch (error) {
        console.error('클럽 등록 실패:', error);
        alert('등록에 실패했습니다.');
      }
    }
  }
}
</script>
