import { createRouter, createWebHistory } from 'vue-router'                                    
import boardList from "@/pages/board/boardList.vue";
import boardRegisterForm from '@/pages/board/boardRegisterForm.vue';
import boardDetail from '@/pages/board/boardDetail.vue';
import boardEditForm from '@/pages/board/boardEditForm.vue';
import Stadium_DetailView from '@/pages/stadium/detailView.vue';
import Stadium_List from '@/pages/stadium/list.vue';                                
import Home from '@/pages/Home.vue';
import NotFound from '@/pages/NotFound.vue';
import Member_LoginForm from '@/pages/member/loginForm.vue';
import Member_RegistForm from '@/pages/member/registForm.vue';
import Member_DetailView from '@/pages/member/detailView.vue';
import Member_UpdateForm from '@/pages/member/updateForm.vue';
import Member_List from '@/pages/member/list.vue';
import matchList from '@/pages/board/match/matchList.vue';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path:'/match/matchlist', name: 'matchList', component: matchList },
        { path:'/board/boardlist', name: 'boardList', component: boardList },
        { path:'/board/boardregisterform', name: 'boardRegisterForm', component: boardRegisterForm },
        { path: '/board/boarddetail/:id', name: 'boardDetail', component: boardDetail },
        { path: '/board/boardeditform/:id', name: 'boardEditForm', component: boardEditForm },
        { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
        { path: '/', name: 'Home', component: Home },
        { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
        { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
        { path: '/member/detailView', name: 'Member_DetailView', component: Member_DetailView },
        { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
        { path: '/member/list', name: 'Member_List', component: Member_List },
        { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
        { path: '/stadium/list',  name: 'Stadium_List', component: Stadium_List },
        { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
    ]
})

export default router;