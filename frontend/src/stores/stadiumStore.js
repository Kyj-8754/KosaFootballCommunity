import { defineStore } from 'pinia'

export const StadiumDataStore = defineStore('stadium', {
  state: () => ({
    stadiumData: null
  }),
  actions: {
    setStadium(data) {
      this.stadiumData = data
    },
    clearStadium() {
      this.stadiumData = null
    }
  },
  persist: true
})
