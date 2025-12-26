<template>
  <div class="navbar">
    <div class="left-menu">
      <el-button class="menu-toggle" @click="toggleSidebar" link>
        <el-icon size="24">
          <Expand v-if="!sidebar.opened" />
          <Fold v-else />
        </el-icon>
      </el-button>
    </div>
    
    <div class="right-menu">
      <div class="user-info">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            {{ user?.fullName || user?.username }}
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useSidebarStore } from '@/stores/sidebar'
import { Expand, Fold, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const sidebar = useSidebarStore()

const user = computed(() => authStore.user)

const toggleSidebar = () => {
  sidebar.toggleSidebar()
}

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.left-menu {
  display: flex;
  align-items: center;
}

.menu-toggle {
  padding: 0;
  margin-right: 20px;
}

.right-menu {
  display: flex;
  align-items: center;
}

.user-info {
  margin-left: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #333;
  display: flex;
  align-items: center;
}
</style>