<template>
  <div class="login-logo enhanced">
    <background-effects />
    <div class="logo-content">
      <animated-container animation-type="bounceIn" :delay="0.3">
        <yin-icon :icon="icon" class="floating-icon"></yin-icon>
      </animated-container>
      <animated-container animation-type="fadeInUp" :delay="0.6">
        <div class="welcome-text">
          <h2>欢迎回来</h2>
          <p>享受你的音乐时光</p>
        </div>
      </animated-container>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import YinIcon from "./YinIcon.vue";
import BackgroundEffects from "@/components/common/BackgroundEffects.vue";
import AnimatedContainer from "@/components/common/AnimatedContainer.vue";
import { Icon } from "@/enums";

export default defineComponent({
  components: {
    YinIcon,
    BackgroundEffects,
    AnimatedContainer,
  },
  data() {
    return {
      icon: Icon.ERJI,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.login-logo {
  // 简洁现代的背景设计 - 去掉所有紫色
  background: linear-gradient(135deg, 
    #f8fafc 0%, 
    #f1f5f9 50%, 
    #e2e8f0 100%
  );
  height: calc(100vh - $header-height - $footer-height);
  min-width: 50vw;
  overflow: hidden;
  position: relative;
  @include layout(center, center);

  // 增强版样式 - 纯净设计
  &.enhanced {
    background: linear-gradient(135deg, 
      #ffffff 0%, 
      #f8fafc 50%, 
      #f1f5f9 100%
    );
    
    // 添加微妙的几何装饰
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: 
        radial-gradient(circle at 20% 80%, rgba(64, 158, 255, 0.03) 0%, transparent 50%),
        radial-gradient(circle at 80% 20%, rgba(64, 158, 255, 0.02) 0%, transparent 50%),
        radial-gradient(circle at 40% 40%, rgba(64, 158, 255, 0.01) 0%, transparent 50%);
      pointer-events: none;
    }
    
    // 添加微妙的网格纹理
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: 
        linear-gradient(rgba(64, 158, 255, 0.02) 1px, transparent 1px),
        linear-gradient(90deg, rgba(64, 158, 255, 0.02) 1px, transparent 1px);
      background-size: 50px 50px;
      pointer-events: none;
      opacity: 0.3;
    }
  }

  .logo-content {
    position: relative;
    z-index: 2;
    text-align: center;
  }

  .floating-icon {
    .icon {
      @include icon(36rem, $color-blue-dark);
      transform: rotate(-30deg);
      filter: drop-shadow(0 10px 30px rgba(0, 0, 0, 0.3));
      animation: iconFloat 6s ease-in-out infinite;
      transition: all 0.3s ease;

      &:hover {
        transform: rotate(-30deg) scale(1.1);
        filter: drop-shadow(0 15px 40px rgba(0, 0, 0, 0.4));
      }
    }
  }

  .welcome-text {
    margin-top: 40px;
    color: #2d3748;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    h2 {
      font-size: 2.5rem;
      font-weight: 700;
      margin: 0 0 10px 0;
      background: linear-gradient(135deg, #2d3748, #4a5568);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      animation: textShimmer 3s ease-in-out infinite;
    }

    p {
      font-size: 1.2rem;
      font-weight: 400;
      margin: 0;
      opacity: 0.8;
      letter-spacing: 0.5px;
      color: #64748b;
    }
  }
}

// 动画定义
@keyframes iconFloat {
  0%, 100% {
    transform: rotate(-30deg) translateY(0);
  }
  25% {
    transform: rotate(-25deg) translateY(-10px);
  }
  50% {
    transform: rotate(-30deg) translateY(-5px);
  }
  75% {
    transform: rotate(-35deg) translateY(-15px);
  }
}

@keyframes textShimmer {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

// 响应式设计
@media screen and (min-width: $sm) {
  .login-logo {
    width: 50vw;
  }
}

@media screen and (max-width: $sm) {
  .login-logo {
    width: 100vw;
    
    .floating-icon .icon {
      @include icon(24rem, $color-blue-dark);
    }
    
    .welcome-text {
      margin-top: 30px;
      
      h2 {
        font-size: 2rem;
      }
      
      p {
        font-size: 1rem;
      }
    }
  }
}

// 减少动画偏好
@media (prefers-reduced-motion: reduce) {
  .floating-icon .icon {
    animation: none;
    transform: rotate(-30deg);
  }
  
  .welcome-text h2 {
    animation: none;
    background: white;
    -webkit-text-fill-color: white;
  }
}
</style>
