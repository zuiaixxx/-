<template>
  <div class="coach-detail-manage">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="教练用户名">
          <el-input 
            v-model="queryParams.username" 
            placeholder="请输入教练用户名" 
            clearable 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="教练等级">
          <el-select v-model="queryParams.level" placeholder="请选择教练等级" clearable style="width: 150px;">
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属校区">
          <el-select v-model="queryParams.campusId" placeholder="请选择校区" clearable style="width: 200px;">
            <el-option
              v-for="campus in campusList"
              :key="campus.id"
              :label="campus.campusName"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleQuery" :icon="Search">查询</el-button>
          <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
          <el-button type="primary" @click="handleAdd" :icon="Plus">新增教练信息</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 数据表格 -->
    <el-table :data="tableData" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="photoUrl" label="照片" width="80">
        <template #default="{row}">
          <el-avatar 
            v-if="row.photoUrl" 
            :src="row.photoUrl" 
            size="small"
            shape="square"
          />
          <span v-else class="no-photo">暂无照片</span>
        </template>
      </el-table-column>
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="level" label="教练等级" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getLevelTagType(row.level)" size="small">
            {{ row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hourlyRate" label="每小时收费" width="120" align="center">
        <template #default="{row}">
          <span class="price">¥{{ row.hourlyRate }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="campusId" label="所属校区" width="150">
        <template #default="{row}">
          <div v-if="row.campusId" class="campus-info">
            <span class="campus-display">{{ getCampusDisplayName(row.campusId) }}</span>
          </div>
          <span v-else class="no-campus">未分配</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="审核状态" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="getStatusTagType(row.status)" size="small">
            {{ getStatusDisplayName(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="achievements" label="比赛成绩" width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{row}">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template #default="{row}">
          <div class="action-buttons">
            <el-button type="primary" link @click="handleEdit(row)" :icon="Edit" size="small">编辑</el-button>
            <el-button type="success" link @click="handleApprove(row)" :icon="Check" size="small" 
              v-if="row.status === 'pending'">审核通过</el-button>
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
      :title="isAdd ? '新增教练信息' : '编辑教练信息'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择教练" prop="username">
              <el-select 
                v-model="form.username" 
                placeholder="请选择教练用户"
                :disabled="!isAdd"
                filterable
                style="width: 100%;"
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
                    <span class="coach-phone" v-if="coach.phone"> - {{ coach.phone }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练等级" prop="level">
              <el-radio-group v-model="form.level" class="level-group">
                <el-radio value="初级" class="level-radio">初级</el-radio>
                <el-radio value="中级" class="level-radio">中级</el-radio>
                <el-radio value="高级" class="level-radio">高级</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每小时收费" prop="hourlyRate">
              <el-input-number 
                v-model="form.hourlyRate" 
                :min="0" 
                :precision="2"
                :step="10"
                style="width: 100%;"
                placeholder="请输入收费标准"
              />
            </el-form-item>
          </el-col>
        </el-row>

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
              :value="campus.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="审核状态" prop="status">
          <el-radio-group v-model="form.status" class="status-group">
            <el-radio value="pending" class="status-radio">待审核</el-radio>
            <el-radio value="approved" class="status-radio">已通过</el-radio>
            <el-radio value="rejected" class="status-radio">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="教练照片" prop="photoUrl">
          <div class="photo-upload-wrapper">
            <el-upload
              class="photo-uploader"
              action=""
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handlePhotoChange"
              accept="image/*"
            >
              <div v-if="form.photoUrl" class="photo-preview">
                <img :src="form.photoUrl" class="coach-photo" />
                <div class="photo-edit-overlay">
                  <el-icon><Camera /></el-icon>
                </div>
              </div>
              <div v-else class="photo-empty">
                <el-icon class="photo-icon"><Plus /></el-icon>
                <div class="photo-text">点击上传教练照片</div>
              </div>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="比赛成绩" prop="achievements">
          <el-input 
            v-model="form.achievements" 
            type="textarea" 
            :rows="4"
            placeholder="请输入比赛成绩和主要成就" 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
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
import { getCoachDetailList, saveCoachDetail, updateCoachDetail, deleteCoachDetail, getCoachDetailById } from '@/api/coachDetail'
import { getAllCoach } from '@/api/user'
import { getCampusList } from '@/api/campus'
import { uploadFile } from '@/api/file'
import { Search, Refresh, Plus, Edit, Delete, Camera, Check } from '@element-plus/icons-vue'

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
const photoFile = ref(null)
const coachList = ref([]) // 教练用户列表
const campusList = ref([]) // 校区列表

// 查询参数
const queryParams = reactive({
  username: '',
  level: '',
  campusId: ''
})

// 表单数据
const form = ref({
  id: null,
  username: '',
  realName: '',
  level: '初级',
  hourlyRate: 0,
  photoUrl: '',
  achievements: '',
  status: 'pending',
  campusId: null // 使用null作为初始值，对应数据库中的id字段
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请选择教练用户', trigger: 'change' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择教练等级', trigger: 'change' }
  ],
  hourlyRate: [
    { required: true, message: '请输入每小时收费', trigger: 'blur' },
    { type: 'number', min: 0, message: '收费不能小于0', trigger: 'blur' }
  ],
  campusId: [
    { required: true, message: '请选择所属校区', trigger: 'change' }
  ],
  photoUrl: [
    { required: true, message: '请上传教练照片', trigger: 'blur' }
  ]
}

// 获取等级标签类型
const getLevelTagType = (level) => {
  const typeMap = {
    '初级': 'info',
    '中级': '',
    '高级': 'danger'
  }
  return typeMap[level] || 'default'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态显示名称
const getStatusDisplayName = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return statusMap[status] || status
}

// 获取校区显示名称
const getCampusDisplayName = (campusId) => {
  const campus = campusList.value.find(item => item.id === campusId)
  return campus ? campus.campusName : `校区ID: ${campusId}`
}

// 获取教练信息列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCoachDetailList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取教练信息列表失败')
    }
  } catch (error) {
    console.error('获取教练信息列表失败:', error)
    ElMessage.error('获取教练信息列表失败')
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
  queryParams.username = ''
  queryParams.level = ''
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
    username: '',
    realName: '',
    level: '初级',
    hourlyRate: 0,
    photoUrl: '',
    achievements: '',
    status: 'pending',
    campusId: null // 重置为null
  }
  photoFile.value = null
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
    const res = await getCoachDetailById(row.id)
    if (res.code === 200) {
      form.value = { ...res.data }
      isAdd.value = false
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取教练详情失败')
    }
  } catch (error) {
    console.error('获取教练详情失败:', error)
    ElMessage.error('获取教练详情失败')
  }
}

// 审核通过
const handleApprove = async (row) => {
  try {
    const updateData = { ...row, status: 'approved' }
    const res = await updateCoachDetail(updateData)
    if (res.code === 200) {
      ElMessage.success('审核通过成功')
      getList()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除教练"${row.realName}"的信息吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      buttonSize: 'default'
    }
  ).then(async () => {
    try {
      const res = await deleteCoachDetail(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除教练信息失败:', error)
      ElMessage.error('删除教练信息失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 处理照片上传
const handlePhotoChange = (file) => {
  photoFile.value = file.raw
  // 预览
  const reader = new FileReader()
  reader.onload = (e) => {
    form.value.photoUrl = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 先上传照片
        if (photoFile.value) {
          const uploadRes = await uploadFile(photoFile.value)
          if (uploadRes.code === 200) {
            form.value.photoUrl = uploadRes.data
          } else {
            ElMessage.error('照片上传失败')
            return
          }
        }
        
        // 保存教练信息
        const apiMethod = isAdd.value ? saveCoachDetail : updateCoachDetail
        const res = await apiMethod(form.value)
        
        if (res.code === 200) {
          ElMessage.success(isAdd.value ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存教练信息失败:', error)
        ElMessage.error('保存教练信息失败')
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
.coach-detail-manage {
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
  
  .no-photo {
    color: #999;
    font-size: 12px;
  }
  
  .price {
    color: #e6a23c;
    font-weight: 500;
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

// 对话框样式
.level-group, .status-group {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  
  .level-radio, .status-radio {
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
  
  .coach-phone {
    color: #999;
    font-size: 12px;
    margin-left: 8px;
  }
}

.photo-upload-wrapper {
  width: 150px;
  height: 200px;
}

.photo-uploader {
  width: 100%;
  height: 100%;
}

.photo-empty {
  width: 150px;
  height: 200px;
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

.photo-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.photo-text {
  font-size: 12px;
  color: #8c939d;
}

.photo-preview {
  width: 150px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid #e0e0e0;
  cursor: pointer;
}

.coach-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-edit-overlay {
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

.photo-preview:hover .photo-edit-overlay {
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