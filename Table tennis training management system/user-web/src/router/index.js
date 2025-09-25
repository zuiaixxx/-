import {createRouter,createWebHistory} from "vue-router"



//制定路由规则

const routes = [
    {
        path:"/",
        name:"main",
        component:()=>import("@/views/Main.vue"), // 动态导入的方式  按需加载
        redirect:"/home",
        children:[
            {
                path:"home",
                name:"home",
                component:()=>import("@/views/Home.vue"),
            },
            {
                path:"/campus",
                name:"campus",
                component:()=>import("@/views/Campus.vue"),
            },
            {
                path:"/coach",
                name:"coach",
                component:()=>import("@/views/Coach.vue"),
            },
            {
                path:"/course",
                name:"course",
                component:()=>import("@/views/Course.vue"),
            },
            {
                path:"/profile",
                name:"profile",
                component:()=>import("@/views/Profile.vue"),
            }
        ]
    },
    {
        path:"/login",
        name:"login",
        component:()=>import("@/views/Login.vue"),
    }
    
]

//创建路由对象

const router = createRouter({
    history:createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

//导出路由对象

export default router