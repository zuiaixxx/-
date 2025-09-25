<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, Check, User, Avatar } from '@element-plus/icons-vue'

// 模拟系统公告数据
const announcements = ref([
  { id: 1, title: '乒乓球培训课程优惠活动', content: '本月新学员报名享受8折优惠，老学员推荐新学员可获得免费课程体验券。', date: '2025-09-25' },
  { id: 2, title: '教练团队介绍', content: '我们聘请了多位专业乒乓球教练，具有丰富的教学经验和比赛经历，为学员提供专业指导。', date: '2025-09-20' },
  { id: 3, title: '场馆设施升级完成', content: '场馆新增了专业比赛台和训练设备，为学员提供更好的训练环境和学习体验。', date: '2025-09-15' }
])

// 模拟统计数据
const statistics = ref({
  totalCourses: 48,
  completedCourses: 156,
  activeStudents: 89,
  coachCount: 12
})
</script>

<template>
  <div class="home-container">
    <h1>欢迎使用乒乓球培训管理系统</h1>
    
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>本月课程</span>
            <el-icon><Calendar /></el-icon>
          </div>
        </template>
        <div class="card-value">{{ statistics.totalCourses }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>已完成课程</span>
            <el-icon><Check /></el-icon>
          </div>
        </template>
        <div class="card-value">{{ statistics.completedCourses }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>在线学员</span>
            <el-icon><User /></el-icon>
          </div>
        </template>
        <div class="card-value">{{ statistics.activeStudents }}</div>
      </el-card>
      
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="card-header">
            <span>专业教练</span>
            <el-icon><Avatar /></el-icon>
          </div>
        </template>
        <div class="card-value">{{ statistics.coachCount }}</div>
      </el-card>
    </div>
    
    <!-- 系统公告 -->
    <div class="announcement-section">
      <h2>培训公告</h2>
      <el-card class="announcement-card" v-for="item in announcements" :key="item.id">
        <div class="announcement-header">
          <h3>{{ item.title }}</h3>
          <span class="announcement-date">{{ item.date }}</span>
        </div>
        <p>{{ item.content }}</p>
      </el-card>
    </div>
    
    <!-- 操作指南 -->
    <div class="guide-section">
      <h2>学习流程</h2>
      <el-steps :active="4" finish-status="success" direction="horizontal">
        <el-step title="选择教练" description="浏览教练信息并选择合适的教练" />
        <el-step title="预约课程" description="选择时间段和场地预约课程" />
        <el-step title="完成支付" description="在线支付课程费用" />
        <el-step title="参与课程" description="按时参与课程并进行评价" />
      </el-steps>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  padding: 20px;
}

h1 {
  margin-bottom: 30px;
  font-size: 24px;
  text-align: center;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.announcement-section, .guide-section {
  margin-top: 30px;
}

h2 {
  margin-bottom: 20px;
  font-size: 20px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}

.announcement-card {
  margin-bottom: 15px;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.announcement-header h3 {
  margin: 0;
  font-size: 16px;
}

.announcement-date {
  color: #999;
  font-size: 12px;
}

.guide-section {
  margin-top: 40px;
  margin-bottom: 40px;
}
</style> 