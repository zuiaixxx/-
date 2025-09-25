<script setup>
import { ref, reactive } from 'vue'
import { useStore } from '@/store'

const userStore = useStore()

// 当前激活的标签
const activeTab = ref('all')

// 分类选项
const categories = [
  { name: 'all', label: '全部' },
  { name: '讨论', label: '讨论' },
  { name: '求助', label: '求助' },
  { name: '教程', label: '教程' },
  { name: '分享', label: '分享' }
]

// 发帖表单
const postForm = reactive({
  title: '',
  content: '',
  category: '讨论'
})

// 发布按钮状态
const loading = ref(false)

// 发布帖子
const submitPost = () => {
  if (postForm.title && postForm.content) {
    loading.value = true
    // 模拟发布
    setTimeout(() => {
      ElMessage.success('发布成功')
      loading.value = false
      postForm.title = ''
      postForm.content = ''
    }, 1000)
  } else {
    ElMessage.warning('请填写标题和内容')
  }
}
</script>

<template>
  <div class="forum-container">
    <h1>校园论坛</h1>
    
    <!-- 发帖区域 -->
    <el-card class="post-card">
      <template #header>
        <div class="card-header">
          <span>发布新帖</span>
          <el-select v-model="postForm.category" size="small">
            <el-option
              v-for="item in categories.filter(c => c.name !== 'all')"
              :key="item.name"
              :label="item.label"
              :value="item.name"
            />
          </el-select>
        </div>
      </template>
      
      <el-input
        v-model="postForm.title"
        placeholder="帖子标题"
        class="post-title-input"
      />
      
      <el-input
        v-model="postForm.content"
        type="textarea"
        :rows="4"
        placeholder="帖子内容"
        class="post-content-input"
      />
      
      <div class="post-actions">
        <el-button type="primary" :loading="loading" @click="submitPost">发布</el-button>
      </div>
    </el-card>
    
    <!-- 分类标签 -->
    <div class="category-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane 
          v-for="category in categories" 
          :key="category.name" 
          :label="category.label" 
          :name="category.name"
        >
          <el-empty description="暂无内容"></el-empty>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.forum-container {
  padding: 20px;
}

h1 {
  margin-bottom: 20px;
  font-size: 24px;
}

.post-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-title-input {
  margin-bottom: 15px;
}

.post-content-input {
  margin-bottom: 15px;
}

.post-actions {
  display: flex;
  justify-content: flex-end;
}

.category-tabs {
  margin-top: 30px;
}
</style> 