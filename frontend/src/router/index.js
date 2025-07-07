import { createRouter, createWebHistory } from 'vue-router'

// 기본 페이지
import Home from '@/pages/Home.vue'
import NotFound from '@/pages/NotFound.vue'

// 멤버 관련
import OAuth_Success from '@/pages/oauth/Success.vue';
import Member_LoginForm from '@/pages/member/loginForm.vue';
import Member_RegistForm from '@/pages/member/registForm.vue';
import Member_DetailView from '@/pages/member/detailView.vue';
import Member_UpdateForm from '@/pages/member/updateForm.vue';
import Member_List from '@/pages/member/list.vue';
import Member_SocialRegister from '@/pages/member/socialRegister.vue';
import Member_MyPage from '@/pages/member/myPage.vue'

// 게시판 관련
import boardList from "@/pages/board/boardList.vue";
import boardRegisterForm from '@/pages/board/boardRegisterForm.vue';
import boardDetail from '@/pages/board/boardDetail.vue';
import boardEditForm from '@/pages/board/boardEditForm.vue';

// 경기장 관련
import Stadium_DetailView from '@/pages/stadium/detailView.vue'
import Stadium_List from '@/pages/stadium/list.vue';
import Stadium_Reservation from '@/pages/reservation/reservationForm.vue';

// 예약/결제 관련
import Reservation_Form from '@/pages/reservation/reservationForm.vue';
import Reservation_Confirm from '@/pages/reservation/reservation_confirm.vue';

// 클럽 관련
import Club_List from '@/pages/club/list.vue'
import Club_DetailView from '@/pages/club/detailView.vue'
import Club_RegistForm from '@/pages/club/registForm.vue'
import Club_UpdateForm from '@/pages/club/updateForm.vue'
import Club_ApplyList from '@/pages/club/applyList.vue'
import Club_MemberList from '@/pages/club/memberList.vue'
// 모집글 관련
import Recruit_List from '@/pages/recruitBoard/list.vue'
import Recruit_DetailView from '@/pages/recruitBoard/detailView.vue'
import Recruit_RegistForm from '@/pages/recruitBoard/registForm.vue'
import Recruit_UpdateForm from '@/pages/recruitBoard/updateForm.vue'

//매치관련
import matchList from '@/pages/board/match/matchList.vue';
import matchDetail from '@/pages/board/match/matchDetail.vue';
import matchLog from '@/pages/board/match/matchLog.vue';

const routes = [
  { path: '/', name: 'Home', component: Home },

  // 멤버 관련
  { path: '/oauth/success', name: 'OAuth_Success', component: OAuth_Success },
  { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
  { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
  { path: '/member/detailView', name: 'Member_DetailView', component: Member_DetailView },
  { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
  { path: '/member/list', name: 'Member_List', component: Member_List },
  { path: '/member/socialRegister', name: 'Member_SocialRegister', component: Member_SocialRegister },
  { path: '/member/myPage', name: 'Member_MyPage', component: Member_MyPage },

  // 게시판 관련
  { path:'/board/boardlist', name: 'boardList', component: boardList },
  { path:'/board/boardregisterform', name: 'boardRegisterForm', component: boardRegisterForm },
  { path: '/board/boarddetail/:id', name: 'boardDetail', component: boardDetail },
  { path: '/board/boardeditform/:id', name: 'boardEditForm', component: boardEditForm },

  // 예약 결제 관련
  { path:'/reservation/reservationForm', name: 'reservation_Form', component: Reservation_Form} ,
  { path:'/reservation/reservation_confirm/:reservationId', name: 'reservation_Confirm', component: Reservation_Confirm, props: true} ,

  // 경기장 관련
  { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
  { path: '/stadium/list',  name: 'Stadium_List', component: Stadium_List },
  { path: '/stadium/reservation',  name: 'Stadium_Reservation', component: Stadium_Reservation },

  // 클럽 관련
  { path: '/club', name: 'Club_List', component: Club_List },
  { path: '/club/registForm', name: 'Club_RegistForm', component: Club_RegistForm },
  { path: '/club/:teamCode', name: 'Club_DetailView', component: Club_DetailView },
  { path: '/club/:teamCode/updateForm', name: 'Club_UpdateForm', component: Club_UpdateForm },
  { path: '/club/:teamCode/applyList', name: 'Club_ApplyList', component: Club_ApplyList },
  { path: '/club/:teamCode/memberList', name: 'Club_MemberList', component: Club_MemberList },

  // 모집글 관련
  { path: '/recruitBoard', name: 'Recruit_List', component: Recruit_List },
  { path: '/recruitBoard/new', name: 'Recruit_RegistForm', component: Recruit_RegistForm },
  { path: '/recruitBoard/:bno', name: 'Recruit_DetailView', component: Recruit_DetailView },
  { path: '/recruitBoard/:bno/updateForm', name: 'Recruit_UpdateForm', component: Recruit_UpdateForm },

  //매치 관련
    { path:'/match/matchlist', name: 'matchList', component: matchList },
    { path:'/match/matchdetail/:id', name: 'matchDetail', component: matchDetail },
    { path:'/match/matchlog/:id', name: 'matchLog', component: matchLog },

  // 404 Not Found
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
