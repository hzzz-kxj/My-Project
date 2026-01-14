<!-- 页面跳转模板 -->
<template>
  <div id="app">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 如果用户已登录，显示顶部标题栏和带侧边栏的布局 -->
      <div v-if="isLoggedIn" class="logged-in-layout">
        <!-- 顶部标题栏 -->
        <header class="app-header">
          <div class="header-content">
            <div class="header-title">
              <h1>用户择校平台数据分析及推荐系统</h1>
              <p class="welcome-text">欢迎回来，{{ username }}！</p>
            </div>
            <div class="header-actions">
              <button class="logout-btn" @click="logout">
                <i class="fas fa-sign-out-alt"></i>
                退出登录
              </button>
            </div>
          </div>
        </header>

        <!-- 侧边栏和内容区域 -->
        <div class="layout-container">
          <!-- 固定侧边栏 -->
          <nav class="sidebar">
            <div class="sidebar-menu">
              <RouterLink to="/page1" class="sidebar-link">
                <i class="fas fa-chart-bar"></i>
                <span>页面1</span>
              </RouterLink>
              <RouterLink to="/page2" class="sidebar-link">
                <i class="fas fa-university"></i>
                <span>页面2</span>
              </RouterLink>
              <RouterLink to="/page3" class="sidebar-link">
                <i class="fas fa-graduation-cap"></i>
                <span>页面3</span>
              </RouterLink>
              <RouterLink to="/page4" class="sidebar-link">
                <i class="fas fa-chart-pie"></i>
                <span>页面4</span>
              </RouterLink>
              <RouterLink to="/page5" class="sidebar-link">
                <i class="fas fa-search"></i>
                <span>页面5</span>
              </RouterLink>
              <RouterLink to="/page6" class="sidebar-link">
                <i class="fas fa-cog"></i>
                <span>页面6</span>
              </RouterLink>
            </div>
          </nav>

          <!-- 主要内容区域 -->
          <main class="content-area">
            <div class="content-wrapper">
              <RouterView/>
            </div>
          </main>
        </div>
      </div>

      <!-- 如果用户未登录，只显示内容区域（用于首页、登录、注册页面） -->
      <div v-else class="guest-layout">
        <RouterView/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, watch, computed} from 'vue'
import {useRouter} from 'vue-router'
import { useUserStore } from '@/store/user';

const router = useRouter()
const userStore = useUserStore()

// 直接使用 store 中的状态
const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username)

// 监听路由变化
watch(() => router.currentRoute.value, (newRoute) => {
  // 如果用户登录后访问首页，重定向到 Home
  if (isLoggedIn.value && newRoute.path === '/Home') {
    router.push('/page1')
  }

  // 如果用户未登录，确保不能访问需要登录的页面
  if (!isLoggedIn.value && newRoute.path !== '/Home' &&
    newRoute.path !== '/Login' && newRoute.path !== '/Register') {
    router.push('/Home')
  }
}, {immediate: true})

// 退出登录
const logout = () => {
  userStore.logout()
  router.push('/Home')
}

// 监听 localStorage 变化
window.addEventListener('storage', (e) => {
  if (e.key === 'isLoggedIn') {
    userStore.checkLoginStatus()
  }
})

onMounted(() => {
  // 强制刷新后总是显示首页（无侧边栏布局）
  userStore.isLoggedIn = false
  userStore.userInfo = { username: '' }

  // 如果当前页面需要登录权限，跳转到 Home
  const currentPath = router.currentRoute.value.path
  const requiresAuth = !['/Home', '/Login', '/Register'].includes(currentPath)

  if (requiresAuth) {
    router.push('/Home')
  }
})
</script>

<style>
/* 重置一些默认样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Segoe UI', 'Microsoft YaHei', 'PingFang SC', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8fafc;
}

/* 登录后的整体布局 */
.logged-in-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 顶部标题栏样式 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  text-align: center;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30px;
  max-width: 100%;
  margin: 0 auto;
}

.header-title h1 {
  color: white;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.welcome-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  margin-top: 4px;
  font-weight: 400;
}

/* 退出按钮样式 */
.logout-btn {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
}

.logout-btn:active {
  transform: translateY(0);
}

/* 侧边栏和内容布局 */
.layout-container {
  display: flex;
  flex: 1;
  margin-top: 70px; /* 为顶部标题栏留出空间 */
  min-height: calc(100vh - 70px);
}

/* 侧边栏样式 */
.sidebar {
  position: fixed;
  left: 0;
  top: 70px; /* 从标题栏下方开始 */
  bottom: 0;
  width: 220px;
  background: white;
  border-right: 1px solid #e5e7eb;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  z-index: 900;
  overflow-y: auto;
}

.sidebar-menu {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: #4b5563;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
  margin: 0 8px;
  border-radius: 8px;
}

.sidebar-link:hover {
  background-color: #f3f4f6;
  color: #667eea;
}

.sidebar-link.router-link-active {
  background-color: #eff6ff;
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 600;
}

.sidebar-link i {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}

.sidebar-link span {
  flex: 1;
}

/* 主要内容区域样式 */
.content-area {
  flex: 1;
  margin-left: 220px; /* 侧边栏宽度 */
  padding: 0;
  min-height: calc(100vh - 70px);
  overflow-y: auto;
}

.content-wrapper {
  padding: 30px;
  max-width: 100%;
}

/* 未登录时的布局 */
.guest-layout {
  width: 100%;
  min-height: 100vh;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.2);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .sidebar {
    width: 200px;
  }

  .content-area {
    margin-left: 200px;
  }

  .content-wrapper {
    padding: 25px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 20px;
  }

  .header-title h1 {
    font-size: 1.3rem;
  }

  .sidebar {
    width: 180px;
  }

  .content-area {
    margin-left: 180px;
  }

  .content-wrapper {
    padding: 20px;
  }
}

@media (max-width: 576px) {
  .app-header {
    height: 60px;
  }

  .layout-container {
    margin-top: 60px;
    min-height: calc(100vh - 60px);
  }

  .sidebar {
    top: 60px;
    width: 60px;
  }

  .sidebar-link span {
    display: none;
  }

  .sidebar-link {
    justify-content: center;
    padding: 14px;
    margin: 0 4px;
  }

  .content-area {
    margin-left: 60px;
  }

  .content-wrapper {
    padding: 15px;
  }
}
</style>
