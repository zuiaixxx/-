<template>
  <div class="campus-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon">
          <School />
        </el-icon>
        校区展示
      </h1>
      <p class="page-subtitle">探索我们的乒乓球培训校区</p>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索校区名称、地址或联系人"
            prefix-icon="Search"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.campusType"
            placeholder="选择校区类型"
            clearable
            @change="handleSearch"
          >
            <el-option label="全部类型" value="" />
            <el-option label="分校区" value="B" />
            <el-option label="中心校区" value="C" />
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

    <!-- 校区列表 -->
    <div class="campus-list" v-loading="loading">
      <el-row :gutter="24" v-if="campusList.length > 0">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="campus in campusList" :key="campus.id">
          <div class="campus-card">
            <!-- 校区图片 -->
            <div class="campus-image">
              <img 
                :src="campus.imageUrl || defaultImage" 
                :alt="campus.campusName"
                @error="handleImageError"
              />
              <div class="campus-type-tag">
                <el-tag :type="getTypeTagType(campus.campusType)" size="small">
                  {{ getCampusTypeText(campus.campusType) }}
                </el-tag>
              </div>
            </div>

            <!-- 校区信息 -->
            <div class="campus-info">
              <h3 class="campus-name">{{ campus.campusName }}</h3>
              
              <div class="info-item">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">{{ campus.address }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><User /></el-icon>
                <span class="info-text">{{ campus.contactPerson }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><Phone /></el-icon>
                <span class="info-text">{{ campus.phone }}</span>
              </div>

              <div class="info-item" v-if="campus.email">
                <el-icon class="info-icon"><Message /></el-icon>
                <span class="info-text">{{ campus.email }}</span>
              </div>

              <!-- 操作按钮 -->
              <div class="campus-actions">
                <el-button type="primary" size="small" @click="viewCampusDetail(campus)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button type="success" size="small" @click="selectCoach(campus)">
                  <el-icon><UserFilled /></el-icon>
                  选择教练
                </el-button>
                <el-button type="warning" size="small" @click="bookCourse(campus)">
                  <el-icon><Calendar /></el-icon>
                  预约课程
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="!loading" 
        description="暂无校区数据"
        :image-size="200"
      >
        <el-button type="primary" @click="loadCampusList">刷新数据</el-button>
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

    <!-- 校区详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedCampus?.campusName"
      width="60%"
      center
    >
      <div v-if="selectedCampus" class="campus-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <img 
              :src="selectedCampus.imageUrl || defaultImage" 
              :alt="selectedCampus.campusName"
              class="detail-image"
            />
          </el-col>
          <el-col :span="12">
            <div class="detail-info">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="校区名称">
                  {{ selectedCampus.campusName }}
                </el-descriptions-item>
                <el-descriptions-item label="校区类型">
                  <el-tag :type="getTypeTagType(selectedCampus.campusType)">
                    {{ getCampusTypeText(selectedCampus.campusType) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="详细地址">
                  {{ selectedCampus.address }}
                </el-descriptions-item>
                <el-descriptions-item label="联系人">
                  {{ selectedCampus.contactPerson }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">
                  {{ selectedCampus.phone }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱" v-if="selectedCampus.email">
                  {{ selectedCampus.email }}
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">
                  {{ formatDate(selectedCampus.createdTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="更新时间" v-if="selectedCampus.updatedTime">
                  {{ formatDate(selectedCampus.updatedTime) }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="success" @click="selectCoach(selectedCampus)">
            选择教练
          </el-button>
          <el-button type="primary" @click="bookCourse(selectedCampus)">
            预约课程
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 教练选择弹窗 -->
    <el-dialog
      v-model="coachDialogVisible"
      :title="`选择${selectedCampus?.campusName}的教练`"
      width="80%"
      center
    >
      <div class="coach-selection">
        <!-- 教练搜索 -->
        <div class="coach-search">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-select
                v-model="coachSearchForm.level"
                placeholder="选择教练等级"
                clearable
                @change="searchCoaches"
              >
                <el-option label="全部等级" value="" />
                <el-option label="初级" value="初级" />
                <el-option label="中级" value="中级" />
                <el-option label="高级" value="高级" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="searchCoaches" :loading="coachLoading">
                搜索
              </el-button>
            </el-col>
            <el-col :span="4">
              <el-button @click="resetCoachSearch">
                重置
              </el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 教练列表 -->
        <div class="coach-list" v-loading="coachLoading">
          <el-row :gutter="16" v-if="coachList.length > 0">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="coach in coachList" :key="coach.id">
              <div class="coach-card" :class="{ 'selected': selectedCoach?.id === coach.id }">
                <div class="coach-avatar">
                  <img 
                    :src="coach.photoUrl || defaultCoachImage" 
                    :alt="coach.realName"
                    @error="handleCoachImageError"
                  />
                  <div class="coach-level-tag">
                    <el-tag :type="getCoachLevelType(coach.level)" size="small">
                      {{ coach.level }}
                    </el-tag>
                  </div>
                </div>
                
                <div class="coach-info">
                  <h4 class="coach-name">{{ coach.realName }}</h4>
                  <p class="coach-username">@{{ coach.username }}</p>
                  
                  <div class="coach-details">
                    <div class="detail-item">
                      <span class="label">收费：</span>
                      <span class="value">￥{{ coach.hourlyRate }}/小时</span>
                    </div>
                    
                    <div class="detail-item" v-if="coach.achievements">
                      <span class="label">成绩：</span>
                      <span class="value">{{ coach.achievements }}</span>
                    </div>
                    
                    <div class="detail-item">
                      <span class="label">状态：</span>
                      <el-tag :type="getCoachStatusType(coach.status)" size="small">
                        {{ getCoachStatusText(coach.status) }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <div class="coach-actions">
                    <el-button 
                      :type="selectedCoach?.id === coach.id ? 'success' : 'primary'"
                      size="small" 
                      @click="selectCoachItem(coach)"
                      :disabled="coach.status !== 'approved'"
                    >
                      {{ selectedCoach?.id === coach.id ? '已选中' : '选择教练' }}
                    </el-button>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <!-- 无数据状态 -->
          <el-empty 
            v-else-if="!coachLoading" 
            description="该校区暂无可用教练"
            :image-size="120"
          />
        </div>
        
        <!-- 教练分页 -->
        <div class="coach-pagination" v-if="coachTotal > 0">
          <el-pagination
            v-model:current-page="coachCurrentPage"
            v-model:page-size="coachPageSize"
            :page-sizes="[8, 16, 24]"
            :total="coachTotal"
            layout="total, sizes, prev, pager, next"
            @size-change="handleCoachSizeChange"
            @current-change="handleCoachCurrentChange"
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="coachDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmCoachSelection"
            :disabled="!selectedCoach"
          >
            确认选择
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 课程预约弹窗 -->
    <el-dialog
      v-model="courseDialogVisible"
      :title="`预约${selectedCampus?.campusName}的课程`"
      width="80%"
      center
    >
      <div class="course-selection">
        <!-- 课程搜索 -->
        <div class="course-search">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-input
                v-model="courseSearchForm.coachUsername"
                placeholder="搜索教练姓名或用户名"
                prefix-icon="Search"
                clearable
                @keyup.enter="searchCourses"
                @input="searchCourses"
              />
            </el-col>
            <el-col :span="6">
              <el-select
                v-model="courseSearchForm.available"
                placeholder="课程状态"
                @change="searchCourses"
              >
                <el-option label="全部课程" :value="null" />
                <el-option label="可预约" :value="true" />
                <el-option label="不可预约" :value="false" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="searchCourses" :loading="courseLoading">
                搜索
              </el-button>
            </el-col>
            <el-col :span="4">
              <el-button @click="resetCourseSearch">
                重置
              </el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 课程列表 -->
        <div class="course-list" v-loading="courseLoading">
          <el-row :gutter="16" v-if="paginatedCourseList.length > 0">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in paginatedCourseList" :key="course.id">
              <div class="course-card" :class="{ 'selected': selectedCourse?.id === course.id, 'disabled': !course.available }">
                <div class="course-image">
                  <img 
                    :src="course.img || 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'" 
                    :alt="course.coachName"
                  />
                  <div class="course-status-tag">
                    <el-tag :type="getCourseStatusType(course.available)" size="small">
                      {{ getCourseStatusText(course.available) }}
                    </el-tag>
                  </div>
                </div>
                
                <div class="course-info">
                  <h4 class="course-title">{{ course.coachName }}教练</h4>
                  <p class="course-court">球台：{{ course.courtNumber }}</p>
                  
                  <div class="course-details">
                    <div class="detail-item">
                      <span class="label">日期：</span>
                      <span class="value">{{ course.date }}</span>
                    </div>
                    
                    <div class="detail-item">
                      <span class="label">时间：</span>
                      <span class="value">{{ course.startTime }}-{{ course.endTime }}</span>
                    </div>
                    
                    <div class="detail-item">
                      <span class="label">时长：</span>
                      <span class="value">{{ course.duration }}分钟</span>
                    </div>
                  </div>
                  
                  <div class="course-actions">
                    <el-button 
                      :type="selectedCourse?.id === course.id ? 'success' : 'primary'"
                      size="small" 
                      @click="selectCourseItem(course)"
                      :disabled="!course.available"
                    >
                      {{ selectedCourse?.id === course.id ? '已选中' : '选择课程' }}
                    </el-button>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <!-- 无数据状态 -->
          <el-empty 
            v-else-if="!courseLoading" 
            description="该校区暂无可用课程"
            :image-size="120"
          />
        </div>
        
        <!-- 课程分页 -->
        <div class="course-pagination" v-if="courseTotal > 0">
          <el-pagination
            v-model:current-page="courseCurrentPage"
            v-model:page-size="coursePageSize"
            :page-sizes="[8, 16, 24]"
            :total="courseTotal"
            layout="total, sizes, prev, pager, next"
            @size-change="handleCourseSizeChange"
            @current-change="handleCourseCurrentChange"
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="courseDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmCourseBooking"
            :disabled="!selectedCourse"
          >
            确认预约
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCampusList } from '@/api/campus'
import { getCoachDetailList } from '@/api/coachDetail'
import { getCourseList } from '@/api/course'
import { saveStudentCoach } from '@/api/studentCoach'
import { saveBookingRecord } from '@/api/bookingRecord'
import { getUserByToken } from '@/api/user'
import { 
  School, Search, Refresh, Location, User, Phone, Message, 
  View, Calendar, UserFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const loading = ref(false)
const campusList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const detailDialogVisible = ref(false)
const selectedCampus = ref(null)

// 教练相关数据
const coachDialogVisible = ref(false)
const coachLoading = ref(false)
const coachList = ref([])
const coachTotal = ref(0)
const coachCurrentPage = ref(1)
const coachPageSize = ref(8)
const selectedCoach = ref(null)

// 课程相关数据
const courseDialogVisible = ref(false)
const courseLoading = ref(false)
const allCourseList = ref([]) // 全部课程数据
const filteredCourseList = ref([]) // 筛选后的课程数据
const paginatedCourseList = ref([]) // 分页后的课程数据
const courseTotal = ref(0)
const courseCurrentPage = ref(1)
const coursePageSize = ref(8)
const selectedCourse = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  campusType: ''
})

// 教练搜索表单
const coachSearchForm = reactive({
  level: ''
})

// 课程搜索表单
const courseSearchForm = reactive({
  coachUsername: '', // 支持搜索教练用户名和姓名
  available: null // null表示显示全部
})

// 默认图片
const defaultImage = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
const defaultCoachImage = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 当前用户信息
const currentUser = ref(null)

// 获取校区列表
const loadCampusList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      campusType: searchForm.campusType || undefined
    }
    
    const response = await getCampusList(params)
    if (response.code === 200) {
      campusList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取校区列表失败')
    }
  } catch (error) {
    console.error('获取校区列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadCampusList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.campusType = ''
  currentPage.value = 1
  loadCampusList()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadCampusList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadCampusList()
}

// 获取校区类型标签样式
const getTypeTagType = (type) => {
  const typeMap = {
    'B': 'warning', 
    'C': 'success'
  }
  return typeMap[type] || 'info'
}

// 获取校区类型文本
const getCampusTypeText = (type) => {
  const typeMap = {
    'B': '分校区',
    'C': '中心校区'
  }
  return typeMap[type] || '未知类型'
}

// 图片错误处理
const handleImageError = (event) => {
  event.target.src = defaultImage
}

// 查看校区详情
const viewCampusDetail = (campus) => {
  selectedCampus.value = campus
  detailDialogVisible.value = true
}

// 选择教练
const selectCoach = async (campus) => {
  selectedCampus.value = campus
  selectedCoach.value = null
  coachDialogVisible.value = true
  await loadCoachList()
}

// 加载教练列表
const loadCoachList = async () => {
  if (!selectedCampus.value) return
  
  coachLoading.value = true
  try {
    const params = {
      pageNum: coachCurrentPage.value,
      pageSize: coachPageSize.value,
      campusId: selectedCampus.value.id,
      level: coachSearchForm.level || undefined
    }
    
    const response = await getCoachDetailList(params)
    if (response.code === 200) {
      coachList.value = response.data.records || []
      coachTotal.value = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取教练列表失败')
    }
  } catch (error) {
    console.error('获取教练列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    coachLoading.value = false
  }
}

// 搜索教练
const searchCoaches = () => {
  coachCurrentPage.value = 1
  loadCoachList()
}

// 重置教练搜索
const resetCoachSearch = () => {
  coachSearchForm.level = ''
  coachCurrentPage.value = 1
  loadCoachList()
}

// 教练分页处理
const handleCoachSizeChange = (val) => {
  coachPageSize.value = val
  loadCoachList()
}

const handleCoachCurrentChange = (val) => {
  coachCurrentPage.value = val
  loadCoachList()
}

// 选择教练项
const selectCoachItem = (coach) => {
  if (coach.status !== 'approved') {
    ElMessage.warning('该教练尚未通过审核，无法选择')
    return
  }
  selectedCoach.value = coach
}

// 确认教练选择
const confirmCoachSelection = async () => {
  if (!selectedCoach.value) {
    ElMessage.warning('请先选择一位教练')
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
      coachId: selectedCoach.value.id,
      coachName: selectedCoach.value.realName,
      isApproved: false // 默认未审核
    }
    
    const response = await saveStudentCoach(studentCoachData)
    if (response.code === 200) {
      ElMessage.success(`成功选择教练：${selectedCoach.value.realName}，请等待审核`)
      coachDialogVisible.value = false
    } else {
      ElMessage.error(response.msg || '选择教练失败')
    }
  } catch (error) {
    console.error('选择教练错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
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
const handleCoachImageError = (event) => {
  event.target.src = defaultCoachImage
}

// 预约课程
const bookCourse = async (campus) => {
  selectedCampus.value = campus
  selectedCourse.value = null
  courseDialogVisible.value = true
  await loadAllCourses()
}

// 加载所有课程数据（仅在打开弹窗时调用一次）
const loadAllCourses = async () => {
  if (!selectedCampus.value) return
  
  courseLoading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 1000, // 获取所有数据
      campusId: selectedCampus.value.id
    }
    
    const response = await getCourseList(params)
    if (response.code === 200) {
      allCourseList.value = response.data.records || []
      // 初始化筛选
      filterAndPaginateCourses()
    } else {
      ElMessage.error(response.msg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    courseLoading.value = false
  }
}

// 前端筛选和分页
const filterAndPaginateCourses = () => {
  // 筛选数据
  let filtered = allCourseList.value.filter(course => {
    // 教练用户名筛选
    if (courseSearchForm.coachUsername && 
        !course.coachUsername.toLowerCase().includes(courseSearchForm.coachUsername.toLowerCase()) &&
        !course.coachName.toLowerCase().includes(courseSearchForm.coachUsername.toLowerCase())) {
      return false
    }
    
    // 可预约状态筛选
    if (courseSearchForm.available !== null && courseSearchForm.available !== undefined) {
      if (course.available !== courseSearchForm.available) {
        return false
      }
    }
    
    return true
  })
  
  filteredCourseList.value = filtered
  courseTotal.value = filtered.length
  
  // 分页处理
  const startIndex = (courseCurrentPage.value - 1) * coursePageSize.value
  const endIndex = startIndex + coursePageSize.value
  paginatedCourseList.value = filtered.slice(startIndex, endIndex)
}

// 搜索课程
const searchCourses = () => {
  courseCurrentPage.value = 1
  filterAndPaginateCourses()
}

// 重置课程搜索
const resetCourseSearch = () => {
  courseSearchForm.coachUsername = ''
  courseSearchForm.available = null
  courseCurrentPage.value = 1
  filterAndPaginateCourses()
}

// 课程分页处理
const handleCourseSizeChange = (val) => {
  coursePageSize.value = val
  courseCurrentPage.value = 1
  filterAndPaginateCourses()
}

const handleCourseCurrentChange = (val) => {
  courseCurrentPage.value = val
  filterAndPaginateCourses()
}

// 选择课程项
const selectCourseItem = (course) => {
  if (!course.available) {
    ElMessage.warning('该课程不可预约')
    return
  }
  selectedCourse.value = course
}

// 确认课程预约
const confirmCourseBooking = async () => {
  if (!selectedCourse.value) {
    ElMessage.warning('请先选择一门课程')
    return
  }
  
  if (!currentUser.value) {
    ElMessage.error('获取用户信息失败')
    return
  }
  
  try {
    // 生成预约编号
    const bookingNo = `BK${Date.now()}${Math.floor(Math.random() * 1000)}`
    
    const bookingData = {
      bookingNo: bookingNo,
      studentUsername: currentUser.value.username,
      studentName: currentUser.value.realName || currentUser.value.username,
      coachUsername: selectedCourse.value.coachUsername,
      coachName: selectedCourse.value.coachName,
      campusId: selectedCampus.value.id.toString(),
      courseId: selectedCourse.value.id,
      date: selectedCourse.value.date,
      startTime: selectedCourse.value.startTime,
      endTime: selectedCourse.value.endTime,
      duration: selectedCourse.value.duration,
      courtNumber: selectedCourse.value.courtNumber,
      status: 'pending', // 待确认
      amount: 0, // 默认为0，后续可根据教练收费设置
      paymentStatus: 'unpaid' // 未支付
    }
    
    const response = await saveBookingRecord(bookingData)
    if (response.code === 200) {
      ElMessage.success(`课程预约成功！预约编号：${bookingNo}`)
      courseDialogVisible.value = false
      // 重新加载课程列表以更新状态
      await loadAllCourses()
    } else {
      ElMessage.error(response.msg || '课程预约失败')
    }
  } catch (error) {
    console.error('课程预约错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 格式化日期时间
const formatDateTime = (date, time) => {
  if (!date || !time) return '-'
  return `${date} ${time}`
}

// 获取课程状态文本
const getCourseStatusText = (available) => {
  return available ? '可预约' : '不可预约'
}

// 获取课程状态标签类型
const getCourseStatusType = (available) => {
  return available ? 'success' : 'danger'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
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

// 页面加载时获取数据
onMounted(() => {
  loadCampusList()
  getCurrentUser()
})
</script>

<style scoped>
.campus-container {
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

.campus-list {
  margin-bottom: 24px;
}

.campus-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  margin-bottom: 24px;
  height: 100%;
}

.campus-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.campus-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.campus-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.campus-card:hover .campus-image img {
  transform: scale(1.05);
}

.campus-type-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.campus-info {
  padding: 20px;
}

.campus-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  text-align: center;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.info-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.info-text {
  flex: 1;
  word-break: break-all;
}

.campus-actions {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.campus-actions .el-button {
  flex: 1;
  min-width: 80px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.campus-detail .detail-image {
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

/* 教练选择样式 */
.coach-selection {
  max-height: 600px;
  overflow-y: auto;
}

.coach-search {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.coach-list {
  margin-bottom: 20px;
}

.coach-card {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
}

.coach-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.coach-card.selected {
  border-color: #67c23a;
  background: #f0f9ff;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.25);
}

.coach-avatar {
  position: relative;
  text-align: center;
  margin-bottom: 12px;
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
  top: -5px;
  right: calc(50% - 50px);
}

.coach-info {
  text-align: center;
}

.coach-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.coach-username {
  font-size: 12px;
  color: #8492a6;
  margin: 0 0 12px 0;
}

.coach-details {
  text-align: left;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 13px;
}

.detail-item .label {
  color: #606266;
  font-weight: 500;
}

.detail-item .value {
  color: #409eff;
  font-weight: 600;
}

.coach-actions {
  margin-top: 12px;
}

.coach-actions .el-button {
  width: 100%;
}

.coach-pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 课程选择样式 */
.course-selection {
  max-height: 600px;
  overflow-y: auto;
}

.course-search {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.course-list {
  margin-bottom: 20px;
}

.course-card {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
}

.course-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.course-card.selected {
  border-color: #67c23a;
  background: #f0f9ff;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.25);
}

.course-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.course-image {
  position: relative;
  height: 120px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}

.course-info {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.course-court {
  font-size: 12px;
  color: #8492a6;
  margin: 0 0 12px 0;
}

.course-details {
  margin-bottom: 12px;
}

.course-actions .el-button {
  width: 100%;
}

.course-pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .campus-container {
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
  
  .campus-card {
    margin-bottom: 16px;
  }
  
  .campus-info {
    padding: 16px;
  }
  
  .campus-actions {
    flex-direction: column;
  }
  
  .campus-actions .el-button {
    width: 100%;
    margin-bottom: 8px;
  }
  
  .coach-search {
    padding: 16px;
  }
  
  .coach-card {
    margin-bottom: 12px;
  }
  
  .coach-avatar img {
    width: 60px;
    height: 60px;
  }
}
</style>