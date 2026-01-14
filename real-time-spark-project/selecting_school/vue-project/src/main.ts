import {createApp} from 'vue'
import App from './App.vue'
import {createRouter, createWebHistory} from "vue-router";
import Home from './pages/Home.vue';
import Login from './pages/Login.vue';
import Register from './pages/Register.vue';
import TestPage1 from './pages/TestPage1.vue';
import TestPage2 from './pages/TestPage2.vue';
import TestPage3 from './pages/TestPage3.vue';
import TestPage4 from "@/pages/TestPage4.vue";
import TestPage5 from "@/pages/TestPage5.vue";
import TestPage6 from "@/pages/TestPage6.vue";

//1. 配置路由规则
const routes = [
  {path: "/", redirect: "/Home"},
  {path: "/Home", component: Home},
  {path: "/Login", component: Login},
  {path: "/Register", component: Register},
  {path: "/page1", component: TestPage1},
  {path: "/page2", component: TestPage2},
  {path: "/page3", component: TestPage3},
  {path: "/page4", component: TestPage4},
  {path: "/page5", component: TestPage5},
  {path: "/page6", component: TestPage6},
]

//2. 创建路由器
const router = createRouter({
  history: createWebHistory(),
  routes
})

//3. 加载路由器
const app = createApp(App);
app.use(router)

import {createPinia} from 'pinia';

const pinia = createPinia()
app.use(pinia)

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'

app.use(ElementPlus)

import axios from "axios";
import HomePage from "@/pages/Home.vue";

axios.defaults.baseURL = "http://localhost:8080"
app.config.globalProperties.$http = axios;

app.mount('#app')
