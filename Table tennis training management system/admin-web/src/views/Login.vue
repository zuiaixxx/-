<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { login as adminLogin, getUserByToken } from '@/api/user';
import { useStore } from '@/store';

const router = useRouter();
const store = useStore();
const loginForm = reactive({
    username: '',
    password: '',
    role: 'superAdmin'
});

// 角色选项
const roleOptions = [
    { value: 'superAdmin', label: '超级管理员' },
    { value: 'campusAdmin', label: '校区管理员' },
    { value: 'coach', label: '教练' }
];

// 移除错误消息的onMounted

const login = async () => {
    try {
        // 调用管理员登录API
        const res = await adminLogin(loginForm);
        console.log(res);
        
        if (res.data && res.code === 200) {
            // 登录成功，保存token
            store.setToken(res.data);
            
            // 获取用户详细信息
            const userRes = await getUserByToken();
            if (userRes.code === 200) {
                // 保存用户信息到store
                store.setUserInfo(userRes.data);
                
                ElMessage({
                    message: res.msg || '登录成功',
                    type: 'success'
                });
                
                // 跳转到首页
                router.push({ path: '/' });
            } else {
                ElMessage({
                    message: '获取用户信息失败',
                    type: 'error'
                });
            }
        } else {
            // 登录失败
            ElMessage({
                message: res.msg || '登录失败，请检查用户名和密码',
                type: 'error'
            });
        }
    } catch (error) {
        console.error('登录错误:', error);
        ElMessage({
            message: '登录失败，请稍后再试',
            type: 'error'
        });
    }
}
</script>


<template>
    <div class="body-login">
        <el-form :model="loginForm" class="login-container" @keyup.enter="login">
            <div class="title-section">
                <h1>🏓 乒乓球培训管理系统</h1>
                <p class="subtitle">欢迎登录</p>
            </div>
            
            <el-form-item label="角色选择">
                <el-radio-group v-model="loginForm.role" class="role-group">
                    <el-radio 
                        v-for="option in roleOptions" 
                        :key="option.value" 
                        :value="option.value" 
                        class="role-radio"
                    >
                        {{ option.label }}
                    </el-radio>
                </el-radio-group>
            </el-form-item>
            
            <el-form-item>
                <el-input 
                    v-model="loginForm.username" 
                    placeholder="请输入用户名"
                    prefix-icon="User"
                    size="large"
                ></el-input>
            </el-form-item>
            
            <el-form-item>
                <el-input 
                    v-model="loginForm.password" 
                    type="password" 
                    placeholder="请输入密码"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                ></el-input>
            </el-form-item>
            
            <el-form-item>
                <el-button 
                    type="primary" 
                    @click="login" 
                    class="login-btn"
                    size="large"
                    :loading="false"
                >
                    登录
                </el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
</script>

<style scoped lang="less">
.body-login {
    width: 100%;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.login-container {
    width: 450px;
    background-color: #fff;
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    
    .title-section {
        text-align: center;
        margin-bottom: 30px;
        
        h1 {
            color: #2c3e50;
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 8px;
        }
        
        .subtitle {
            color: #7f8c8d;
            font-size: 16px;
            margin: 0;
        }
    }
    
    :deep(.el-form-item) {
        margin-bottom: 20px;
        
        .el-form-item__label {
            color: #2c3e50;
            font-weight: 500;
            text-align: left;
        }
    }
    
    .role-group {
        width: 100%;
        display: flex;
        justify-content: space-between;
        
        .role-radio {
            flex: 1;
            margin-right: 0;
            
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
    
    .login-btn {
        width: 100%;
        height: 45px;
        font-size: 16px;
        font-weight: 500;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        
        &:hover {
            background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
            transform: translateY(-1px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }
    }
    
    :deep(.el-input) {
        .el-input__wrapper {
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            
            &:hover {
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }
            
            &.is-focus {
                box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
            }
        }
    }
}
</style>