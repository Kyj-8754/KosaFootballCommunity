import { ref, watch, onMounted, inject, computed, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// 아이디 찾기 스크립트
export function useFindIdForm() {
  const router = useRouter()

  const form = ref({
    userName: '',
    userPhone: ''
  })

  const smsCode = ref('')
  const smsCodeMsg = ref('')
  const smsCodeValid = ref(false)
  const isCodeSent = ref(false)

  // 문자 전송
  const sendSmsCode = async () => {
    if (!form.value.userPhone) {
      alert('전화번호를 입력해주세요')
      return
    }

    try {
      const res = await fetch('/login_api/user/na/smsCode', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ userPhone: form.value.userPhone })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        alert('인증 코드가 전송되었습니다')
        isCodeSent.value = true
      } else {
        alert(json.res_msg || '전송 실패')
      }
    } catch (err) {
      console.error('SMS 전송 오류:', err)
      alert('서버 오류로 전송 실패')
    }
  }

  // 인증번호 검증
  const verifySmsCode = async () => {
    try {
      const res = await fetch('/login_api/user/na/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          userPhone: form.value.userPhone,
          smsCode: smsCode.value
        })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        smsCodeMsg.value = '인증에 성공했습니다.'
        smsCodeValid.value = true
      } else {
        smsCodeMsg.value = json.res_msg || '인증 실패'
        smsCodeValid.value = false
      }
    } catch (err) {
      console.error('인증 오류:', err)
      smsCodeMsg.value = '서버 오류로 인증 실패'
      smsCodeValid.value = false
    }
  }

  // 아이디 찾기 제출
  const onSubmit = async () => {
    if (!smsCodeValid.value) {
      alert('전화번호 인증을 완료해주세요')
      return
    }

    try {
      const response = await fetch('/login_api/user/na/findId', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify({
          userName: form.value.userName,
          userPhone: form.value.userPhone
        })
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push('/member/loginForm')
      } else {
        alert(json.res_msg || '아이디 찾기에 실패했습니다.')
      }
    } catch (error) {
      console.error('아이디 찾기 요청 중 오류 발생:', error)
      alert('서버 오류로 아이디 찾기에 실패했습니다.')
    }
  }

  watch(() => form.value.userPhone, (newVal, oldVal) => {
    if (newVal !== oldVal) {
      isCodeSent.value = false
      smsCodeValid.value = false
      smsCode.value = ''
      smsCodeMsg.value = ''
    }
  })

  return {
    form,
    smsCode,
    smsCodeMsg,
    smsCodeValid,
    isCodeSent,
    sendSmsCode,
    verifySmsCode,
    onSubmit
  }
}

// 비밀번호 찾기 스크립트
export function useFindPwdForm() {
  const router = useRouter()

  const form = ref({
    userId: '',
    userName: '',
    userPhone: ''
  })

  const smsCode = ref('')
  const smsCodeMsg = ref('')
  const smsCodeValid = ref(false)
  const isCodeSent = ref(false)

  // 문자 전송
  const sendSmsCode = async () => {
    if (!form.value.userPhone) {
      alert('전화번호를 입력해주세요')
      return
    }

    try {
      const res = await fetch('/login_api/user/na/smsCode', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ userPhone: form.value.userPhone })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        alert('인증 코드가 전송되었습니다')
        isCodeSent.value = true
      } else {
        alert(json.res_msg || '전송 실패')
      }
    } catch (err) {
      console.error('SMS 전송 오류:', err)
      alert('서버 오류로 전송 실패')
    }
  }

  // 인증번호 검증
  const verifySmsCode = async () => {
    try {
      const res = await fetch('/login_api/user/na/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          userPhone: form.value.userPhone,
          smsCode: smsCode.value
        })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        smsCodeMsg.value = '인증에 성공했습니다.'
        smsCodeValid.value = true
      } else {
        smsCodeMsg.value = json.res_msg || '인증 실패'
        smsCodeValid.value = false
      }
    } catch (err) {
      console.error('인증 오류:', err)
      smsCodeMsg.value = '서버 오류로 인증 실패'
      smsCodeValid.value = false
    }
  }

  // 비밀번호 찾기 제출
  const onSubmit = async () => {
    if (!smsCodeValid.value) {
      alert('전화번호 인증을 완료해주세요')
      return
    }

    try {
      const response = await fetch('/login_api/user/na/findPwd', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify({
          userId: form.value.userId,
          userName: form.value.userName,
          userPhone: form.value.userPhone
        })
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push({ path: '/member/resetPwd', query: { userNo: json.userNo } })
      } else {
        alert(json.res_msg || '비밀번호 찾기에 실패했습니다.')
      }
    } catch (error) {
      console.error('비밀번호 찾기 요청 중 오류 발생:', error)
      alert('서버 오류로 비밀번호 찾기에 실패했습니다.')
    }
  }

  watch(() => form.value.userPhone, (newVal, oldVal) => {
    if (newVal !== oldVal) {
      isCodeSent.value = false
      smsCodeValid.value = false
      smsCode.value = ''
      smsCodeMsg.value = ''
    }
  })

  return {
    form,
    smsCode,
    smsCodeMsg,
    smsCodeValid,
    isCodeSent,
    sendSmsCode,
    verifySmsCode,
    onSubmit
  }
}

// 친구 목록 화면 스크립트
export function useFriendTabs(userNo, token) {
  const activeTab = ref('friends')
  const tabList = [
    { label: '친구', value: 'friends' },
    { label: '신청대기', value: 'pending' },
    { label: '친구검색', value: 'searchFriend' }
  ]

  const friends = ref([])
  const pending = ref([])
  const searchKeyword = ref('')
  const searchResults = ref([])

  // 숫자 표시를 위한 카운트
  const getCount = (type) => {
    if (type === 'friends') return friends.value.length
    if (type === 'pending') return pending.value.length
    if (type === 'searchFriend') return searchResults.value.length
    return 0
  }

  // 친구 목록 불러오기
  const loadFriendList = async () => {
    if (!userNo?.value) return
    try {
      const res = await axios.get('/login_api/mypage/friends', {
        params: { userNo: userNo.value },
        headers: { Authorization: `Bearer ${token.value}` }
      })
      if (res.data?.res_code === '200') {
        friends.value = res.data.data
      }
    } catch (err) {
      console.error('친구 목록 불러오기 실패', err)
    }
  }

  // 신청 대기 목록 불러오기
  const loadPendingRequests = async () => {
    if (!userNo?.value) return
    try {
      const res = await axios.get('/login_api/mypage/pending', {
        params: { userNo: userNo.value },
        headers: { Authorization: `Bearer ${token.value}` }
      })
      if (res.data?.res_code === '200') {
        pending.value = res.data.data
      }
    } catch (err) {
      console.error('신청 대기 목록 불러오기 실패', err)
    }
  }

  // 친구 검색
  const searchFriends = async () => {
    const keyword = searchKeyword.value.trim()
    if (!keyword) {
      searchResults.value = []
      return
    }

    try {
      const res = await axios.get('/login_api/mypage/search', {
        params: {
          keyword,
          loginUserNo: userNo.value
        },
        headers: { Authorization: `Bearer ${token.value}` }
      })

      if (res.data?.res_code === '200' && res.data.data) {
        searchResults.value = [res.data.data]
      } else {
        searchResults.value = []
      }
    } catch (err) {
      console.error('🔍 친구 검색 오류', err)
      searchResults.value = []
      alert('검색 중 문제가 발생했습니다.')
    }
  }

  // 친구 신청
  const requestFriend = async (targetUserNo) => {
    if (!userNo?.value) {
      alert('로그인이 필요합니다.')
      return
    }

    try {
      await axios.post('/login_api/mypage/request', {
        requesterNo: userNo.value,
        requestedNo: targetUserNo
      }, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      alert('친구 요청을 보냈습니다.')
      await searchFriends()
    } catch (err) {
      console.error('친구 요청 실패:', err)
      alert('친구 요청에 실패했습니다.')
    }
  }

  // 친구 신청 수락
  const acceptFriendRequest = async (requesterNo) => {
    try {
      const res = await axios.post('/login_api/mypage/accept', {
        requesterNo,
        requestedNo: userNo.value
      }, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      if (res.data?.res_code === '200') {
        alert('친구 요청을 수락했습니다.')
        pending.value = pending.value.filter(user => user.userNo !== requesterNo)
        await loadFriendList()
      } else {
        alert('친구 수락에 실패했습니다.')
      }
    } catch (err) {
      console.error('친구 수락 오류', err)
      alert('친구 수락 중 오류가 발생했습니다.')
    }
  }

  // 친구 신청 거절
  const rejectFriendRequest = async (requesterNo) => {
    try {
      const res = await axios.post('/login_api/mypage/reject', {
        requesterNo,
        requestedNo: userNo.value
      }, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      if (res.data?.res_code === '200') {
        alert('친구 요청을 거절했습니다.')
        pending.value = pending.value.filter(user => user.userNo !== requesterNo)
      } else {
        alert('친구 거절에 실패했습니다.')
      }
    } catch (err) {
      console.error('친구 거절 오류', err)
      alert('친구 거절 중 오류가 발생했습니다.')
    }
  }

  onMounted(() => {
    if (userNo?.value) {
      loadFriendList()
      loadPendingRequests()
    }
  })

  watch(userNo, (val) => {
    if (val) {
      loadFriendList()
      loadPendingRequests()
    }
  }, { immediate: true })

  return {
    activeTab,
    tabList,
    friends,
    pending,
    searchKeyword,
    searchResults,
    getCount,
    loadFriendList,
    loadPendingRequests,
    searchFriends,
    requestFriend,
    acceptFriendRequest,
    rejectFriendRequest
  }
}

// 로그인 스크립트
export function useLoginForm() {
  const userId = ref('')
  const userPwd = ref('')
  const router = useRouter()
  const route = useRoute()
  const token = inject('token')

  // 로그인 요청
  const login = async () => {
    try {
      const res = await axios.post(
        '/security_api/generateToken',
        {
          userId: userId.value,
          userPwd: userPwd.value,
        },
        { withCredentials: true }
      )
      localStorage.setItem('accessToken', res.data.accessToken)
      localStorage.setItem('refreshToken', res.data.refreshToken)
      token.value = res.data.accessToken
      router.push('/')
    } catch (err) {
      alert('로그인 실패: ' + (err.response?.data?.res_msg || '오류 발생'))
      userId.value = ''
      userPwd.value = ''
      console.error('❌ 실패 상태코드:', err.response?.status)
      console.error('❌ 실패 응답:', err.response?.data)
    }
  }

  onMounted(() => {
    const accessToken = route.query.accessToken
    const refreshToken = route.query.refreshToken

    if (accessToken !== undefined || refreshToken !== undefined) {
      if (accessToken && refreshToken) {
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)
        if (token) token.value = accessToken
        router.push('/')
      } else {
        alert('소셜 로그인 실패')
        router.push('/login')
      }
    }
  })

  // 소셜 로그인 URL 백엔드에서 받아 리디렉트
  const loginWith = async (provider) => {
    try {
      const res = await axios.get(`/login_api/oauth/authorize/${provider}`)
      const url = res.data.authorizationUrl
      window.location.href = url
    } catch (err) {
      const response = err.response?.data

      if (response?.res_code === 'need_register') {
        const { provider, providerId, nickname, profileImage } = response
        router.push({
          name: 'Member_SocialRegister',
          query: {
            provider,
            providerId,
            nickname,
            profileImage
          }
        })
      }// 로그인 성공 시 토큰 저장 및 페이지 이동 
      else if (response?.res_code === '200' && response.accessToken) {
        localStorage.setItem('accessToken', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken)
        token.value = response.accessToken
        router.push('/')
      } else {
        alert(`소셜 로그인 실패: ${provider}`)
        console.error(err)
      }
    }
  }

  return {
    userId,
    userPwd,
    login,
    loginWith
  }
}

// 회원 탈퇴
export function useDeleteMember() {
  const password = ref('')
  const token = inject('token')
  const logout = inject('logout')
  const loginType = inject('loginType')
  const userNo = inject('userNo')

  const onSubmit = async () => {
    if (!userNo?.value) {
      alert('회원 정보가 없습니다.')
      return
    }

    const confirmDelete = confirm('정말로 탈퇴하시겠습니까?')
    if (!confirmDelete) return

    try {
      const res = await axios.post('/login_api/user/deleteMember', {
        userNo: userNo.value,
        password: password.value,
        loginType: loginType.value
      }, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })

      alert(res.data.res_msg)

      if (res.data.res_code === '200') {
        logout()
      }
    } catch (err) {
      console.error('회원 탈퇴 오류:', err)
      alert('회원 탈퇴에 실패했습니다.')
    }
  }

  return {
    password,
    onSubmit
  }
}

// 마이페이지
export function useMemberDetail() {
  const token = inject('token')
  const userNo = inject('userNo')
  const loginType = inject('loginType')

  const member = ref(null)

  // 로컬 계정 비밀번호 변경 조건
  const showPasswordChangeBtn = computed(() => loginType?.value === 'local')

  onMounted(async () => {
    if (!userNo?.value) {
      console.warn('userNo가 없습니다.')
      return
    }

    try {
      const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo.value}`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      member.value = res.data.member
    } catch (err) {
      console.error('회원 정보 조회 실패:', err)
    }
  })

  return {
    member,
    showPasswordChangeBtn
  }
}

// 다른 유저 친구 목록 조회 및 친구 요청
export function useOtherFriendList() {
  const route = useRoute()
  const loginUserNo = inject('userNo')
  const token = inject('token')

  const friends = ref([])
  const member = ref({})

  const loadFriendList = async () => {
    const targetUserNo = route.query.userNo
    if (!targetUserNo || !loginUserNo?.value) return

    try {
      const res = await axios.get('/login_api/mypage/otherFriends', {
        params: {
          targetUserNo,
          loginUserNo: loginUserNo.value
        },
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })

      if (res.data?.res_code === '200') {
        friends.value = res.data.data
      }
    } catch (err) {
      console.error('친구 목록 조회 실패:', err)
    }
  }

  const requestFriend = async (targetUserNo) => {
    try {
      await axios.post('/login_api/mypage/request', {
        requesterNo: loginUserNo.value,
        requestedNo: targetUserNo
      }, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      alert('친구 요청을 보냈습니다.')
      await loadFriendList()
    } catch (error) {
      console.error('친구 요청 실패:', error)
      alert('친구 요청에 실패했습니다.')
    }
  }

  onMounted(async () => {
    const userNo = route.query.userNo
    if (!userNo) {
      console.warn('userNo 쿼리 파라미터가 없습니다.')
      return
    }

    try {
      const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo}`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      member.value = res.data.member
    } catch (err) {
      console.error('회원 정보 조회 실패:', err)
    }
    await loadFriendList()
  })

  return {
    loginUserNo,
    friends,
    member,
    requestFriend
  }
}

// 비밀번호 변경
export function useUpdatePassword() {
  const token = inject('token')
  const userNo = inject('userNo')
  const router = useRouter()

  const form = reactive({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  })

  const passwdMsg = ref('')

  const onSubmit = async () => {
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]).{8,}$/

    if (!pwRegex.test(form.newPassword)) {
      passwdMsg.value = '영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다'
      form.newPassword = ''
      form.confirmNewPassword = ''
      return
    }

    if (form.newPassword !== form.confirmNewPassword) {
      passwdMsg.value = '비밀번호가 일치하지 않습니다'
      form.newPassword = ''
      form.confirmNewPassword = ''
      return
    }

    passwdMsg.value = ''

    try {
      const response = await fetch('/login_api/user/updatePassword', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=utf-8',
          Authorization: `Bearer ${token.value}`
        },
        body: JSON.stringify({
          currentPassword: form.currentPassword,
          newPassword: form.newPassword,
          userNo: userNo.value
        })
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push({ name: 'Member_MyPage' })
      } else {
        alert(json.res_msg || '비밀번호 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error('비밀번호 변경 중 오류 발생:', error)
      alert('서버 오류로 비밀번호 변경에 실패했습니다.')
    }
  }

  const onCancel = () => {
    form.currentPassword = ''
    form.newPassword = ''
    form.confirmNewPassword = ''
    passwdMsg.value = ''
  }

  return {
    form,
    passwdMsg,
    onSubmit,
    onCancel
  }
}

// 회원 프로필 수정
export function useProfileUpdate() {
  const token = inject('token')
  const userNo = inject('userNo')
  const router = useRouter()

  const form = ref({
    userNo: '',
    userName: '',
    userComment: '',
    styleCode: '',
    statCode: ''
  })

  const fetchMemberDetail = async () => {
    if (!userNo?.value) {
      alert('회원 번호가 전달되지 않았습니다.')
      router.push('/')
      return
    }

    try {
      const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo.value}`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })

      const data = res.data.member
      form.value = {
        userNo: data.userNo,
        userName: data.userName,
        userComment: data.userComment,
        styleCode: data.styleCode ?? '',
        statCode: data.statCode ?? ''
      }
    } catch (err) {
      console.error('[회원 정보 조회 실패]', err)
      alert('회원 정보를 불러오지 못했습니다.')
      router.push('/')
    }
  }

  const onSubmit = async () => {
    try {
      const res = await axios.post(
        `/login_api/mypage/profileUpdate?userNo=${form.value.userNo}`,
        form.value,
        {
          headers: {
            Authorization: `Bearer ${token.value}`
          }
        }
      )

      const result = res.data
      alert(result.res_msg)

      if (result.res_code !== '400') {
        router.push({ name: 'Member_Profile', query: { userNo: form.value.userNo } })
      }
    } catch (err) {
      console.error('[회원 정보 수정 중 오류]', err)
      alert('회원 정보 수정 중 오류 발생')
    }
  }

  onMounted(fetchMemberDetail)

  return {
    form,
    onSubmit
  }
}

// 회원가입
export function useRegisterForm() {
  const router = useRouter()

  const form = reactive({
    userId: '',
    userPwd: '',
    userPwd2: '',
    userName: '',
    userBirth: '',
    userPhone: '',
    userPostcode: '',
    userAddr: '',
    userDetailAddr: '',
    userGender: ''
  })

  const useridMsg = ref('')
  const passwdMsg = ref('')
  const validClicked = ref(false)

  const smsCode = ref('')
  const smsCodeMsg = ref('')
  const smsCodeValid = ref(false)
  const isCodeSent = ref(false)

  const checkDuplicateId = async () => {
    if (!form.userId || form.userId.length < 8) {
      useridMsg.value = '아이디는 8자 이상이어야 합니다'
      validClicked.value = false
      return
    }

    try {
      const response = await fetch('/login_api/user/na/isExistUserId', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify({ userId: form.userId })
      })

      const json = await response.json()

      if (json.existUserId) {
        useridMsg.value = `${form.userId}는 이미 존재하는 아이디입니다`
        validClicked.value = false
      } else {
        useridMsg.value = ''
        alert(`${form.userId}는 사용 가능한 아이디입니다`)
        validClicked.value = true
      }
    } catch (error) {
      alert('중복 확인 중 오류가 발생했습니다')
      console.error(error)
      validClicked.value = false
    }
  }

  const sendSmsCode = async () => {
    if (!form.userPhone) {
      alert('전화번호를 입력해주세요')
      return
    }

    try {
      const checkRes = await fetch('/login_api/user/na/isExistPhone', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify({ userPhone: form.userPhone })
      })

      const checkJson = await checkRes.json()
      if (checkJson.res_code === '409') {
        alert(checkJson.res_msg)
        return
      }

      const res = await fetch('/login_api/user/na/smsCode', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ userPhone: form.userPhone })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        alert('인증 코드가 전송되었습니다')
        isCodeSent.value = true
      } else {
        alert(json.res_msg || '전송 실패')
      }
    } catch (err) {
      console.error('SMS 인증 오류:', err)
      alert('서버 오류로 인증 실패')
    }
  }

  const verifySmsCode = async () => {
    try {
      const res = await fetch('/login_api/user/na/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          userPhone: form.userPhone,
          smsCode: smsCode.value
        })
      })

      const json = await res.json()
      if (json.res_code === '200') {
        smsCodeMsg.value = '인증에 성공했습니다.'
        smsCodeValid.value = true
      } else {
        smsCodeMsg.value = json.res_msg || '인증 실패'
        smsCodeValid.value = false
      }
    } catch (err) {
      console.error('인증 오류:', err)
      smsCodeMsg.value = '서버 오류로 인증 실패'
      smsCodeValid.value = false
    }
  }

  const onSubmit = async () => {
    if (!validClicked.value) {
      alert('아이디 중복 확인을 해주세요')
      return
    }

    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]).{8,}$/
    if (!pwRegex.test(form.userPwd)) {
      passwdMsg.value = '영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다'
      form.userPwd = ''
      form.userPwd2 = ''
      return
    }

    if (form.userPwd !== form.userPwd2) {
      passwdMsg.value = '비밀번호가 일치하지 않습니다'
      form.userPwd = ''
      form.userPwd2 = ''
      return
    }

    passwdMsg.value = ''
    const payload = { ...form }
    delete payload.userPwd2

    if (!smsCodeValid.value) {
      alert('전화번호 인증을 완료해주세요')
      return
    }

    try {
      const response = await fetch('/login_api/user/na/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify(payload)
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push('/member/loginForm')
      } else {
        alert(json.res_msg || '회원가입에 실패했습니다.')
      }
    } catch (error) {
      console.error('회원가입 요청 중 오류 발생:', error)
      alert('서버 오류로 회원가입에 실패했습니다.')
    }
  }

  const onReset = () => {
    Object.keys(form).forEach(key => form[key] = '')
    useridMsg.value = ''
    passwdMsg.value = ''
    validClicked.value = false
  }

  const findZipcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        form.userPostcode = data.zonecode
        form.userAddr = data.address
      }
    }).open()
  }

  onMounted(() => {
    if (!window.daum?.Postcode) {
      const script = document.createElement('script')
      script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
      document.body.appendChild(script)
    }
  })

  watch(() => form.userId, () => {
    validClicked.value = false
  })

  watch(() => form.userPhone, (newVal, oldVal) => {
    if (newVal !== oldVal) {
      isCodeSent.value = false
      smsCodeValid.value = false
      smsCode.value = ''
      smsCodeMsg.value = ''
    }
  })

  return {
    form,
    useridMsg,
    passwdMsg,
    smsCode,
    smsCodeMsg,
    smsCodeValid,
    isCodeSent,
    checkDuplicateId,
    sendSmsCode,
    verifySmsCode,
    onSubmit,
    onReset,
    findZipcode
  }
}

// 비밀번호 초기화
export function useResetPassword() {
  const route = useRoute()
  const router = useRouter()

  const userNo = ref(route.query.userNo || null)

  const form = ref({
    newPassword: '',
    confirmNewPassword: ''
  })

  const passwdMsg = ref('')

  const onSubmit = async () => {
    if (!userNo.value) {
      alert('잘못된 접근입니다.')
      router.push('/member/loginForm')
      return
    }

    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]).{8,}$/

    if (!pwRegex.test(form.value.newPassword)) {
      passwdMsg.value = '영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다'
      form.value.newPassword = ''
      form.value.confirmNewPassword = ''
      return
    }

    if (form.value.newPassword !== form.value.confirmNewPassword) {
      passwdMsg.value = '비밀번호가 일치하지 않습니다'
      form.value.newPassword = ''
      form.value.confirmNewPassword = ''
      return
    }

    passwdMsg.value = ''

    try {
      const response = await fetch('/login_api/user/na/resetPassword', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
          newPassword: form.value.newPassword,
          userNo: userNo.value
        })
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push('/member/loginForm')
      } else {
        alert(json.res_msg || '비밀번호 변경에 실패했습니다.')
      }
    } catch (error) {
      console.error('비밀번호 변경 중 오류 발생:', error)
      alert('서버 오류로 비밀번호 변경에 실패했습니다.')
    }
  }

  const onCancel = () => {
    form.value.newPassword = ''
    form.value.confirmNewPassword = ''
    passwdMsg.value = ''
  }

  return {
    form,
    passwdMsg,
    onSubmit,
    onCancel
  }
}

// 소셜 로그인 유저 회원 가입
export function useSocialRegister() {
  const router = useRouter()
  const route = useRoute()

  const form = reactive({
    provider: '',
    providerId: '',
    userName: '',
    userBirth: '',
    userGender: '',
    userPhone: '',
    userPostcode: '',
    userAddr: '',
    userDetailAddr: ''
  })

  const onSubmit = async () => {
    form.userPhone = form.userPhone.replace(/[^0-9]/g, '')

    if (!form.userBirth || !form.userGender || !form.userPhone || !form.userPostcode || !form.userAddr) {
      alert('모든 필수 정보를 입력해주세요.')
      return
    }

    try {
      const response = await fetch('/login_api/oauth/na/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        body: JSON.stringify(form)
      })

      const json = await response.json()

      if (json.res_code === '200') {
        alert(json.res_msg)
        router.push({ path: '/member/loginForm' })
      } else {
        alert(json.res_msg || '회원가입에 실패했습니다.')
      }
    } catch (error) {
      console.error('회원가입 요청 중 오류 발생:', error)
      alert('서버 오류로 회원가입에 실패했습니다.')
    }
  }

  const onReset = () => {
    form.userBirth = ''
    form.userGender = ''
    form.userPhone = ''
    form.userPostcode = ''
    form.userAddr = ''
    form.userDetailAddr = ''
  }

  const findZipcode = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        form.userPostcode = data.zonecode
        form.userAddr = data.address
      }
    }).open()
  }

  onMounted(() => {
    const query = route.query

    if (!query.provider || !query.providerId || !query.nickname) {
      alert('잘못된 접근입니다.')
      router.replace('/member/loginForm')
      return
    }

    form.provider = query.provider
    form.providerId = query.providerId
    form.userName = query.nickname || query.name || '이름없음'

    if (!window.daum?.Postcode) {
      const script = document.createElement('script')
      script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
      document.body.appendChild(script)
    }
  })

  return {
    form,
    onSubmit,
    onReset,
    findZipcode
  }
}

// 회원 정보 수정
export function useUserInfoEdit() {
  const router = useRouter()
  const token = inject('token')
  const userNo = inject('userNo')

  const form = ref({
    userNo: '',
    userId: '',
    userName: '',
    userBirth: '',
    userPostCode: '',
    userAddr: '',
    userDetailAddr: '',
    userGender: ''
  })

  const originalData = ref({})
  let isPostcodeLoaded = false

  const fetchMemberDetail = async () => {
    if (!userNo?.value) {
      alert('로그인이 필요합니다.')
      router.push('/')
      return
    }

    try {
      const res = await axios.get(`/login_api/mypage/detailView?userNo=${userNo.value}`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })

      const data = res.data.member
      originalData.value = data
      console.log(data.userBirth)
      form.value = {
        userNo: data.userNo,
        userId: data.userId,
        userName: data.userName,
        userBirth: formatDate(data.userBirth),
        userPostCode: data.userPostCode,
        userAddr: data.userAddr,
        userDetailAddr: data.userDetailAddr,
        userGender: data.userGender
      }
    } catch (err) {
      console.error('[회원 정보 조회 실패]', err)
      alert('회원 정보를 불러오지 못했습니다.')
      router.push('/')
    }
  }

  const formatDate = (dateStr) => {
    if (!dateStr) return ''
    try {
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    } catch {
      console.warn('[날짜 파싱 실패]', dateStr)
      return ''
    }
  }

  const handleFindZipcode = () => {
    if (!window.daum || !window.daum.Postcode) {
      alert('우편번호 API가 아직 로딩되지 않았습니다.')
      return
    }

    new window.daum.Postcode({
      oncomplete: (data) => {
        form.value.userPostCode = data.zonecode
        form.value.userAddr = data.address
      }
    }).open()
  }

  const onSubmit = async () => {
    try {
      const res = await fetch(`/login_api/mypage/update?userNo=${form.value.userNo}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=utf-8',
          Authorization: `Bearer ${token.value}`
        },
        body: JSON.stringify(form.value)
      })

      const result = await res.json()
      alert(result.res_msg)
      if (result.res_code !== '400') {
        router.push({ name: 'Member_MyPage' })
      }
    } catch (err) {
      console.error('[회원 정보 수정 중 오류]', err)
      alert('회원 정보 수정 중 오류 발생')
    }
  }

  onMounted(() => {
    fetchMemberDetail()

    if (!window.daum?.Postcode && !isPostcodeLoaded) {
      const script = document.createElement('script')
      script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
      script.onload = () => {
        isPostcodeLoaded = true
        console.log('✅ 우편번호 스크립트 로드 완료')
      }
      script.onerror = () => {
        console.error('❌ 우편번호 스크립트 로드 실패')
      }
      document.body.appendChild(script)
    }
  })

  return {
    form,
    onSubmit,
    handleFindZipcode
  }
}
