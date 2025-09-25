<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '@/store'
import { 
  User, 
  Calendar, 
  Star, 
  Coin, 
  Connection, 
  Reading, 
  Avatar 
} from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()
const currentTime = ref('')

// 获取用户名称
const getUserName = () => {
  const userInfo = store.state.userInfo
  if (userInfo) {
    return userInfo.name || userInfo.username || '用户'
  }
  return '用户'
}

// 判断用户角色
const isCoach = () => {
  return store.state.userInfo?.role === 'coach'
}

const isSuperAdmin = () => {
  return store.state.userInfo?.role === 'superAdmin'
}

const isCampusAdmin = () => {
  return store.state.userInfo?.role === 'campusAdmin'
}

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 定时器
let timeInterval = null

// 快速操作函数
const goToMyStudents = () => {
  router.push('/studentCoach')
}

const goToMyCourses = () => {
  router.push('/myCourse')
}

const goToMyBookings = () => {
  router.push('/myBooking')
}

const goToUserManagement = () => {
  router.push('/user')
}

const goToCoachManagement = () => {
  router.push('/coachDetail')
}

// 生命周期
onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 60000) // 每分钟更新
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1>🏓 乒乓球培训管理系统</h1>
        <p class="welcome-text">欢迎回来，{{ getUserName() }}！</p>
        <p class="current-time">{{ currentTime }}</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="30" color="#409eff"><User /></el-icon>
        </div>
        <div class="stat-content">
          <h3>今日活跃用户</h3>
          <p class="stat-number">128</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="30" color="#67c23a"><Calendar /></el-icon>
        </div>
        <div class="stat-content">
          <h3>今日预约</h3>
          <p class="stat-number">45</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="30" color="#e6a23c"><Star /></el-icon>
        </div>
        <div class="stat-content">
          <h3>本月评价</h3>
          <p class="stat-number">256</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="30" color="#f56c6c"><Coin /></el-icon>
        </div>
        <div class="stat-content">
          <h3>本月收入</h3>
          <p class="stat-number">￥8,520</p>
        </div>
      </div>
    </div>

    <!-- 功能区域 -->
    <div class="function-area">
      <!-- 快速操作 -->
      <div class="quick-actions">
        <h2>快速操作</h2>
        <div class="action-buttons">
          <el-button 
            v-if="isCoach()" 
            type="primary" 
            @click="goToMyStudents"
            class="action-btn"
          >
            <el-icon><Connection /></el-icon>
            查看我的学生
          </el-button>
          
          <el-button 
            v-if="isCoach()" 
            type="success" 
            @click="goToMyCourses"
            class="action-btn"
          >
            <el-icon><Reading /></el-icon>
            查看我的课程
          </el-button>
          
          <el-button 
            v-if="isCoach()" 
            type="warning" 
            @click="goToMyBookings"
            class="action-btn"
          >
            <el-icon><Calendar /></el-icon>
            管理预约
          </el-button>
          
          <el-button 
            v-if="isSuperAdmin()" 
            type="primary" 
            @click="goToUserManagement"
            class="action-btn"
          >
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
          
          <el-button 
            v-if="isSuperAdmin() || isCampusAdmin()" 
            type="info" 
            @click="goToCoachManagement"
            class="action-btn"
          >
            <el-icon><Avatar /></el-icon>
            教练管理
          </el-button>
        </div>
      </div>

      <!-- 最近活动 -->
      <div class="recent-activities">
        <h2>最近活动</h2>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-icon">
              <el-icon color="#409eff"><User /></el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-title">新用户注册</p>
              <p class="activity-desc">张三同学注册了账号</p>
              <p class="activity-time">5分钟前</p>
            </div>
          </div>
          
          <div class="activity-item">
            <div class="activity-icon">
              <el-icon color="#67c23a"><Calendar /></el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-title">课程预约</p>
              <p class="activity-desc">李四预约了明天的乒乓球课程</p>
              <p class="activity-time">10分钟前</p>
            </div>
          </div>
          
          <div class="activity-item">
            <div class="activity-icon">
              <el-icon color="#e6a23c"><Star /></el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-title">课程评价</p>
              <p class="activity-desc">王五对课程给出了5星好评</p>
              <p class="activity-time">15分钟前</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统公告 -->
    <div class="announcements">
      <h2>系统公告</h2>
      <el-card class="announcement-card">
        <div class="announcement-item">
          <h3>🎉 系统升级完成</h3>
          <p>系统已完成最新版本升级，新增了评价管理功能，欢迎体验！</p>
          <p class="announcement-date">2024-09-25</p>
        </div>
      </el-card>
      
      <el-card class="announcement-card">
        <div class="announcement-item">
          <h3>📅 国庆节放假通知</h3>
          <p>国庆节期间（10月1日-10月7日）系统正常运行，请合理安排课程时间。</p>
          <p class="announcement-date">2024-09-20</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;
  text-align: left;
}

.banner-content h1 {
  font-size: 28px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.welcome-text {
  font-size: 18px;
  margin: 10px 0;
  opacity: 0.9;
}

.current-time {
  font-size: 14px;
  margin: 5px 0 0 0;
  opacity: 0.8;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  margin-right: 16px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
}

.stat-content {
  text-align: left;
}

.stat-content h3 {
  font-size: 14px;
  color: #909399;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.stat-number {
  font-size: 24px;
  color: #303133;
  margin: 0;
  font-weight: 600;
}

/* 功能区域 */
.function-area {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .function-area {
    grid-template-columns: 1fr;
  }
}

.quick-actions,
.recent-activities {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.quick-actions h2,
.recent-activities h2 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
  text-align: left;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.action-btn {
  border-radius: 6px;
  padding: 12px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn .el-icon {
  font-size: 16px;
}

/* 最近活动 */
.activity-list {
  space-y: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 12px;
  padding: 8px;
  border-radius: 6px;
  background: #f5f7fa;
}

.activity-content {
  flex: 1;
  text-align: left;
}

.activity-title {
  font-size: 14px;
  color: #303133;
  margin: 0 0 4px 0;
  font-weight: 500;
}

.activity-desc {
  font-size: 13px;
  color: #606266;
  margin: 0 0 4px 0;
}

.activity-time {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* 系统公告 */
.announcements {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.announcements h2 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
  text-align: left;
}

.announcement-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.announcement-card:last-child {
  margin-bottom: 0;
}

.announcement-item {
  text-align: left;
}

.announcement-item h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.announcement-item p {
  font-size: 14px;
  color: #606266;
  margin: 0 0 8px 0;
  line-height: 1.6;
}

.announcement-date {
  font-size: 12px;
  color: #909399 !important;
  margin: 8px 0 0 0 !important;
}
</style>