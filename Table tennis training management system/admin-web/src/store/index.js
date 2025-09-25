import {defineStore} from 'pinia'
import { ref } from 'vue'
function initState (){
    return {
        isCollapse:false,
        tabsList:[
            {
                path:'/home',
                name:'home',
                label:'首页',
                icon:'home'
            }
        ],
        currentMenu:null,
        token: localStorage.getItem('token') || null,
        userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
    }
}

export const useStore  = defineStore("allData",()=>{
    const state = ref(initState())
    function addTags (tag){
        if(tag.name==='home'){
            state.value.currentMenu = null;
        }else{
            // 找到是否存在
            const find = state.value.tabsList.findIndex((item)=>item.name === tag.name);
            if(find === -1){
                state.value.tabsList.push(tag);
            }
        }
    }
    function deleteTags (tag){
        state.value.tabsList = state.value.tabsList.filter((item)=>item.name!== tag.name);
    }

    function setToken(token) {
        state.value.token = token;
        localStorage.setItem('token', token);
    }

    function removeToken() {
        state.value.token = null;
        localStorage.removeItem('token');
    }

    function setUserInfo(userInfo) {
        state.value.userInfo = userInfo;
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
    }

    function removeUserInfo() {
        state.value.userInfo = null;
        localStorage.removeItem('userInfo');
    }

    function clearTabs() {
        state.value.tabsList = [
            {
                path: '/home',
                name: 'home',
                label: '首页',
                icon: 'home'
            }
        ];
        state.value.currentMenu = null;
    }

    function logout() {
        removeToken();
        removeUserInfo();
        clearTabs();
    }

    return {
        state,
        addTags,
        deleteTags,
        setToken,
        removeToken,
        setUserInfo,
        removeUserInfo,
        clearTabs,
        logout
    }
})