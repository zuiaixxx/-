<template>
  <div class="my-evaluation-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>我的评价</h2>
      <el-button type="primary" @click="loadMyEvaluation">刷新</el-button>
    </div>

    <!-- 评价记录列表 -->
    <div class="table-container">
      <el-table 
        :data="evaluationList" 
        style="width: 100%" 
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookingNo" label="预约单号" width="150" />
        <el-table-column prop="studentUsername" label="学员用户名" width="120" />
        <el-table-column prop="studentFeedback" label="学员反馈" width="200" show-overflow-tooltip />
        <el-table-column prop="studentRating" label="学员评分" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.studentRating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="coachFeedback" label="教练反馈" width="200" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.coachFeedback">{{ scope.row.coachFeedback }}</span>
            <el-tag v-else type="warning" size="small">待反馈</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachRating" label="教练评分" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-if="scope.row.coachRating"
              v-model="scope.row.coachRating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
            <el-tag v-else type="warning" size="small">未评分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleReply(scope.row)"
            >
              {{ scope.row.coachFeedback ? '编辑反馈' : '添加反馈' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && evaluationList.length === 0" class="empty-state">
      <el-empty description="暂无评价数据" />
    </div>

    <!-- 反馈对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="教练反馈"
      width="600px"
    >
      <el-form :model="replyForm" label-width="100px">
        <el-form-item label="预约单号">
          <el-input v-model="replyForm.bookingNo" disabled />
        </el-form-item>
        <el-form-item label="学员反馈">
          <el-input
            v-model="replyForm.studentFeedback"
            type="textarea"
            :rows="3"
            disabled
          />
        </el-form-item>
        <el-form-item label="学员评分">
          <el-rate
            v-model="replyForm.studentRating"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}星"
          />
        </el-form-item>
        <el-form-item label="教练反馈" required>
          <el-input
            v-model="replyForm.coachFeedback"
            type="textarea"
            :rows="4"
            placeholder="请输入您的反馈内容"
          />
        </el-form-item>
        <el-form-item label="教练评分" required>
          <el-rate
            v-model="replyForm.coachRating"
            show-text
            text-color="#ff9900"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReply">确认提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCoachEvaluation, saveEvaluationRecord } from '@/api/evaluationRecord'

// 响应式数据
const loading = ref(false)
const evaluationList = ref([])
const replyDialogVisible = ref(false)
const currentEvaluation = ref(null)

// 反馈表单
const replyForm = reactive({
  id: null,
  bookingNo: '',
  studentUsername: '',
  studentFeedback: '',
  studentRating: 0,
  coachUsername: '',
  coachFeedback: '',
  coachRating: 0
})

// 获取我的评价记录列表
const loadMyEvaluation = async () => {
  loading.value = true
  try {
    const response = await getCoachEvaluation()
    if (response.code === 200) {
      evaluationList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取评价记录失败')
    }
  } catch (error) {
    console.error('获取评价记录失败:', error)
    ElMessage.error('获取评价记录失败')
  } finally {
    loading.value = false
  }
}

// 处理回复
const handleReply = (row) => {
  currentEvaluation.value = row
  
  // 重置表单
  Object.keys(replyForm).forEach(key => {
    replyForm[key] = row[key] || ''
  })
  
  replyDialogVisible.value = true
}

// 确认回复
const confirmReply = async () => {
  if (!replyForm.coachFeedback.trim()) {
    ElMessage.warning('请输入教练反馈内容')
    return
  }
  
  if (!replyForm.coachRating) {
    ElMessage.warning('请选择教练评分')
    return
  }
  
  try {
    const response = await saveEvaluationRecord({
      ...currentEvaluation.value,
      coachFeedback: replyForm.coachFeedback,
      coachRating: replyForm.coachRating
    })
    
    if (response.code === 200) {
      ElMessage.success('反馈提交成功')
      replyDialogVisible.value = false
      loadMyEvaluation()
    } else {
      ElMessage.error(response.msg || '反馈提交失败')
    }
  } catch (error) {
    console.error('反馈提交失败:', error)
    ElMessage.error('反馈提交失败')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadMyEvaluation()
})
</script>

<style scoped>
.my-evaluation-container {
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

.dialog-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.dialog-footer .el-button {
  border-radius: 4px;
}
</style>