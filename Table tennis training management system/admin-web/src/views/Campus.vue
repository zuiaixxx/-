<template>
  <div class="campus-manage">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="关键词">
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="请输入校区名称、地址或联系人" 
            clearable 
            style="width: 250px;"
          />
        </el-form-item>
        <el-form-item label="校区类型">
          <el-select v-model="queryParams.campusType" placeholder="请选择校区类型" clearable style="width: 150px;">
            <el-option label="中心校区" value="C" />
            <el-option label="分校区" value="B" />
          </el-select>
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleQuery" :icon="Search">查询</el-button>
          <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
          <el-button type="primary" @click="handleAdd" :icon="Plus">新增校区</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 数据表格 -->
    <el-table :data="tableData" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="campusNo" label="校区编号" width="120" />
      <el-table-column prop="campusName" label="校区名称" width="150" />
      <el-table-column prop="campusType" label="类型" width="100" align="center">
        <template #default="{row}">
          <el-tag :type="row.campusType === 'C' ? 'danger' : 'success'" size="small">
            {{ row.campusType === 'C' ? '中心校区' : '分校区' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip />
      <el-table-column prop="contactPerson" label="联系人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="150" show-overflow-tooltip />
      <el-table-column prop="adminUsername" label="校区管理员" width="150">
        <template #default="{row}">
          <div v-if="row.adminUsername" class="admin-info">
            <span class="admin-display">{{ getAdminDisplayName(row.adminUsername) }}</span>
          </div>
          <span v-else class="no-admin">未分配</span>
        </template>
      </el-table-column>
      <el-table-column prop="imageUrl" label="校区图片" width="100">
        <template #default="{row}">
          <el-avatar 
            v-if="row.imageUrl" 
            :src="row.imageUrl" 
            shape="square" 
            size="small"
          />
          <span v-else class="no-image">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="160">
        <template #default="{row}">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180" align="center">
        <template #default="{row}">
          <div class="action-buttons">
            <el-button type="primary" link @click="handleEdit(row)" :icon="Edit" size="small">编辑</el-button>
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
      :title="isAdd ? '新增校区' : '编辑校区'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="校区编号" prop="campusNo">
              <el-input 
                v-model="form.campusNo" 
                placeholder="如：ZX001, FX002" 
                maxlength="10"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="校区名称" prop="campusName">
              <el-input v-model="form.campusName" placeholder="请输入校区名称" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="校区类型" prop="campusType">
              <el-radio-group v-model="form.campusType" class="campus-type-group">
                <el-radio value="C" class="type-radio">中心校区</el-radio>
                <el-radio value="B" class="type-radio">分校区</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input 
            v-model="form.address" 
            type="textarea" 
            :rows="2"
            placeholder="请输入详细地址" 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="15" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入联系邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="校区管理员" prop="adminUsername">
          <el-select 
            v-model="form.adminUsername" 
            placeholder="请选择校区管理员"
            clearable
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="admin in campusAdminList"
              :key="admin.username"
              :label="`${admin.name}(${admin.username})`"
              :value="admin.username"
            >
              <div class="admin-option">
                <span class="admin-name">{{ admin.name }}</span>
                <span class="admin-username">({{ admin.username }})</span>
                <span class="admin-phone" v-if="admin.phone"> - {{ admin.phone }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="校区图片" prop="imageUrl">
          <div class="image-upload-wrapper">
            <el-upload
              class="image-uploader"
              action=""
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleImageChange"
              accept="image/*"
            >
              <div v-if="form.imageUrl" class="image-preview">
                <img :src="form.imageUrl" class="campus-image" />
                <div class="image-edit-overlay">
                  <el-icon><Camera /></el-icon>
                </div>
              </div>
              <div v-else class="image-empty">
                <el-icon class="image-icon"><Plus /></el-icon>
                <div class="image-text">点击上传校区图片</div>
              </div>
            </el-upload>
          </div>
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
import { getCampusList, saveCampus, updateCampus, deleteCampus, getCampusById } from '@/api/campus'
import { uploadFile } from '@/api/file'
import { getAllCampusAdmin } from '@/api/user'
import { Search, Refresh, Plus, Edit, Delete, Camera } from '@element-plus/icons-vue'

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
const campusAdminList = ref([]) // 校区管理员列表

// 查询参数
const queryParams = reactive({
  keyword: '',
  campusType: ''
})

// 表单数据
const form = ref({
  id: null,
  campusNo: '',
  campusName: '',
  campusType: 'C',
  address: '',
  contactPerson: '',
  phone: '',
  email: '',
  adminUsername: '',
  imageUrl: ''
})

// 表单验证规则
const rules = {
  campusNo: [
    { required: true, message: '请输入校区编号', trigger: 'blur' },
    { pattern: /^[A-Z]{2}\d{3}$/, message: '校区编号格式：如ZX001', trigger: 'blur' }
  ],
  campusName: [
    { required: true, message: '请输入校区名称', trigger: 'blur' }
  ],
  campusType: [
    { required: true, message: '请选择校区类型', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 获取校区列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCampusList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryParams
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取校区列表失败')
    }
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  pageNum.value = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.campusType = ''
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
    campusNo: '',
    campusName: '',
    campusType: 'C',
    address: '',
    contactPerson: '',
    phone: '',
    email: '',
    adminUsername: '',
    imageUrl: ''
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
    const res = await getCampusById(row.id)
    if (res.code === 200) {
      form.value = { ...res.data }
      isAdd.value = false
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取校区详情失败')
    }
  } catch (error) {
    console.error('获取校区详情失败:', error)
    ElMessage.error('获取校区详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除校区"${row.campusName}"吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      buttonSize: 'default'
    }
  ).then(async () => {
    try {
      const res = await deleteCampus(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除校区失败:', error)
      ElMessage.error('删除校区失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 处理图片上传
const handleImageChange = (file) => {
  imageFile.value = file.raw
  // 预览
  const reader = new FileReader()
  reader.onload = (e) => {
    form.value.imageUrl = e.target.result
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
            // 文件上传返回的是URL地址
            form.value.imageUrl = uploadRes.data
          } else {
            ElMessage.error('图片上传失败')
            return
          }
        }
        
        // 保存校区信息
        const apiMethod = isAdd.value ? saveCampus : updateCampus
        const res = await apiMethod(form.value)
        
        if (res.code === 200) {
          ElMessage.success(isAdd.value ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存校区失败:', error)
        ElMessage.error('保存校区失败')
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

// 获取管理员显示名称
const getAdminDisplayName = (username) => {
  const admin = campusAdminList.value.find(item => item.username === username)
  return admin ? `${admin.name}(${username})` : username
}

// 获取所有校区管理员
const getCampusAdminList = async () => {
  try {
    const res = await getAllCampusAdmin()
    if (res.code === 200) {
      campusAdminList.value = res.data || []
    } else {
      console.error('获取校区管理员列表失败:', res.msg)
    }
  } catch (error) {
    console.error('获取校区管理员列表失败:', error)
  }
}

// 组件挂载时获取列表
onMounted(() => {
  getList()
  getCampusAdminList() // 加载校区管理员列表
})
</script>

<style lang="less" scoped>
.campus-manage {
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
  
  .admin-info {
    .admin-display {
      color: #2c3e50;
      font-size: 13px;
    }
  }
  
  .no-admin {
    color: #999;
    font-size: 12px;
    font-style: italic;
  }
}

// 对话框样式
.campus-type-group {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  
  .type-radio {
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

.campus-image {
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

// 管理员选择框样式
.admin-option {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  
  .admin-name {
    font-weight: 500;
    color: #2c3e50;
  }
  
  .admin-username {
    color: #409eff;
    font-size: 12px;
    margin-left: 4px;
  }
  
  .admin-phone {
    color: #999;
    font-size: 12px;
    margin-left: 8px;
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