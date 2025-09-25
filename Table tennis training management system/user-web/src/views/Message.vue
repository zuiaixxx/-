<script setup>
import { ref, reactive } from 'vue'
import { useStore } from '@/store'

const userStore = useStore()

// 留言表单
const messageForm = reactive({
  title: '',
  content: '',
  type: '建议'
})

// 留言类型选项
const messageTypes = [
  { label: '建议', value: '建议' },
  { label: '反馈', value: '反馈' },
  { label: '咨询', value: '咨询' },
  { label: '投诉', value: '投诉' }
]

// 表单验证规则
const messageRules = {
  title: [
    { required: true, message: '请输入留言标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择留言类型', trigger: 'change' }
  ]
}

// 表单引用
const messageFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 提交留言
const submitMessage = async () => {
  if (messageFormRef.value) {
    await messageFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        // 模拟提交请求
        setTimeout(() => {
          ElMessage.success('留言提交成功')
          messageForm.title = ''
          messageForm.content = ''
          messageForm.type = '建议'
          loading.value = false
        }, 1000)
      }
    })
  }
}
</script>

<template>
  <div class="message-container">
    <h1>留言板</h1>
    
    <!-- 留言表单 -->
    <el-card class="message-form-card">
      <template #header>
        <div class="card-header">
          <span>提交新留言</span>
        </div>
      </template>
      
      <el-form
        ref="messageFormRef"
        :model="messageForm"
        :rules="messageRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="messageForm.title" placeholder="请输入留言标题" />
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-select v-model="messageForm.type" placeholder="请选择留言类型" style="width: 100%">
            <el-option
              v-for="item in messageTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="messageForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入留言内容"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="submitMessage"
          >
            提交留言
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.message-container {
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
  font-size: 24px;
}

.message-form-card {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}
</style> 