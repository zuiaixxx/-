<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/user'
import { useStore } from '@/store'
import { Trophy } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useStore()

// 当前激活的面板
const activeTab = ref('login')

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

// 表单验证规则
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度最少为6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref(null)
const registerFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 登录请求
const handleLogin = async () => {
  if (loginFormRef.value) {
    await loginFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        try {
          // 添加role字段为user
          const loginData = {
            ...loginForm,
            role: 'user'
          }
          const res = await login(loginData)
          if (res.code === 200) {
            // 保存token
            userStore.setToken(res.data)
            // 获取用户信息
            await userStore.getUserInfo()
            // 跳转到首页
            router.push('/')
            ElMessage.success('登录成功！欢迎使用乒乓球培训管理系统')
          } else {
            ElMessage.error(res.msg || '登录失败，请检查用户名和密码')
          }
        } catch (error) {
          console.error('登录错误', error)
          ElMessage.error('登录失败，请检查网络连接')
        } finally {
          loading.value = false
        }
      }
    })
  }
}

// 注册请求
const handleRegister = async () => {
  if (registerFormRef.value) {
    await registerFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        try {
          const res = await register({
            username: registerForm.username,
            password: registerForm.password,
            email: registerForm.email,
            phone: registerForm.phone,
            role: 'user'
          })
          
          if (res.code === 200) {
            ElMessage.success('注册成功，请登录')
            // 切换到登录面板
            activeTab.value = 'login'
            // 预填写用户名
            loginForm.username = registerForm.username
          } else {
            ElMessage.error(res.message || '注册失败')
          }
        } catch (error) {
          console.error('注册错误', error)
          ElMessage.error('注册失败，请检查网络连接')
        } finally {
          loading.value = false
        }
      }
    })
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="header">
        <div class="logo-container">
          <el-icon class="logo-icon" size="48" color="#409EFF">
            <Trophy />
          </el-icon>
        </div>
        <h1>乒乓球培训管理系统</h1>
        <p class="subtitle">用户登录</p>
      </div>
      
      <el-tabs v-model="activeTab" class="form-tabs">
        <!-- 登录面板 -->
        <el-tab-pane label="用户登录" name="login">
          <el-form 
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            label-position="top"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleLogin"
                style="width: 100%"
              >
                登录
              </el-button>
            </el-form-item>
            
            <div class="bottom-links">
              <el-button text @click="activeTab = 'register'">
                没有账号？立即注册
              </el-button>
            </div>
          </el-form>
        </el-tab-pane>
        
        <!-- 注册面板 -->
        <el-tab-pane label="用户注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                prefix-icon="Message"
              />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleRegister"
                style="width: 100%"
              >
                注册
              </el-button>
            </el-form-item>
            
            <div class="bottom-links">
              <el-button text @click="activeTab = 'login'">
                已有账号？立即登录
              </el-button>
            </div>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="30" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="2"/><circle cx="20" cy="30" r="15" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="1"/><circle cx="80" cy="70" r="20" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="1"/></svg>') repeat;
  animation: float 20s infinite linear;
  z-index: 1;
}

@keyframes float {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
  border: 1px solid rgba(255, 255, 255, 0.18);
  z-index: 2;
  position: relative;
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.45);
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  text-align: center;
}

.logo-container {
  margin-bottom: 15px;
  padding: 15px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
}

.logo-icon {
  color: white !important;
}

h1 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 14px;
  color: #8492a6;
  margin: 0;
  font-weight: 400;
}

.form-tabs {
  width: 100%;
}

.form-tabs :deep(.el-tabs__header) {
  margin-bottom: 25px;
}

.form-tabs :deep(.el-tabs__nav-wrap) {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 20px;
}

.form-tabs :deep(.el-tabs__item) {
  border-radius: 8px;
  margin: 0 4px;
  padding: 0 20px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.form-tabs :deep(.el-tabs__item.is-active) {
  background: #409EFF;
  color: white;
}

.form-tabs :deep(.el-tabs__active-bar) {
  display: none;
}

.form-tabs :deep(.el-form-item__label) {
  font-weight: 500;
  color: #2c3e50;
}

.form-tabs :deep(.el-input) {
  border-radius: 10px;
}

.form-tabs :deep(.el-input__wrapper) {
  border-radius: 10px;
  transition: all 0.3s ease;
}

.form-tabs :deep(.el-input__wrapper:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form-tabs :deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF, #67C23A);
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.form-tabs :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.bottom-links {
  margin-top: 15px;
  text-align: center;
}

.bottom-links :deep(.el-button--text) {
  color: #409EFF;
  font-weight: 500;
  transition: all 0.3s ease;
}

.bottom-links :deep(.el-button--text:hover) {
  color: #67C23A;
  transform: scale(1.05);
}
</style> 