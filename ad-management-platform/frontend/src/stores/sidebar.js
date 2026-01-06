import { defineStore } from 'pinia'

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    opened: true
  }),
  
  actions: {
    toggleSidebar() {
      this.opened = !this.opened
    },
    
    closeSidebar() {
      this.opened = false
    },
    
    openSidebar() {
      this.opened = true
    }
  }
})