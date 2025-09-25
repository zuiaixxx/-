<template>
  <div class="student-coach-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>我的学生</h2>
      <el-button type="primary" @click="loadMyStudent">刷新</el-button>
    </div>

    <!-- 学生列表 -->
    <div class="table-container">
      <el-table 
        :data="studentList" 
        style="width: 100%" 
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="学员姓名" width="120" />
        <el-table-column prop="coachName" label="教练姓名" width="120" />
        <el-table-column prop="isApproved" label="审批状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.isApproved ? 'success' : 'warning'" 
              size="small"
            >
              {{ scope.row.isApproved ? '已同意' : '待审批' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                v-if="!scope.row.isApproved"
                type="success"
                size="small"
                @click="handleApprove(scope.row)"
              >
                同意
              </el-button>
              <el-button
                v-if="scope.row.isApproved"
                type="warning"
                size="small"
                @click="handleReject(scope.row)"
              >
                撤销
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && studentList.length === 0" class="empty-state">
      <el-empty description="暂无学生数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyStudent, saveStudentCoach } from '@/api/studentCoach'

// 响应式数据
const loading = ref(false)
const studentList = ref([])

// 获取我的学生列表
const loadMyStudent = async () => {
  loading.value = true
  try {
    const response = await getMyStudent()
    if (response.code === 200) {
      studentList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取学生列表失败')
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 同意学生申请
const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确定要同意 "${row.userName}" 的师生关系申请吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await saveStudentCoach({
        id: row.id,
        userId: row.userId,
        userName: row.userName,
        coachId: row.coachId,
        coachName: row.coachName,
        isApproved: true
      })
      if (response.code === 200) {
        ElMessage.success('同意成功')
        loadMyStudent()
      } else {
        ElMessage.error(response.msg || '同意失败')
      }
    } catch (error) {
      console.error('同意失败:', error)
      ElMessage.error('同意失败')
    }
  })
}

// 撤销同意
const handleReject = (row) => {
  ElMessageBox.confirm(
    `确定要撤销对 "${row.userName}" 的同意吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await saveStudentCoach({
        id: row.id,
        userId: row.userId,
        userName: row.userName,
        coachId: row.coachId,
        coachName: row.coachName,
        isApproved: false
      })
      if (response.code === 200) {
        ElMessage.success('撤销成功')
        loadMyStudent()
      } else {
        ElMessage.error(response.msg || '撤销失败')
      }
    } catch (error) {
      console.error('撤销失败:', error)
      ElMessage.error('撤销失败')
    }
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadMyStudent()
})
</script>

<style scoped>
.student-coach-container {
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

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-buttons .el-button {
  border-radius: 4px;
}

.empty-state {
  margin-top: 40px;
  text-align: center;
}
</style>