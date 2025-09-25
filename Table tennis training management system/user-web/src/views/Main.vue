<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '@/store'
import { House, User, School, UserFilled, Calendar } from '@element-plus/icons-vue'

const userStore = useStore()
const router = useRouter()

// 菜单项
const menuItems = [
  { path: '/home', title: '主页', icon: 'House' },
  { path: '/campus', title: '校区', icon: 'School' },
  { path: '/coach', title: '教练', icon: 'UserFilled' },
  { path: '/course', title: '课程', icon: 'Calendar' },
  { path: '/profile', title: '个人中心', icon: 'User' }
]

// 用户信息
const userInfo = ref({})

// 获取用户信息
onMounted(async () => {
  if (userStore.token) {
    userInfo.value = await userStore.getUserInfo()
  } else {
    router.push('/login')
  }
})

// 退出登录
const logout = () => {
  userStore.removeToken()
  router.push('/login')
}
</script>

<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="logo">乒乓球培训管理系统</div>
      
      <div class="nav-menu">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path" 
          :to="item.path"
          class="nav-item"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </router-link>
      </div>
      
      <div class="user-section">
        <el-dropdown trigger="click">
          <span class="user-dropdown">
            <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span>{{ userInfo.username || '用户' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    
    <!-- 内容区域 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.nav-menu {
  display: flex;
  gap: 25px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  text-decoration: none;
}

.nav-item:hover {
  color: #409EFF;
}

.nav-item.router-link-active {
  color: #409EFF;
  font-weight: bold;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7f9;
}
</style> 