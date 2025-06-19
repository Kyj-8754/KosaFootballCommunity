import { createRouter, createWebHistory } from 'vue-router'

// 기본 페이지
import Home from '@/pages/Home.vue';
import NotFound from '@/pages/NotFound.vue';

// 멤버 관련
import Member_LoginForm from '@/pages/member/loginForm.vue';
import Member_RegistForm from '@/pages/member/registForm.vue';
import Member_DetailView from '@/pages/member/detailView.vue';
import Member_UpdateForm from '@/pages/member/updateForm.vue';
import Member_List from '@/pages/member/list.vue';

// 게시판 관련
import Board_DetailView from '@/pages/board/detailView.vue';
import Board_List from '@/pages/board/list.vue';
import Board_RegistForm from '@/pages/board/registForm.vue';
import Board_UpdateForm from '@/pages/board/updateForm.vue';

// ✅ 클럽 관련
import Club_List from '@/pages/club/list.vue';
import Club_DetailView from '@/pages/club/detailView.vue'; // 이름 통일

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: Home },

    // 멤버 관련
    { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
    { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
    { path: '/member/detailView', name: 'Member_DetailView', component: Member_DetailView },
    { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
    { path: '/member/list', name: 'Member_List', component: Member_List },

    // 게시판 관련
    { path: '/board/detailView', name: 'Board_DetailView', component: Board_DetailView },
    { path: '/board/list', name: 'Board_List', component: Board_List },
    { path: '/board/registForm', name: 'Board_RegistForm', component: Board_RegistForm },
    { path: '/board/updateForm', name: 'Board_UpdateForm', component: Board_UpdateForm },

    // ✅ 클럽 관련
    { path: '/club/list', name: 'Club_List', component: Club_List },
    { path: '/club/detailView/:id', name: 'Club_DetailView', component: Club_DetailView },

    // 404
    { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
  ]
})

export default router;
