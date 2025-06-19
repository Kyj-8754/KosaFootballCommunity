import { createRouter, createWebHistory } from 'vue-router'                                    
// import Home from '@/pages/Home.vue';
// import NotFound from '@/pages/NotFound.vue';
import boardList from "@/pages/board/boardList.vue";
import boardRegisterForm from '@/pages/board/boardRegisterForm.vue';
import boardDetail from '@/pages/board/boardDetail.vue';
import boardEditForm from '@/pages/board/boardEditForm.vue';
import Stadium_DetailView from '@/pages/stadium/detailView.vue';


const router = createRouter({
    history: createWebHistory(),
    routes: [
        // { path: '/', name: 'Home', component: Home },
        // { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
        { path:'/board/boardlist', component: boardList },
        { path:'/board/boardregisterform', component: boardRegisterForm },
        { path: '/board/boarddetail/:id', component: boardDetail },
        { path: '/board/boardeditform/:id', component: boardEditForm },
        { path: '/stadium/detailView',  name: 'Stadium_DetailView', component: Stadium_DetailView },
        { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
    ]
})

export default router;