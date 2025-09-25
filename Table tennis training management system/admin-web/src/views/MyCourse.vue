<template>
  <div class="my-course-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>我的课程</h2>
      <el-button type="primary" @click="loadMyCourse">刷新</el-button>
    </div>

    <!-- 课程列表 -->
    <div class="table-container">
      <el-table 
        :data="courseList" 
        style="width: 100%" 
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coachName" label="教练姓名" width="120" />
        <el-table-column prop="campusId" label="校区编号" width="100" />
        <el-table-column prop="date" label="课程日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="available" label="可预约" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.available ? 'success' : 'danger'" 
              size="small"
            >
              {{ scope.row.available ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courtNumber" label="球台编号" width="100" />
        <el-table-column prop="img" label="课程图片" width="120">
          <template #default="scope">
            <el-image
              v-if="scope.row.img"
              :src="scope.row.img"
              style="width: 60px; height: 40px"
              fit="cover"
              :preview-src-list="[scope.row.img]"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && courseList.length === 0" class="empty-state">
      <el-empty description="暂无课程数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCourse } from '@/api/course'

// 响应式数据
const loading = ref(false)
const courseList = ref([])

// 获取我的课程列表
const loadMyCourse = async () => {
  loading.value = true
  try {
    const response = await getMyCourse()
    if (response.code === 200) {
      courseList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadMyCourse()
})
</script>

<style scoped>
.my-course-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  text-align: left;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.page-header .el-button {
  border-radius: 4px;
}

.table-container {
  margin-bottom: 20px;
}

.el-table {
  text-align: left;
}

.empty-state {
  margin-top: 40px;
  text-align: center;
}
</style>