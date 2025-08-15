<template>
  <transition name="announcement" appear>
    <div v-if="show" class="announcement-container">
      <div class="announcement-card">
        <!-- 背景装饰 -->
        <div class="announcement-bg">
          <div class="bg-pattern"></div>
          <div class="bg-glow"></div>
        </div>
        
        <!-- 主要内容 -->
        <div class="announcement-content">
          <div class="announcement-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L13.09 8.26L20 9L13.09 9.74L12 16L10.91 9.74L4 9L10.91 8.26L12 2Z" fill="currentColor"/>
              <path d="M19 15L20.09 18.26L24 19L20.09 19.74L19 23L17.91 19.74L14 19L17.91 18.26L19 15Z" fill="currentColor"/>
              <path d="M5 15L6.09 18.26L10 19L6.09 19.74L5 23L3.91 19.74L0 19L3.91 18.26L5 15Z" fill="currentColor"/>
            </svg>
          </div>
          
          <div class="announcement-text">
            <h3 class="announcement-title">解锁更多精彩功能</h3>
            <p class="announcement-description">
              注册账户，享受歌曲点赞、评论、收藏音乐等专属功能
            </p>
          </div>
          
          <div class="announcement-actions">
            <button 
              type="button" 
              class="btn-register" 
              @click="goToRegister"
            >
              <span>立即注册</span>
              <svg class="btn-arrow" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
            
            <button 
              type="button" 
              class="btn-close" 
              @click="$emit('hide')"
              title="关闭"
            >
              <X class="close-icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance } from 'vue';
import { X } from 'lucide-vue-next';
import mixin from '@/mixins/mixin';
import { NavName, RouterName } from '@/enums';

export default defineComponent({
  components: { X },
  emits: ['hide'],
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  setup() {
    const { changeIndex, routerManager } = mixin();
    
    const goToRegister = () => {
      // 正确设置导航栏状态为注册
      changeIndex(NavName.SignUp);
      routerManager(RouterName.SignUp, { path: RouterName.SignUp });
    };
    
    return { goToRegister };
  }
});
</script>

<style lang="scss" scoped>
.announcement-container {
  position: fixed;
  bottom: 60px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  max-width: 800px;
  width: calc(100vw - 32px);
  pointer-events: none;
}

.announcement-card {
  position: relative;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(248, 250, 252, 0.95) 100%);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 
    0 20px 25px -5px rgba(0, 0, 0, 0.1),
    0 10px 10px -5px rgba(0, 0, 0, 0.04),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  overflow: hidden;
  pointer-events: auto;
}

.announcement-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  
  .bg-pattern {
    position: absolute;
    inset: 0;
    background-image: 
      radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 40% 80%, rgba(119, 198, 255, 0.1) 0%, transparent 50%);
    animation: patternFloat 8s ease-in-out infinite;
  }
  
  .bg-glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: conic-gradient(
      from 0deg,
      transparent,
      rgba(120, 119, 198, 0.1),
      transparent,
      rgba(255, 119, 198, 0.1),
      transparent,
      rgba(119, 198, 255, 0.1),
      transparent
    );
    animation: rotate 20s linear infinite;
  }
}

.announcement-content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
}

.announcement-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  animation: iconPulse 2s ease-in-out infinite;
  
  svg {
    width: 30px;
    height: 30px;
  }
}

.announcement-text {
  flex: 1;
  min-width: 0;
}

.announcement-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.announcement-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.announcement-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-register {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.6);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  .btn-arrow {
    width: 16px;
    height: 16px;
    transition: transform 0.3s ease;
  }
  
  &:hover .btn-arrow {
    transform: translateX(2px);
  }
}

.btn-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(107, 114, 128, 0.1);
  border: none;
  border-radius: 8px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
    transform: scale(1.1);
  }
  
  .close-icon {
    width: 16px;
    height: 16px;
  }
}

// 动画
@keyframes patternFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(10px, -10px) rotate(1deg);
  }
  66% {
    transform: translate(-5px, 5px) rotate(-1deg);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

// 过渡动画
.announcement-enter-active {
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.announcement-leave-active {
  transition: all 0.3s ease-in;
}

.announcement-enter-from {
  opacity: 0;
  transform: translateX(-50%) translateY(100px) scale(0.8);
}

.announcement-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(20px) scale(0.95);
}

// 响应式设计
@media (max-width: 640px) {
  .announcement-content {
    flex-direction: column;
    text-align: center;
    gap: 12px;
    padding: 16px 20px;
  }
  
  .announcement-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .btn-register {
    flex: 1;
    justify-content: center;
  }
}
</style>