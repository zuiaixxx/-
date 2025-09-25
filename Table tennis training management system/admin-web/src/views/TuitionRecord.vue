<template>
  <div class="tuition-record-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="交易编号">
          <el-input 
            v-model="searchForm.recordNo" 
            placeholder="请输入交易编号" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select 
            v-model="searchForm.paymentMethod" 
            placeholder="请选择支付方式" 
            clearable 
            style="width: 150px"
          >
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="线下支付" value="offline" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd">新增缴费记录</el-button>
    </div>

    <!-- 表格 -->
    <el-table 
      :data="tableData" 
      style="width: 100%" 
      v-loading="loading"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="recordNo" label="交易编号" width="180" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="用户姓名" width="120" />
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="scope">
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="支付方式" width="120">
        <template #default="scope">
          <el-tag 
            :type="getPaymentMethodType(scope.row.paymentMethod)" 
            size="small"
          >
            {{ getPaymentMethodText(scope.row.paymentMethod) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :small="false"
        :disabled="false"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      class="tuition-record-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="padding: 0 20px"
      >
        <div class="form-section">
          <h4 class="section-title">基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="交易编号" prop="recordNo">
                <el-input v-model="form.recordNo" placeholder="请输入交易编号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择用户" prop="userId">
                <el-select 
                  v-model="form.userId" 
                  placeholder="请选择用户" 
                  filterable
                  @change="handleUserChange"
                  style="width: 100%"
                >
                  <el-option 
                    v-for="user in userList" 
                    :key="user.id"
                    :label="`${user.name}(${user.username})`" 
                    :value="user.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="form.username" placeholder="选择用户后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="用户姓名">
                <el-input v-model="form.name" placeholder="选择用户后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4 class="section-title">支付信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="金额" prop="amount">
                <el-input-number 
                  v-model="form.amount" 
                  :precision="2" 
                  :min="0" 
                  :max="99999.99"
                  style="width: 100%"
                  placeholder="请输入金额"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="支付方式" prop="paymentMethod">
                <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
                  <el-option label="微信支付" value="wechat" />
                  <el-option label="支付宝" value="alipay" />
                  <el-option label="线下支付" value="offline" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input 
                  v-model="form.remark" 
                  type="textarea" 
                  :rows="3"
                  placeholder="请输入备注信息"
                  maxlength="255"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { 
  getTuitionRecordList, 
  getTuitionRecordById, 
  saveTuitionRecord, 
  deleteTuitionRecord 
} from '@/api/tuitionRecord'
import { getAllUser } from '@/api/user'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增缴费记录')
const formRef = ref()
const userList = ref([])

// 搜索表单
const searchForm = reactive({
  recordNo: '',
  paymentMethod: ''
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  recordNo: '',
  username: '',
  name: '',
  userId: null,
  amount: null,
  paymentMethod: '',
  remark: ''
})

// 表单验证规则
const rules = {
  recordNo: [
    { required: true, message: '请输入交易编号', trigger: 'blur' },
    { max: 32, message: '交易编号长度不能超过32个字符', trigger: 'blur' }
  ],
  userId: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于等于0', trigger: 'blur' }
  ]
}

// 获取所有用户列表
const getUserListData = async () => {
  try {
    const response = await getAllUser()
    if (response.code === 200) {
      userList.value = response.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 用户选择改变时的处理
const handleUserChange = (userId) => {
  const selectedUser = userList.value.find(user => user.id === userId)
  if (selectedUser) {
    form.username = selectedUser.username
    form.name = selectedUser.name
  }
}

// 获取缴费记录列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getTuitionRecordList(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取缴费记录列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getList()
}

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.pageNum = 1
  getList()
}

// 新增
const handleAdd = async () => {
  dialogTitle.value = '新增缴费记录'
  resetForm()
  await getUserListData()
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑缴费记录'
  await getUserListData()
  try {
    const response = await getTuitionRecordById(row.id)
    if (response.code === 200) {
      Object.keys(form).forEach(key => {
        form[key] = response.data[key]
      })
      // 根据 username 找到对应的 userId
      const user = userList.value.find(u => u.username === response.data.username)
      if (user) {
        form.userId = user.id
      }
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取缴费记录详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除交易编号为 "${row.recordNo}" 的缴费记录吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteTuitionRecord(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    const response = await saveTuitionRecord({ ...form })
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      getList()
    }
  } catch (error) {
    ElMessage.error(form.id ? '更新失败' : '新增失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    if (key === 'id' || key === 'userId') {
      form[key] = null
    } else {
      form[key] = ''
    }
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getList()
}

// 获取支付方式标签类型
const getPaymentMethodType = (method) => {
  const typeMap = {
    'wechat': 'success',
    'alipay': 'primary', 
    'offline': 'warning'
  }
  return typeMap[method] || ''
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const textMap = {
    'wechat': '微信支付',
    'alipay': '支付宝',
    'offline': '线下支付'
  }
  return textMap[method] || method
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.tuition-record-container {
  padding: 20px;
}

.search-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.operation-buttons {
  margin-bottom: 20px;
  text-align: left;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.tuition-record-dialog .form-section {
  margin-bottom: 24px;
}

.tuition-record-dialog .section-title {
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.dialog-footer {
  text-align: right;
}
</style>