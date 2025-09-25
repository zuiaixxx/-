<template>
  <div class="coach-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon">
          <UserFilled />
        </el-icon>
        教练展示
      </h1>
      <p class="page-subtitle">选择专业的乒乓球教练</p>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select
            v-model="searchForm.campusId"
            placeholder="选择校区"
            clearable
            @change="handleSearch"
          >
            <el-option label="全部校区" value="" />
            <el-option 
              v-for="campus in campusList" 
              :key="campus.id" 
              :label="campus.campusName" 
              :value="campus.id" 
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.level"
            placeholder="选择教练等级"
            clearable
            @change="handleSearch"
          >
            <el-option label="全部等级" value="" />
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 教练列表 -->
    <div class="coach-list" v-loading="loading">
      <el-row :gutter="24" v-if="coachList.length > 0">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="coach in coachList" :key="coach.id">
          <div class="coach-card">
            <!-- 教练头像 -->
            <div class="coach-avatar">
              <img 
                :src="coach.photoUrl || defaultCoachImage" 
                :alt="coach.realName"
                @error="handleImageError"
              />
              <div class="coach-level-tag">
                <el-tag :type="getCoachLevelType(coach.level)" size="small">
                  {{ coach.level }}
                </el-tag>
              </div>
            </div>

            <!-- 教练信息 -->
            <div class="coach-info">
              <h3 class="coach-name">{{ coach.realName }}</h3>
              <p class="coach-username">@{{ coach.username }}</p>
              
              <div class="info-item">
                <el-icon class="info-icon"><School /></el-icon>
                <span class="info-text">{{ getCampusName(coach.campusId) }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><Money /></el-icon>
                <span class="info-text">￥{{ coach.hourlyRate }}/小时</span>
              </div>

              <div class="info-item" v-if="coach.achievements">
                <el-icon class="info-icon"><Trophy /></el-icon>
                <span class="info-text">{{ coach.achievements }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><CircleCheck /></el-icon>
                <el-tag :type="getCoachStatusType(coach.status)" size="small">
                  {{ getCoachStatusText(coach.status) }}
                </el-tag>
              </div>

              <!-- 操作按钮 -->
              <div class="coach-actions">
                <el-button type="primary" size="small" @click="viewCoachDetail(coach)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button 
                  type="success" 
                  size="small" 
                  @click="selectCoach(coach)"
                  :disabled="coach.status !== 'approved'"
                >
                  <el-icon><UserFilled /></el-icon>
                  选择教练
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="!loading" 
        description="暂无教练数据"
        :image-size="200"
      >
        <el-button type="primary" @click="loadCoachList">刷新数据</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 教练详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedCoach?.realName"
      width="60%"
      center
    >
      <div v-if="selectedCoach" class="coach-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <img 
              :src="selectedCoach.photoUrl || defaultCoachImage" 
              :alt="selectedCoach.realName"
              class="detail-image"
            />
          </el-col>
          <el-col :span="12">
            <div class="detail-info">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="教练姓名">
                  {{ selectedCoach.realName }}
                </el-descriptions-item>
                <el-descriptions-item label="用户名">
                  {{ selectedCoach.username }}
                </el-descriptions-item>
                <el-descriptions-item label="教练等级">
                  <el-tag :type="getCoachLevelType(selectedCoach.level)">
                    {{ selectedCoach.level }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="所属校区">
                  {{ getCampusName(selectedCoach.campusId) }}
                </el-descriptions-item>
                <el-descriptions-item label="收费标准">
                  ￥{{ selectedCoach.hourlyRate }}/小时
                </el-descriptions-item>
                <el-descriptions-item label="比赛成绩" v-if="selectedCoach.achievements">
                  {{ selectedCoach.achievements }}
                </el-descriptions-item>
                <el-descriptions-item label="审核状态">
                  <el-tag :type="getCoachStatusType(selectedCoach.status)">
                    {{ getCoachStatusText(selectedCoach.status) }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="selectCoach(selectedCoach)"
            :disabled="selectedCoach?.status !== 'approved'"
          >
            选择教练
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCoachDetailList } from '@/api/coachDetail'
import { getCampusList } from '@/api/campus'
import { saveStudentCoach } from '@/api/studentCoach'
import { getUserByToken } from '@/api/user'
import { 
  UserFilled, Search, Refresh, School, Money, Trophy, CircleCheck, View
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const loading = ref(false)
const coachList = ref([])
const campusList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const detailDialogVisible = ref(false)
const selectedCoach = ref(null)
const currentUser = ref(null)

// 搜索表单
const searchForm = reactive({
  campusId: '',
  level: ''
})

// 默认教练头像
const defaultCoachImage = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 获取教练列表
const loadCoachList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      campusId: searchForm.campusId || undefined,
      level: searchForm.level || undefined
    }
    
    const response = await getCoachDetailList(params)
    if (response.code === 200) {
      coachList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取教练列表失败')
    }
  } catch (error) {
    console.error('获取教练列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取校区列表
const loadCampusList = async () => {
  try {
    const response = await getCampusList({ pageNum: 1, pageSize: 1000 })
    if (response.code === 200) {
      campusList.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取校区列表错误:', error)
  }
}

// 获取当前用户信息
const getCurrentUser = async () => {
  try {
    const response = await getUserByToken()
    if (response.code === 200) {
      currentUser.value = response.data
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadCoachList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.campusId = ''
  searchForm.level = ''
  currentPage.value = 1
  loadCoachList()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadCoachList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadCoachList()
}

// 获取校区名称
const getCampusName = (campusId) => {
  const campus = campusList.value.find(c => c.id == campusId)
  return campus ? campus.campusName : '未知校区'
}

// 获取教练等级标签类型
const getCoachLevelType = (level) => {
  const levelMap = {
    '初级': 'info',
    '中级': 'warning',
    '高级': 'danger'
  }
  return levelMap[level] || 'info'
}

// 获取教练状态标签类型
const getCoachStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取教练状态文本
const getCoachStatusText = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 教练图片错误处理
const handleImageError = (event) => {
  event.target.src = defaultCoachImage
}

// 查看教练详情
const viewCoachDetail = (coach) => {
  selectedCoach.value = coach
  detailDialogVisible.value = true
}

// 选择教练
const selectCoach = async (coach) => {
  if (coach.status !== 'approved') {
    ElMessage.warning('该教练尚未通过审核，无法选择')
    return
  }
  
  if (!currentUser.value) {
    ElMessage.error('获取用户信息失败')
    return
  }
  
  try {
    const studentCoachData = {
      userId: currentUser.value.id,
      userName: currentUser.value.realName || currentUser.value.username,
      coachId: coach.id,
      coachName: coach.realName,
      isApproved: false
    }
    
    const response = await saveStudentCoach(studentCoachData)
    if (response.code === 200) {
      ElMessage.success(`成功选择教练：${coach.realName}，请等待审核`)
      detailDialogVisible.value = false
    } else {
      ElMessage.error(response.msg || '选择教练失败')
    }
  } catch (error) {
    console.error('选择教练错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadCoachList()
  loadCampusList()
  getCurrentUser()
})
</script>

<style scoped>
.coach-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.search-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.coach-list {
  margin-bottom: 24px;
}

.coach-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  margin-bottom: 24px;
  height: 100%;
}

.coach-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.coach-avatar {
  position: relative;
  text-align: center;
  padding: 20px;
}

.coach-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f0f0;
}

.coach-level-tag {
  position: absolute;
  top: 15px;
  right: 15px;
}

.coach-info {
  padding: 0 20px 20px;
  text-align: center;
}

.coach-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.coach-username {
  font-size: 12px;
  color: #8492a6;
  margin: 0 0 16px 0;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.info-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.info-text {
  word-break: break-all;
}

.coach-actions {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  justify-content: center;
}

.coach-actions .el-button {
  flex: 1;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.coach-detail .detail-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-info {
  height: 100%;
  display: flex;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coach-container {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .coach-card {
    margin-bottom: 16px;
  }
  
  .coach-info {
    padding: 16px;
  }
  
  .coach-actions {
    flex-direction: column;
  }
  
  .coach-actions .el-button {
    width: 100%;
  }
}
</style>