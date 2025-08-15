<template>
  <div class="login-container">
    <!-- 左侧视频区域 -->
    <div class="video-section">
      <video 
        class="background-video" 
        autoplay 
        muted 
        loop
        playsinline
      >
        <source src="@/assets/video/managevideo.mp4" type="video/mp4">
        您的浏览器不支持视频播放。
      </video>
    </div>
    
    <!-- 右侧登录表单区域 -->
    <div class="form-section">
      <div class="login-card">
        <div class="logo-section">
          <img src="@/assets/images/npu_logo.png" alt="Logo" class="logo-img" />
          <h1 class="title">{{ nusicName }}</h1>
          <p class="subtitle">欢迎使用音乐管理系统</p>
        </div>
        
        <div class="form-content">
          <el-form :model="ruleForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="ruleForm.username" 
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
                class="form-input"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                type="password" 
                placeholder="请输入密码" 
                v-model="ruleForm.password" 
                @keyup.enter="submitForm"
                prefix-icon="Lock"
                size="large"
                class="form-input"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button 
                class="login-btn" 
                type="primary" 
                @click="submitForm"
                size="large"
                :loading="loading"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api/index";
import { RouterName, MUSICNAME } from "@/enums";

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();

    const nusicName = ref(MUSICNAME);
    const loading = ref(false);
    const ruleForm = reactive({
      username: "admin",
      password: "123",
    });
    const rules = reactive({
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    });
    async function submitForm() {
      loading.value = true;
      try {
        let username = ruleForm.username;
        let password = ruleForm.password;
        const result = (await HttpManager.getLoginStatus({username,password})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) routerManager(RouterName.Info, { path: RouterName.Info });
      } finally {
        loading.value = false;
      }
    }
    return {
      nusicName,
      loading,
      ruleForm,
      rules,
      submitForm,
    };
  },
});
</script>

<style scoped>
.login-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* 左侧视频区域 */
.video-section {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.video-content {
  text-align: center;
  color: white;
  z-index: 3;
}

.video-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background: linear-gradient(135deg, #fff 0%, #e0e0e0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.video-subtitle {
  font-size: 1.5rem;
  font-weight: 300;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  opacity: 0.9;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg,rgba(248, 248, 248, 0.45) 0%,rgb(151, 154, 159) 100%);
  padding: 2rem;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideUp 0.8s ease-out;
  width: 100%;
  max-width: 450px;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-img {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  margin-bottom: 1.5rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.logo-img:hover {
  transform: scale(1.05);
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg,rgb(0, 0, 0) 0%,rgb(13, 1, 24) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1rem;
  color: #7f8c8d;
  margin: 0;
  font-weight: 400;
}

.form-content {
  margin-top: 2rem;
}

.login-form {
  width: 100%;
}

.form-input {
  margin-bottom: 20px;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solidrgb(255, 255, 255);
  transition: all 0.3s ease;
  background:rgb(255, 255, 255);
}

.form-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  background: #fff;
}

.form-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: #fff;
}

.form-input :deep(.el-input__inner) {
  font-size: 16px;
  color: #2c3e50;
  font-weight: 500;
}

.form-input :deep(.el-input__inner::placeholder) {
  color: #95a5a6;
  font-weight: 400;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg,rgb(78, 20, 20) 0%,rgb(50, 47, 47) 100%);
  border: none;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-container {
    flex-direction: column;
  }
  
  .video-section {
    height: 40vh;
  }
  
  .form-section {
    height: 60vh;
    padding: 1rem;
  }
  
  .video-title {
    font-size: 2.5rem;
  }
  
  .video-subtitle {
    font-size: 1.2rem;
  }
}

@media (max-width: 768px) {
  .video-section {
    height: 35vh;
  }
  
  .form-section {
    height: 65vh;
  }
  
  .login-card {
    margin: 1rem;
    padding: 2rem 1.5rem;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .logo-img {
    width: 60px;
    height: 60px;
  }
  
  .video-title {
    font-size: 2rem;
  }
  
  .video-subtitle {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem 1rem;
  }
  
  .video-title {
    font-size: 1.5rem;
  }
  
  .video-subtitle {
    font-size: 0.9rem;
  }
}
</style>
