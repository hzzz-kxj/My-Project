<template>
  <div class="login-container">
    <div class="pannal">
      <div class="header">
        <div class="logo">
          <i class="fas fa-video"></i>
        </div>
        <h2>择校平台登录</h2>
      </div>

      <form class="login-form" @submit.prevent="login">
        <div class="form-group">
          <div class="input-group">
            <i class="fas fa-user input-icon"></i>
            <input
              type="text"
              placeholder="用户名"
              class="form-input"
              required
              v-model="username"
            >
          </div>
        </div>

        <div class="form-group">
          <div class="input-group">
            <i class="fas fa-lock input-icon"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              placeholder="密码"
              class="form-input"
              required
              v-model="password"
            >
            <button
              type="button"
              class="toggle-password"
              @click="togglePassword"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
        </div>

        <div class="form-group">
          <div class="remember-forgot">
            <label class="remember-me">
              <input
                type="checkbox"
                id="remeber"
                name="remeber"
                v-model="rememberMe"
              >
              <span class="checkmark"></span>
              <span>记住我</span>
            </label>
            <a href="#" class="forgot-link">忘记密码?</a>
          </div>
        </div>

        <div class="form-group">
          <button
            type="submit"
            class="login-btn"
            :disabled="loading"
          >
            <i
              :class="loading ? 'fas fa-spinner fa-spin' : 'fas fa-sign-in-alt'"
            ></i>
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>

        <div class="register-link">
          还没有账户? <RouterLink to="/Register">立即注册</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

<!-- Login.vue -->
<script setup lang="ts">
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';

const username = ref('');
const password = ref('');
const rememberMe = ref(false);
const showPassword = ref(false);
const loading = ref(false);
const router = useRouter();
const userStore = useUserStore();

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const login = async () => {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码');
    return;
  }

  loading.value = true;

  try {
    const response = await axios.post('http://localhost:8080/api/users/login', {
      username: username.value,
      password: password.value
    });

    console.log('登录成功:', response.data);

    // 使用 Pinia store 设置登录状态
    userStore.login(response.data);

    // 登录成功后跳转到 page1
    router.push('/page1');

  } catch (error) {
    console.error('登录失败:', error);
    alert('登录失败，请检查用户名和密码');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  font-family: 'Microsoft YaHei UI', 'Segoe UI', 'PingFang SC', sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
  /* 使用背景图片 */
  background-image: url('@/assets/bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  /* 添加深色遮罩层，提高文字可读性 */
  position: relative;
}

/* 添加深色遮罩层 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  z-index: -1;
}

.login-container > .pannal {
  width: 100%;
  max-width: 420px;
  padding: 20px;
  z-index: 1;
}

.pannal {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  padding: 40px 35px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 添加半透明白色背景 */
.pannal::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.7);
  z-index: -1;
}

.pannal:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
}

.header {
  text-align: center;
  margin-bottom: 35px;
}

.logo {
  width: 70px;
  height: 70px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.8);
}

.logo i {
  font-size: 30px;
  color: white;
}

h2 {
  color: #333;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 25px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  color: #555;
  font-size: 18px;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 16px 16px 16px 45px;
  border: 2px solid rgba(221, 221, 221, 0.7);
  border-radius: 12px;
  font-size: 16px;
  color: #333;
  background-color: rgba(248, 249, 250, 0.8);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
}

.form-input::placeholder {
  color: #888;
}

.toggle-password {
  position: absolute;
  right: 15px;
  background: none;
  border: none;
  color: #777;
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
  transition: color 0.3s;
}

.toggle-password:hover {
  color: #667eea;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #555;
  font-size: 15px;
  position: relative;
}

.remember-me input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  height: 20px;
  width: 20px;
  background-color: rgba(248, 249, 250, 0.8);
  border: 2px solid #ccc;
  border-radius: 5px;
  margin-right: 10px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
}

.remember-me input:checked ~ .checkmark {
  background-color: #667eea;
  border-color: #667eea;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.remember-me input:checked ~ .checkmark:after {
  display: block;
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
  font-weight: 500;
}

.forgot-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, rgba(102, 126, 234, 1), rgba(118, 75, 162, 1));
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-btn i {
  font-size: 18px;
}

.register-link {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 15px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.register-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .pannal {
    padding: 30px 25px;
  }

  .login-container > .pannal {
    padding: 15px;
  }

  h2 {
    font-size: 24px;
  }

  .logo {
    width: 60px;
    height: 60px;
  }

  .logo i {
    font-size: 24px;
  }
}

/* 添加背景图片适应不同设备的样式 */
@media (max-height: 600px) {
  .login-container {
    background-attachment: scroll;
    align-items: flex-start;
    padding-top: 30px;
  }
}
</style>
