import { createRouter, createWebHistory } from 'vue-router'

// 기본 페이지
import Home from '@/pages/Home.vue'
import NotFound from '@/pages/NotFound.vue'

// 관리자 관련
import Admin_UserList from '@/pages/admin/userList.vue';

// 멤버 관련
import OAuth_Success from '@/pages/oauth/Success.vue';
import Member_LoginForm from '@/pages/member/loginForm.vue';
import Member_FindId from '@/pages/member/findId.vue';
import Member_FindPwd from '@/pages/member/findPwd.vue';
import Member_ResetPwd from '@/pages/member/resetPwd.vue';
import Member_RegistForm from '@/pages/member/registForm.vue';
import Member_UpdateForm from '@/pages/member/updateForm.vue';
import Member_PassWordUpdateForm from '@/pages/member/passwordUpdateForm.vue';
import Member_MemberDelete from '@/pages/member/memberDelete.vue';
import Member_SocialRegister from '@/pages/member/socialRegister.vue';
import Member_MyPage from '@/pages/member/myPage.vue';
import Member_Friend from '@/pages/member/friend.vue';
import Member_Other_Friend from '@/pages/member/otherFriend.vue';
import Member_Profile from '@/pages/member/profile.vue';
import Member_Profile_Update from '@/pages/member/profileUpdate.vue';

// 게시판 관련
import boardList from "@/pages/board/boardList.vue";
import boardRegisterForm from '@/pages/board/boardRegisterForm.vue';
import boardDetail from '@/pages/board/boardDetail.vue';
import boardEditForm from '@/pages/board/boardEditForm.vue';

// 경기장 관련
import Stadium_DetailView from '@/pages/stadium/detailView.vue'
import Stadium_List from '@/pages/stadium/list.vue';
import Stadium_Reservation from '@/pages/stadium/reservation.vue';

// 클럽 관련
import Club_List from '@/pages/club/list.vue'
import Club_DetailView from '@/pages/club/detailView.vue'
import Club_RegistForm from '@/pages/club/registForm.vue'
import Club_UpdateForm from '@/pages/club/updateForm.vue' // ✅ 추가: 클럽 수정 컴포넌트

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

  // 관리자 관련
  { path: '/admin/userList', name: 'Admin_UserList', component: Admin_UserList },

  // 멤버 관련
  { path: '/oauth/success', name: 'OAuth_Success', component: OAuth_Success },
  { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
  { path: '/member/findId', name: 'Member_FindId', component: Member_FindId },
  { path: '/member/findPwd', name: 'Member_FindPwd', component: Member_FindPwd },
  { path: '/member/resetPwd', name: 'Member_ResetPwd', component: Member_ResetPwd },
  { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
  { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
  { path: '/member/passwordUpdateForm', name: 'Member_PassWordUpdateForm', component: Member_PassWordUpdateForm },
  { path: '/member/memberDelete', name: 'Member_MemberDelete', component: Member_MemberDelete },
  { path: '/member/socialRegister', name: 'Member_SocialRegister', component: Member_SocialRegister },
  { path: '/member/myPage', name: 'Member_MyPage', component: Member_MyPage },
  { path: '/member/friend', name: 'Member_Friend', component: Member_Friend },
  { path: '/member/otherFriend', name: 'Member_Other_Friend', component: Member_Other_Friend },
  { path: '/member/profile', name: 'Member_Profile', component: Member_Profile },
  { path: '/member/profileUpdate', name: 'Member_Profile_Update', component: Member_Profile_Update },

  // 게시판 관련
  { path:'/board/boardlist', name: 'boardList', component: boardList },
  { path:'/board/boardregisterform', name: 'boardRegisterForm', component: boardRegisterForm },
  { path: '/board/boarddetail/:id', name: 'boardDetail', component: boardDetail },
  { path: '/board/boardeditform/:id', name: 'boardEditForm', component: boardEditForm },

  // 경기장 관련
  { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
  { path: '/stadium/list',  name: 'Stadium_List', component: Stadium_List },
  { path: '/stadium/reservation',  name: 'Stadium_Reservation', component: Stadium_Reservation },

  // 클럽 관련
  { path: '/club', name: 'Club_List', component: Club_List },
  { path: '/club/registForm', name: 'Club_RegistForm', component: Club_RegistForm },
  { path: '/club/:teamCode', name: 'Club_DetailView', component: Club_DetailView },
  { path: '/club/:teamCode/updateForm', name: 'Club_UpdateForm', component: Club_UpdateForm },

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
