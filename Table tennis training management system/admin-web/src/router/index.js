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
                path:"/user",
                name:"user",
                component:()=>import("@/views/User.vue"),
            },
            {
                path:"/campus",
                name:"campus",
                component:()=>import("@/views/Campus.vue"),
            },
            {
                path:"/coachDetail",
                name:"coachDetail",
                component:()=>import("@/views/CoachDetail.vue"),
            },
            {
                path:"/course",
                name:"course",
                component:()=>import("@/views/Course.vue"),
            },
            {
                path:"/tuitionRecord",
                name:"tuitionRecord",
                component:()=>import("@/views/TuitionRecord.vue"),
            },
            {
                path:"/bookingRecord",
                name:"bookingRecord",
                component:()=>import("@/views/BookingRecord.vue"),
            },
            {
                path:"/evaluationRecord",
                name:"evaluationRecord",
                component:()=>import("@/views/EvaluationRecord.vue"),
            },
            {
                path:"/studentCoach",
                name:"studentCoach",
                component:()=>import("@/views/StudentCoach.vue"),
            },
            {
                path:"/myCourse",
                name:"myCourse",
                component:()=>import("@/views/MyCourse.vue"),
            },
            {
                path:"/myBooking",
                name:"myBooking",
                component:()=>import("@/views/MyBooking.vue"),
            },
            {
                path:"/myEvaluation",
                name:"myEvaluation",
                component:()=>import("@/views/MyEvaluation.vue"),
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

//导出路由对象

export default router