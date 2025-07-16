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
        <p class="text-muted">http://itsfootball.store/club/<strong>{{ club.team_code }}</strong></p>
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
import { inject } from 'vue'

export default {
  name: 'Club_RegistForm',
  data() {
    return {
      club: {
        club_name: '',
        team_code: '',
        user_no: '' // 여기에 inject된 값이 들어감
      },
      codeDuplicateResult: null,
      nameDuplicateResult: null
    }
  },

 mounted() {
  const userNo = inject('userNo')

  if (!userNo || !userNo.value) {
    console.warn('⚠️ userNo가 주입되지 않았거나 값이 없습니다.')
    // 선택적으로 라우터 이동 가능:
    // this.$router.push('/login')
  } else {
    this.club.user_no = userNo.value
  }
},




  methods: {
    // ✅ 팀 코드 중복 확인 (수정된 경로)
    async checkCodeDuplicate() {
      if (!this.club.team_code) {
        alert('팀 코드를 입력하세요.');
        return;
      }
      try {
        const res = await axios.get('/club_api/check-teamcode', { // 바뀐 주소 
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
        const res = await axios.get('/club_api/check-name', { // 바뀐 주소
          params: { name: this.club.club_name }
        });
        this.nameDuplicateResult = res.data;
      } catch (error) {
        console.error('클럽 이름 중복 확인 실패:', error);
        alert('중복 확인 중 오류가 발생했습니다.');
      }
    },

    // ✅ 클럽 등록 
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
        await axios.post('/club_api', this.club); // 바뀐 주소
        alert('클럽이 등록되었습니다. 이어서 클럽 프로필을 작성하세요.');
        this.$router.push(`/club/${this.club.team_code}`); 
      } catch (error) {

          if (
      error.response &&
      error.response.status === 400 &&
      error.response.data === '이미 해당 유저는 클럽을 보유하고 있습니다.'
    ) {
      alert('팀은 1개만 생성할 수 있습니다.');
      return;
    }



        console.error('클럽 등록 실패:', error);
        alert('등록에 실패했습니다.');
      }
    }
  }
}
</script>
