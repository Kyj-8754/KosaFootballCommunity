import { createRouter, createWebHistory } from 'vue-router'                                    
import Home from '@/pages/Home.vue';
import NotFound from '@/pages/NotFound.vue';
import Member_LoginForm from '@/pages/member/loginForm.vue';
import Member_RegistForm from '@/pages/member/registForm.vue';
import Member_DetailView from '@/pages/member/detailView.vue';
import Member_UpdateForm from '@/pages/member/updateForm.vue';
import Member_List from '@/pages/member/list.vue';
import Board_DetailView from '@/pages/board/detailView.vue';
import Board_List from '@/pages/board/list.vue';
import Board_RegistForm from '@/pages/board/registForm.vue';
import Board_UpdateForm from '@/pages/board/updateForm.vue';
import Stadium_DetailView from '@/pages/stadium/detailView.vue';
import Stadium_List from '@/pages/stadium/list.vue';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'Home', component: Home },
        { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
        { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
        { path: '/member/detailView', name: 'Member_DetailView', component: Member_DetailView },
        { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
        { path: '/member/list', name: 'Member_List', component: Member_List },
        { path: '/board/detailView',  name: 'Board_DetailView', component: Board_DetailView },
        { path: '/board/list', name: 'Board_List', component: Board_List },
        { path: '/board/registForm', name: 'Board_RegistForm', component: Board_RegistForm },
        { path: '/board/updateForm', name: 'Board_UpdateForm', component: Board_UpdateForm },
        { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
        { path: '/stadium/list',  name: 'Stadium_List', component: Stadium_List },
        { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
    ]
})

export default router;