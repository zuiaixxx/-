<template>
    <el-aside :width="width">
        <el-menu 
        background-color="#545c64"
        text-color="#fff"
        :collapse="isCollapse"
        :collapse-transition="false"
        :default-active="activeMenu"
        >
            <h3 v-show="!isCollapse">乒乓球培训管理系统</h3>
            <h3 v-show="isCollapse">后台</h3>
            <el-menu-item v-for="item in noChildren" :index="item.path" :key="item.path" @click="handleMenu(item)">
                <component class="icons" :is="item.icon"></component>
                <span>{{ item.label }}</span>
            </el-menu-item>
            <el-sub-menu v-for="item in hasChildren" :index="item.path" :key="item.path">
                <template #title>
                    <component class="icons" :is="item.icon"></component>
                    <span>{{ item.label }}</span>
                </template>
                <el-menu-item-group>
                    <el-menu-item 
                    v-for="subItem in item.children" 
                    :index="subItem.path" 
                    :key="subItem.path">item
                    <component class="icons" :is="subItem.icon">
                    </component>
                    <span>{{ subItem.label }}</span>
                    </el-menu-item>
                </el-menu-item-group>
            </el-sub-menu>
        </el-menu>
    </el-aside>
</template>
<script setup>
import { ref, computed } from 'vue'
import {useStore} from '@/store/index.js'
import { useRouter,useRoute } from 'vue-router';
const router = useRouter()
const route = useRoute()
const store = useStore()
const activeMenu = computed(()=>{
    return route.path
})
const handleMenu = (item) => {
 router.push(item.path)
 store.addTags(item)
}
const list = ref([
    {
        path: '/home',
        name: 'home',
        label: '首页',
        icon: 'house',
        url: 'Home',
        roles: ['superAdmin', 'campusAdmin', 'coach'] // 所有角色都可见
    },
    {
        path: '/user',
        name: 'user',
        label: '用户管理',
        icon: 'user',
        url: 'User',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/campus',
        name: 'campus',
        label: '校区管理',
        icon: 'office-building',
        url: 'Campus',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/coachDetail',
        name: 'coachDetail',
        label: '教练信息管理',
        icon: 'avatar',
        url: 'CoachDetail',
        roles: ['superAdmin', 'campusAdmin'] // 超级管理员和校区管理员可见
    },
    {
        path: '/course',
        name: 'course',
        label: '课程排班管理',
        icon: 'calendar',
        url: 'Course',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/tuitionRecord',
        name: 'tuitionRecord',
        label: '缴费记录管理',
        icon: 'coin',
        url: 'TuitionRecord',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/bookingRecord',
        name: 'bookingRecord',
        label: '预约记录管理',
        icon: 'document',
        url: 'BookingRecord',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/evaluationRecord',
        name: 'evaluationRecord',
        label: '评价记录管理',
        icon: 'star',
        url: 'EvaluationRecord',
        roles: ['superAdmin'] // 只有超级管理员可见
    },
    {
        path: '/studentCoach',
        name: 'studentCoach',
        label: '我的学生',
        icon: 'connection',
        url: 'StudentCoach',
        roles: ['coach'] // 只有教练可见
    },
    {
        path: '/myCourse',
        name: 'myCourse',
        label: '我的课程',
        icon: 'reading',
        url: 'MyCourse',
        roles: ['coach'] // 只有教练可见
    },
    {
        path: '/myBooking',
        name: 'myBooking',
        label: '我的预约',
        icon: 'calendar',
        url: 'MyBooking',
        roles: ['coach'] // 只有教练可见
    },
    {
        path: '/myEvaluation',
        name: 'myEvaluation',
        label: '我的评价',
        icon: 'star',
        url: 'MyEvaluation',
        roles: ['coach'] // 只有教练可见
    },
    {
        path: '/profile',
        name: 'profile',
        label: '个人中心',
        icon: 'user',
        url: 'Profile',
        roles: ['superAdmin', 'campusAdmin', 'coach'] // 所有角色都可见
    },
])
const noChildren = computed(() => {
    const userRole = store.state.userInfo?.role;
    if (!userRole) return [];
    
    return list.value.filter(item => {
        return !item.children && item.roles.includes(userRole);
    });
});

const hasChildren = computed(() => {
    const userRole = store.state.userInfo?.role;
    if (!userRole) return [];
    
    return list.value.filter(item => {
        return item.children && item.roles.includes(userRole);
    });
});

const isCollapse = computed(()=>{
    return store.state.isCollapse
})  
const width = computed(()=>{
   return store.state.isCollapse ? "64px" : "180px"
})



</script>
<style lang="less" scoped>
.icons{
    width: 18px;
    height: 18px;
    margin-right: 5px;
}
.el-menu{
    border-right: none;
    h3{
        line-height: 48px;
        color: #fff;
        text-align: center;
        
    }
}
.el-aside{
    height: 100%;
    background-color: #545c64;
}


</style>