import { createRouter, createWebHistory } from 'vue-router'


const routes = [
  { path: '/', name: 'Home', component: () => import ('@/pages/Home.vue') },

  // 멤버 관련
  { path: '/oauth/success', name: 'OAuth_Success', component: () => import ('@/pages/oauth/Success.vue') },
  { path: '/member/loginForm', name: 'Member_LoginForm', component: () => import ('@/pages/member/loginForm.vue') },
  { path: '/member/registForm', name: 'Member_RegistForm', component: () => import ('@/pages/member/registForm.vue') },
  { path: '/member/detailView', name: 'Member_DetailView', component: () => import ('@/pages/member/detailView.vue') },
  { path: '/member/updateForm', name: 'Member_UpdateForm', component: () => import ('@/pages/member/updateForm.vue') },
  { path: '/member/list', name: 'Member_List', component: () => import ('@/pages/member/list.vue') },
  { path: '/member/socialRegister', name: 'Member_SocialRegister', component: ()=>import ('@/pages/member/socialRegister.vue')},
  { path: '/member/myPage', name: 'Member_MyPage', component: () => import ('@/pages/member/myPage.vue') },

  // 게시판 관련
  { path:'/board/boardlist', name: 'boardList', component: () => import("@/pages/board/boardList.vue")},
  { path:'/board/boardregisterform', name: 'boardRegisterForm', component: ()=>import ('@/pages/board/boardRegisterForm.vue') },
  { path: '/board/boarddetail/:id', name: 'boardDetail', component: () => import ('@/pages/board/boardDetail.vue') },
  { path: '/board/boardeditform/:id', name: 'boardEditForm', component: () => import ('@/pages/board/boardEditForm.vue') },

  // 예약 결제 관련
  { path:'/reservation/reservationForm', name: 'reservation_Form', component: () => import ('@/pages/reservation/reservationForm.vue')} ,
  { path:'/reservation/reservation_confirm/:reservationId', name: 'reservation_Confirm', component: () => import ('@/pages/reservation/reservation_confirm.vue'), props: true} ,
  { path:'/reservation/reservation_list', name: 'Reservation_List', component: () => import('@/pages/reservation/reservation_list.vue')},
  { path:'/payment/success', name: 'PaymentSuccess', component: () => import('@/pages/payment/Success.vue')},
  { path:'/payment/fail', name: 'PaymentFail', component: () => import('@/pages/payment/Fail.vue')},
  // { path:'/payment/cancel', name: 'PaymentCancel', component: () => import('@/pages/payment/Cancel.vue')},

  // 경기장 관련
  { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: () => import ('@/pages/stadium/detailView.vue')},
  { path: '/stadium/list',  name: 'Stadium_List', component: () => import ('@/pages/stadium/list.vue') },
  { path: '/stadium/reservation',  name: 'Stadium_Reservation', component: () => import ('@/pages/reservation/reservationForm.vue') },

  // 클럽 관련
  { path: '/club', name: 'Club_List', component: () => import ('@/pages/club/list.vue') },
  { path: '/club/registForm', name: 'Club_RegistForm', component: () => import ('@/pages/club/registForm.vue') },
  { path: '/club/:teamCode', name: 'Club_DetailView', component: () => import ('@/pages/club/detailView.vue') },
  { path: '/club/:teamCode/updateForm', name: 'Club_UpdateForm', component: ()=>import ('@/pages/club/updateForm.vue') },

  // 모집글 관련
  { path: '/recruitBoard', name: 'Recruit_List', component: () => import ('@/pages/recruitBoard/list.vue')},
  { path: '/recruitBoard/new', name: 'Recruit_RegistForm', component: () => import ('@/pages/recruitBoard/registForm.vue') },
  { path: '/recruitBoard/:bno', name: 'Recruit_DetailView', component: () => import ('@/pages/recruitBoard/detailView.vue') },
  { path: '/recruitBoard/:bno/updateForm', name: 'Recruit_UpdateForm', component: () => import ('@/pages/recruitBoard/updateForm.vue')},

  //매치 관련
    { path:'/match/matchlist', name: 'matchList', component: () => import ('@/pages/board/match/matchList.vue')},
    { path:'/match/matchdetail/:id', name: 'matchDetail', component: () => import ('@/pages/board/match/matchDetail.vue')},
    { path:'/match/matchlog/:id', name: 'matchLog', component: () => import ('@/pages/board/match/matchLog.vue')},

  // 404 Not Found
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import ('@/pages/NotFound.vue')}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
