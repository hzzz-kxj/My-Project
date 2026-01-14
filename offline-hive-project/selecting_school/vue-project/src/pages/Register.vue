<template>
  <div class="register-container">
    <div class="register-panel">
      <div class="header">
        <div class="logo">
          <i class="fas fa-user-plus"></i>
        </div>
        <h2>用户注册</h2>
        <p>创建您的择校平台账户</p>
      </div>

      <form class="register-form" @submit.prevent="register">
        <div class="form-group">
          <div class="input-group">
            <i class="fas fa-user input-icon"></i>
            <input
                type="text"
                placeholder="用户名"
                class="form-input"
                required
                v-model="username"
                @focus="clearError"
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
                @focus="clearError"
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
          <div class="input-group">
            <i class="fas fa-lock input-icon"></i>
            <input
                type="password"
                placeholder="确认密码"
                class="form-input"
                required
                v-model="confirmPassword"
                @focus="clearError"
            >
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          <i class="fas fa-exclamation-circle"></i>
          {{ errorMessage }}
        </div>

        <div class="form-group">
          <button
              type="submit"
              class="register-btn"
              :disabled="loading"
          >
            <i
                :class="loading ? 'fas fa-spinner fa-spin' : 'fas fa-user-plus'"
            ></i>
            {{ loading ? '注册中...' : '立即注册' }}
          </button>
        </div>

        <div class="login-link">
          已有账户? <RouterLink to="/Login">立即登录</RouterLink>
        </div>
      </form>

      <!-- 成功弹框 -->
      <div v-if="showSuccessModal" class="modal-overlay">
        <div class="modal-content">
          <div class="modal-icon success">
            <i class="fas fa-check-circle"></i>
          </div>
          <h3>注册成功！</h3>
          <p>您的账户已创建成功，即将跳转到首页...</p>
          <button @click="goToHome" class="modal-btn">
            <i class="fas fa-home"></i>
            前往首页
          </button>
        </div>
      </div>

      <!-- 失败弹框 -->
      <div v-if="showErrorModal" class="modal-overlay">
        <div class="modal-content">
          <div class="modal-icon error">
            <i class="fas fa-times-circle"></i>
          </div>
          <h3>注册失败</h3>
          <p>{{ modalErrorMessage }}</p>
          <button @click="closeErrorModal" class="modal-btn">
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const loading = ref(false);
const errorMessage = ref('');
const showSuccessModal = ref(false);
const showErrorModal = ref(false);
const modalErrorMessage = ref('');
const router = useRouter();

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const clearError = () => {
  errorMessage.value = '';
};

const validateForm = (): boolean => {
  if (!username.value.trim()) {
    errorMessage.value = '请输入用户名';
    return false;
  }

  if (!password.value) {
    errorMessage.value = '请输入密码';
    return false;
  }

  if (password.value.length < 6) {
    errorMessage.value = '密码长度不能少于6位';
    return false;
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致';
    return false;
  }

  return true;
};

const register = async () => {
  // 表单验证
  if (!validateForm()) {
    return;
  }

  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await axios.post('http://localhost:8080/api/users/register', {
      username: username.value,
      password: password.value
    });

    console.log('注册成功:', response.data);

    // 显示成功弹框
    showSuccessModal.value = true;

    // 3秒后自动跳转到首页
    setTimeout(() => {
      goToHome();
    }, 3000);

  } catch (error: any) {
    console.error('注册失败:', error);

    // 设置弹框错误信息
    let errorMsg = '注册失败，请稍后重试';

    if (error.response) {
      if (error.response.status === 409) {
        errorMsg = '用户名已存在，请选择其他用户名';
        // 用户名重复，重置用户名输入框
        username.value = '';
      } else if (error.response.status === 400) {
        errorMsg = error.response.data || '注册失败';
      } else if (error.response.data) {
        errorMsg = error.response.data;
      }
    } else if (error.request) {
      errorMsg = '网络连接失败，请检查网络设置';
    } else {
      errorMsg = error.message || '注册失败';
    }

    modalErrorMessage.value = errorMsg;
    showErrorModal.value = true;

  } finally {
    loading.value = false;
  }
};

const goToHome = () => {
  router.push('/');
};

const closeErrorModal = () => {
  showErrorModal.value = false;
  modalErrorMessage.value = '';
  // 重置表单
  password.value = '';
  confirmPassword.value = '';
  showPassword.value = false;
};
</script>


<style scoped>
.register-container {
  font-family: 'Microsoft YaHei UI', 'Segoe UI', 'PingFang SC', sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-image: url('@/assets/bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

/* 添加深色遮罩层 */
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  z-index: -1;
}

.register-panel {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  padding: 40px 35px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
  max-width: 450px;
  width: 100%;
}

/* 添加半透明白色背景 */
.register-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.7);
  z-index: -1;
}

.register-panel:hover {
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
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.9), rgba(56, 142, 60, 0.9));
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

.header p {
  color: #666;
  font-size: 15px;
  margin-bottom: 0;
}

.register-form {
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
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
  border-color: #4CAF50;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 4px rgba(76, 175, 80, 0.2);
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
  color: #4CAF50;
}

.error-message {
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
  padding: 12px 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-left: 4px solid #f44336;
}

.error-message i {
  font-size: 16px;
}

.register-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.9), rgba(56, 142, 60, 0.9));
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #4CAF50, #388E3C);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-btn i {
  font-size: 18px;
}

.login-link {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 15px;
}

.login-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #388E3C;
  text-decoration: underline;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background-color: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  text-align: center;
  max-width: 400px;
  width: 90%;
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.modal-icon.success {
  background-color: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.modal-icon.error {
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.modal-content h3 {
  color: #333;
  font-size: 24px;
  margin-bottom: 15px;
  font-weight: 600;
}

.modal-content p {
  color: #666;
  font-size: 16px;
  margin-bottom: 25px;
  line-height: 1.5;
}

.modal-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #4CAF50, #388E3C);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.modal-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-panel {
    padding: 30px 25px;
  }

  .register-container {
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

  .modal-content {
    padding: 30px 20px;
  }
}
</style>
