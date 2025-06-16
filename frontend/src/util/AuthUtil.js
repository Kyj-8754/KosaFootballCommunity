import axios from 'axios'
import { useMemberStore } from '@/stores/member'

// const pathsToRole = [
//     { path: '/', name: 'Home', component: Home },
//     { path: '/member/loginForm', name: 'Member_LoginForm', component: Member_LoginForm },
//     { path: '/member/registForm', name: 'Member_RegistForm', component: Member_RegistForm },
//     { path: '/member/detailView/:id', name: 'Member_DetailView', component: Member_DetailView },
//     { path: '/member/updateForm', name: 'Member_UpdateForm', component: Member_UpdateForm },
//     { path: '/member/list', name: 'Member_List', component: Member_List },
//     { path: '/board/detailView',  name: 'Board_DetailView', component: Board_DetailView },
//     { path: '/board/list', name: 'Board_List', component: Board_List },
//     { path: '/board/registForm', name: 'Board_RegistForm', component: Board_RegistForm },
//     { path: '/board/updateForm', name: 'Board_UpdateForm', component: Board_UpdateForm },
//     { path: '/:paths(.*)*', name: 'NotFound', component: NotFound },
// ]

// 로그인
const Login = async (userid, passwd, sucessCallback, failCallback) => {
    try {
    const res = await axios.post('/api/member/login', { userid, passwd })
    const { status, message, supervisor } = res.data

    switch (status) {
      case 'SUCCESS':
        const store = useMemberStore();
        store.setMember({
            userid, 
            supervisor,
            authenticated: true
        }) 
        if (sucessCallback) sucessCallback()
        break

      case 'NO_USER':
          alert(message)
          if (failCallback) failCallback()
          break
      case 'FAIL_CREDENTIALS':
          alert(message)
          if (failCallback) failCallback()
          break
      case 'FAIL_LOCKED':
          alert(message)
          if (failCallback) failCallback()
          break

      default:
        alert('알 수 없는 오류가 발생했습니다.')
    }
  } catch (err) {
    console.error(err)
    alert('서버 요청 중 오류가 발생했습니다.')
    if (failCallback) failCallback()
  }
}

// 로그 아웃
const logoutProcess = (callback) => {
    const store = useMemberStore();
    store.clearMember()
    callback();
}

export{Login, logoutProcess};