<template>
  <div class="course-manage">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="教练用户名">
          <el-input 
            v-model="queryParams.coachUsername" 
            placeholder="请输入教练用户名" 
            clearable 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="所属校区">
          <el-select v-model="queryParams.campusId" placeholder="请选择校区" clearable style="width: 200px;">
            <el-option
              v-for="campus in campusList"
              :key="campus.id"
              :label="campus.campusName"
              :value="String(campus.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleQuery" :icon="Search">查询</el-button>
          <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
          <el-button type="primary" @click="handleAdd" :icon="Plus">新增课程排班</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 数据表格 -->
    <el-table :data="tableData" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="img" label="课程图片" width="100">
        <template #default="{row}">
          <el-avatar 
            v-if="row.img" 
            :src="row.img" 
            size="small"
            shape="square"
          />
          <span v-else class="no-image">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="coachName" label="教练姓名" width="120" />
      <el-table-column prop="coachUsername" label="教练用户名" width="130" />
      <el-table-column prop="campusId" label="所属校区" width="150">
        <template #default="{row}">
          <div v-if="row.campusId" class="campus-info">
            <span class="campus-display">{{ getCampusDisplayName(row.campusId) }}</span>
          </div>
          <span v-else class="no-campus">未分配</span>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="课程日期" width="120" />
      <el-table-column prop="startTime" label="开始时间" width="100" />
      <el-table-column prop="endTime" label="结束时间" width="100" />
      <el-table-column prop="duration" label="时长(分钟)" width="100" align="center" />
      <el-table-column prop="courtNumber" label="球台编号" width="100" align="center" />
      <el-table-column prop="available" label="可预约状态" width="120" align="center">
        <template #default="{row}">
          <el-tag :type="row.available === true ? 'success' : 'danger'" size="small">
            {{ row.available === true ? '可预约' : '不可预约' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{row}">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template #default="{row}">
          <div class="action-buttons">
            <el-button type="primary" link @click="handleEdit(row)" :icon="Edit" size="small">编辑</el-button>
            <el-button 
              :type="row.available === true ? 'warning' : 'success'" 
              link 
              @click="handleToggleAvailable(row)" 
              :icon="Switch" 
              size="small"
            >
              {{ row.available === true ? '暂停预约' : '开放预约' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" :icon="Delete" size="small">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页组件 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isAdd ? '新增课程排班' : '编辑课程排班'"
      width="900px"
      :close-on-click-modal="false"
      class="course-dialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="course-form">
        <!-- 教练信息区域 -->
        <div class="form-section">
          <h4 class="section-title">教练信息</h4>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="选择教练" prop="coachUsername">
                <el-select 
                  v-model="form.coachUsername" 
                  placeholder="请选择教练"
                  filterable
                  style="width: 100%;"
                  @change="handleCoachChange"
                >
                  <el-option
                    v-for="coach in coachList"
                    :key="coach.username"
                    :label="`${coach.name}(${coach.username})`"
                    :value="coach.username"
                  >
                    <div class="coach-option">
                      <span class="coach-name">{{ coach.name }}</span>
                      <span class="coach-username">({{ coach.username }})</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="教练姓名" prop="coachName">
                <el-input v-model="form.coachName" placeholder="选择教练后自动填充" :disabled="true" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <!-- 课程基本信息 -->
        <div class="form-section">
          <h4 class="section-title">课程基本信息</h4>
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="所属校区" prop="campusId">
                <el-select 
                  v-model="form.campusId" 
                  placeholder="请选择所属校区"
                  style="width: 100%;"
                >
                  <el-option
                    v-for="campus in campusList"
                    :key="campus.id"
                    :label="campus.campusName"
                    :value="String(campus.id)"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="课程日期" prop="date">
                <el-date-picker
                  v-model="form.date"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="开始时间" prop="startTime">
                <el-time-picker
                  v-model="form.startTime"
                  placeholder="选择开始时间"
                  format="HH:mm:ss"
                  value-format="HH:mm:ss"
                  style="width: 100%;"
                  @change="calculateDuration"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束时间" prop="endTime">
                <el-time-picker
                  v-model="form.endTime"
                  placeholder="选择结束时间"
                  format="HH:mm:ss"
                  value-format="HH:mm:ss"
                  style="width: 100%;"
                  @change="calculateDuration"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 课程详细信息 -->
        <div class="form-section">
          <h4 class="section-title">课程详细信息</h4>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="时长(分钟)" prop="duration">
                <el-input-number 
                  v-model="form.duration" 
                  :min="1" 
                  :max="480"
                  style="width: 100%;"
                  placeholder="系统自动计算"
                  :disabled="true"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="球台编号" prop="courtNumber">
                <el-input v-model="form.courtNumber" placeholder="请输入球台编号" maxlength="10" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="可预约状态" prop="available">
                <el-radio-group v-model="form.available" class="available-group">
                  <el-radio :value="true" class="available-radio">可预约</el-radio>
                  <el-radio :value="false" class="available-radio">不可预约</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 课程图片 -->
        <div class="form-section">
          <h4 class="section-title">课程图片</h4>
          <el-row>
            <el-col :span="24">
              <el-form-item label="课程图片" prop="img">
                <div class="image-upload-wrapper">
                  <el-upload
                    class="image-uploader"
                    action=""
                    :auto-upload="false"
                    :show-file-list="false"
                    :on-change="handleImageChange"
                    accept="image/*"
                  >
                    <div v-if="form.img" class="image-preview">
                      <img :src="form.img" class="course-image" />
                      <div class="image-edit-overlay">
                        <el-icon><Camera /></el-icon>
                      </div>
                    </div>
                    <div v-else class="image-empty">
                      <el-icon class="image-icon"><Plus /></el-icon>
                      <div class="image-text">点击上传课程图片</div>
                    </div>
                  </el-upload>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            {{ isAdd ? '保存' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getCourseList, saveCourse, updateCourse, deleteCourse, getCourseById } from '@/api/course'
import { getAllCoach } from '@/api/user'
import { getCampusList } from '@/api/campus'
import { uploadFile } from '@/api/file'
import { Search, Refresh, Plus, Edit, Delete, Camera, Switch } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const formRef = ref(null)
const isAdd = ref(true)
const imageFile = ref(null)
const coachList = ref([]) // 教练用户列表
const campusList = ref([]) // 校区列表

// 查询参数
const queryParams = reactive({
  coachUsername: '',
  campusId: '' // 使用字符串，匹配后端实际返回的数据类型
})

// 表单数据
const form = ref({
  id: null,
  coachUsername: '',
  coachName: '',
  campusId: '', // 使用字符串，匹配后端实际数据类型
  date: '',
  startTime: '',
  endTime: '',
  duration: 0,
  available: true, // 使用boolean类型
  img: '',
  courtNumber: ''
})

// 表单验证规则
const rules = {
  coachUsername: [
    { required: true, message: '请选择教练', trigger: 'change' }
  ],
  campusId: [
    { required: true, message: '请选择所属校区', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择课程日期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  courtNumber: [
    { required: true, message: '请输入球台编号', trigger: 'blur' }
  ]
}

// 获取校区显示名称
const getCampusDisplayName = (campusId) => {
  const campus = campusList.value.find(item => String(item.id) === String(campusId))
  return campus ? campus.campusName : `校区ID: ${campusId}`
}

// 获取课程排班列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取课程排班列表失败')
    }
  } catch (error) {
    console.error('获取课程排班列表失败:', error)
    ElMessage.error('获取课程排班列表失败')
  } finally {
    loading.value = false
  }
}

// 获取教练用户列表
const getCoachList = async () => {
  try {
    const res = await getAllCoach()
    if (res.code === 200) {
      coachList.value = res.data || []
    } else {
      console.error('获取教练用户列表失败:', res.msg)
    }
  } catch (error) {
    console.error('获取教练用户列表失败:', error)
  }
}

// 获取校区列表
const getCampusListData = async () => {
  try {
    const res = await getCampusList({
      pageNum: 1,
      pageSize: 1000 // 获取所有校区
    })
    if (res.code === 200) {
      campusList.value = res.data.records || []
    } else {
      console.error('获取校区列表失败:', res.msg)
    }
  } catch (error) {
    console.error('获取校区列表失败:', error)
  }
}

// 查询
const handleQuery = () => {
  pageNum.value = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.coachUsername = ''
  queryParams.campusId = ''
  handleQuery()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageNum.value = val
  getList()
}

// 重置表单
const resetForm = () => {
  form.value = {
    id: null,
    coachUsername: '',
    coachName: '',
    campusId: '',
    date: '',
    startTime: '',
    endTime: '',
    duration: 0,
    available: true, // 使用boolean类型
    img: '',
    courtNumber: ''
  }
  imageFile.value = null
}

// 新增
const handleAdd = () => {
  resetForm()
  isAdd.value = true
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  try {
    const res = await getCourseById(row.id)
    if (res.code === 200) {
      form.value = { ...res.data }
      isAdd.value = false
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取课程排班详情失败')
    }
  } catch (error) {
    console.error('获取课程排班详情失败:', error)
    ElMessage.error('获取课程排班详情失败')
  }
}

// 切换可预约状态
const handleToggleAvailable = async (row) => {
  try {
    const newStatus = !row.available // 使用boolean取反
    const updateData = { ...row, available: newStatus }
    const res = await updateCourse(updateData)
    if (res.code === 200) {
      ElMessage.success(`已${newStatus === true ? '开放' : '暂停'}预约`)
      getList()
    } else {
      ElMessage.error(res.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除教练"${row.coachName}"在${row.date} ${row.startTime}-${row.endTime}的课程排班吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      buttonSize: 'default'
    }
  ).then(async () => {
    try {
      const res = await deleteCourse(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除课程排班失败:', error)
      ElMessage.error('删除课程排班失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 教练选择变化
const handleCoachChange = (username) => {
  const coach = coachList.value.find(item => item.username === username)
  if (coach) {
    form.value.coachName = coach.name
  }
}

// 计算时长
const calculateDuration = () => {
  if (form.value.startTime && form.value.endTime) {
    const start = new Date(`2000-01-01 ${form.value.startTime}`)
    const end = new Date(`2000-01-01 ${form.value.endTime}`)
    const diff = end.getTime() - start.getTime()
    if (diff > 0) {
      form.value.duration = Math.round(diff / (1000 * 60)) // 转换为分钟
    } else {
      form.value.duration = 0
      ElMessage.warning('结束时间应该晚于开始时间')
    }
  }
}

// 处理图片上传
const handleImageChange = (file) => {
  imageFile.value = file.raw
  // 预览
  const reader = new FileReader()
  reader.onload = (e) => {
    form.value.img = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 先上传图片
        if (imageFile.value) {
          const uploadRes = await uploadFile(imageFile.value)
          if (uploadRes.code === 200) {
            form.value.img = uploadRes.data
          } else {
            ElMessage.error('图片上传失败')
            return
          }
        }
        
        // 保存课程排班信息
        const apiMethod = isAdd.value ? saveCourse : updateCourse
        const res = await apiMethod(form.value)
        
        if (res.code === 200) {
          ElMessage.success(isAdd.value ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存课程排班失败:', error)
        ElMessage.error('保存课程排班失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 日期格式化
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getList()
  getCoachList()
  getCampusListData()
})
</script>

<style lang="less" scoped>
.course-manage {
  padding: 20px;
  
  .search-form {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    .button-group {
      .el-button {
        margin-right: 8px;
        border-radius: 6px;
        
        &:last-child {
          margin-right: 0;
        }
        
        &.el-button--primary {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;
          
          &:hover {
            background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          }
        }
      }
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .action-buttons {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .el-button {
      border-radius: 4px;
      
      &:hover {
        transform: translateY(-1px);
      }
    }
  }
  
  .no-image {
    color: #999;
    font-size: 12px;
  }
  
  .campus-info {
    .campus-display {
      color: #2c3e50;
      font-size: 13px;
    }
  }
  
  .no-campus {
    color: #999;
    font-size: 12px;
    font-style: italic;
  }
}

// 对话框样式优化
.course-dialog {
  :deep(.el-dialog__body) {
    padding: 20px 30px;
    max-height: 70vh;
    overflow-y: auto;
  }
}

.course-form {
  .form-section {
    margin-bottom: 30px;
    padding: 20px;
    background-color: #fafbfc;
    border-radius: 8px;
    border: 1px solid #e8eaec;
    
    .section-title {
      margin: 0 0 20px 0;
      padding-bottom: 10px;
      border-bottom: 2px solid #409eff;
      color: #2c3e50;
      font-size: 16px;
      font-weight: 600;
      display: flex;
      align-items: center;
      
      &::before {
        content: '';
        width: 4px;
        height: 16px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        margin-right: 8px;
        border-radius: 2px;
      }
    }
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  :deep(.el-form-item) {
    margin-bottom: 18px;
    
    .el-form-item__label {
      color: #2c3e50;
      font-weight: 500;
      text-align: left;
    }
  }
}

// 对话框样式
.available-group {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  
  .available-radio {
    margin-right: 20px;
    
    :deep(.el-radio__label) {
      font-size: 14px;
      color: #2c3e50;
    }
    
    :deep(.el-radio__input.is-checked .el-radio__inner) {
      background-color: #409eff;
      border-color: #409eff;
    }
  }
}

.coach-option {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  
  .coach-name {
    font-weight: 500;
    color: #2c3e50;
  }
  
  .coach-username {
    color: #409eff;
    font-size: 12px;
    margin-left: 4px;
  }
}

.image-upload-wrapper {
  width: 200px;
  height: 120px;
}

.image-uploader {
  width: 100%;
  height: 100%;
}

.image-empty {
  width: 200px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fafafa;
  
  &:hover {
    border-color: #409EFF;
    background-color: #f0f7ff;
  }
}

.image-icon {
  font-size: 24px;
  color: #8c939d;
  margin-bottom: 8px;
}

.image-text {
  font-size: 12px;
  color: #8c939d;
}

.image-preview {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid #e0e0e0;
  cursor: pointer;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-edit-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  font-size: 24px;
  
  &:hover {
    opacity: 1;
  }
}

.image-preview:hover .image-edit-overlay {
  opacity: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  
  .el-button {
    border-radius: 6px;
    padding: 8px 20px;
    
    &.el-button--primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      
      &:hover {
        background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      }
    }
  }
}

// 表格样式优化
:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #f8f9fa;
      color: #2c3e50;
      font-weight: 600;
      text-align: left;
    }
  }
  
  .el-table__row {
    &:hover {
      background-color: #f0f7ff;
    }
  }
}

// 下拉选择框样式优化
:deep(.el-select) {
  .el-select__wrapper {
    border-radius: 6px;
    transition: all 0.3s;
    
    &:hover {
      border-color: #409eff;
    }
    
    &.is-focused {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
}
</style>