import { defineStore } from 'pinia'

export const useMemberStore = defineStore('member', {
  state: () => ({
    userid: '',
    supervisor: '',
    authenticated: false
  }),
  actions: {
    setMember(member) {
      this.userid = member.userid
      this.supervisor = member.supervisor
      this.authenticated = member.authenticated
    },
    clearMember() {
      this.userid = ''
      this.supervisor = ''
      this.authenticated = false
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        storage: localStorage, 
        paths: ['userid', 'supervisor', 'authenticated'],
      }
    ]
  }
})