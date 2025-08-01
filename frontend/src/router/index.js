import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import ('@/pages/Home.vue') },

  // 관리자 관련
  { path: '/admin/userList', name: 'Admin_UserList', component: () => import ('@/pages/admin/userList.vue') },

  // 멤버 관련
  { path: '/oauth/success', name: 'OAuth_Success', component: () => import ('@/pages/oauth/Success.vue') },
  { path: '/member/loginForm', name: 'Member_LoginForm', component: () => import ('@/pages/member/loginForm.vue') },
  { path: '/member/findId', name: 'Member_FindId', component: () => import ('@/pages/member/findId.vue') },
  { path: '/member/findPwd', name: 'Member_FindPwd', component: () => import ('@/pages/member/findPwd.vue') },
  { path: '/member/resetPwd', name: 'Member_ResetPwd', component: () => import ('@/pages/member/resetPwd.vue') },
  { path: '/member/registForm', name: 'Member_RegistForm', component: () => import ('@/pages/member/registForm.vue') },
  { path: '/member/updateForm', name: 'Member_UpdateForm', component: () => import ('@/pages/member/updateForm.vue') },
  { path: '/member/passwordUpdateForm', name: 'Member_PassWordUpdateForm', component: () => import ('@/pages/member/passwordUpdateForm.vue') },
  { path: '/member/memberDelete', name: 'Member_MemberDelete', component: () => import ('@/pages/member/memberDelete.vue') },
  { path: '/member/socialRegister', name: 'Member_SocialRegister', component: ()=>import ('@/pages/member/socialRegister.vue')},
  { path: '/member/myPage', name: 'Member_MyPage', component: () => import ('@/pages/member/myPage.vue') },
  { path: '/member/friend', name: 'Member_Friend', component: () => import ('@/pages/member/friend.vue') },
  { path: '/member/otherFriend', name: 'Member_Other_Friend', component: () => import ('@/pages/member/otherFriend.vue') },
  { path: '/member/profile', name: 'Member_Profile', component: () => import ('@/pages/member/profile.vue') },
  { path: '/member/profileUpdate', name: 'Member_Profile_Update', component: () => import ('@/pages/member/profileUpdate.vue') },

  // 게시판 관련
  { path:'/board/boardlist', name: 'boardList', component: () => import("@/pages/board/boardList.vue")},
  { path:'/board/boardregisterform', name: 'boardRegisterForm', component: ()=>import ('@/pages/board/boardRegisterForm.vue') },
  { path: '/board/boarddetail/:id', name: 'boardDetail', component: () => import ('@/pages/board/boardDetail.vue') },
  { path: '/board/boardeditform/:id', name: 'boardEditForm', component: () => import ('@/pages/board/boardEditForm.vue') },

  // 예약 결제 관련
  { path:'/reservation/reservationForm', name: 'reservation_Form', component: () => import ('@/pages/reservation/reservationForm.vue')} ,
  { path:'/reservation/reservation_confirm/:reservationId', name: 'reservation_Confirm', component: () => import ('@/pages/reservation/reservation_confirm.vue'), props: true} ,
  { path:'/reservation/reservation_list', name: 'Reservation_List', component: () => import('@/pages/reservation/reservation_list.vue')},
  { path:'/payment/payment_list', name: 'Payment_List', component: () => import('@/pages/payment/payment_list.vue')},

  // 경기장 관련
  { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: () => import ('@/pages/stadium/detailView.vue')},
  { path: '/stadium/list',  name: 'Stadium_List', component: () => import ('@/pages/stadium/list.vue') },
  { path: '/stadium/reservation',  name: 'Stadium_Reservation', component: () => import ('@/pages/reservation/reservationForm.vue') },

  // 클럽 관련
  { path: '/club', name: 'Club_List', component: () => import ('@/pages/club/list.vue') },
  { path: '/club/registForm', name: 'Club_RegistForm', component: () => import  ('@/pages/club/registForm.vue') },
  { path: '/club/:teamCode', name: 'Club_DetailView', component: () => import  ('@/pages/club/detailView.vue') },
  { path: '/club/:teamCode/updateForm', name: 'Club_UpdateForm', component: () => import  ('@/pages/club/updateForm.vue') },
  { path: '/club/:teamCode/applyList', name: 'Club_ApplyList', component: () => import  ('@/pages/club/applyList.vue') },
  { path: '/club/:teamCode/memberList', name: 'Club_MemberList', component: () => import  ('@/pages/club/memberList.vue') },
  { path: '/club/clubMatchList', name: 'Club_MatchList', component: () => import  ('@/pages/club/clubMatchList.vue') },
  { path: '/club/clubMatchSchedule', name: 'Club_MatchSchedule', component: () => import  ('@/pages/club/ClubMatchScheduleView.vue') },

  // 모집글 관련
  { path: '/recruitBoard', name: 'Recruit_List', component: () => import ('@/pages/recruitBoard/list.vue')},
  { path: '/recruitBoard/new', name: 'Recruit_RegistForm', component: () => import ('@/pages/recruitBoard/registForm.vue') },
  { path: '/recruitBoard/:bno', name: 'Recruit_DetailView', component: () => import ('@/pages/recruitBoard/detailView.vue') },
  { path: '/recruitBoard/:bno/updateForm', name: 'Recruit_UpdateForm', component: () => import ('@/pages/recruitBoard/updateForm.vue')},

  //매치 관련
    { path:'/match/matchlist', name: 'matchList', component: () => import ('@/pages/board/match/matchList.vue')},
    { path:'/match/matchdetail/:id', name: 'matchDetail', component: () => import ('@/pages/board/match/matchDetail.vue')},
    { path:'/match/matchlog/:id', name: 'matchLog', component: () => import ('@/pages/board/match/matchLog.vue')},
    { path:'/match/matchregisterform', name: 'matchregister', component: () => import ('@/pages/board/match/matchRegisterForm.vue')},

  // 웹소켓 관련 알림 리스트


  // 404 Not Found
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import ('@/pages/NotFound.vue')}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
