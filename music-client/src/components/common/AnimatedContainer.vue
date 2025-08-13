<template>
  <div 
    ref="containerRef"
    class="animated-container"
    :class="[
      `animation-${animationType}`,
      { 'is-visible': isVisible, 'is-animated': hasAnimated }
    ]"
    :style="containerStyle"
  >
    <div class="content-wrapper">
      <slot></slot>
    </div>
    
    <!-- 滑动遮罩效果 -->
    <div 
      v-if="showMask"
      class="slide-mask"
      :style="maskStyle"
    ></div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { prefersReducedMotion, getAnimationConfig } from '@/utils/animation';

export default defineComponent({
  name: 'AnimatedContainer',
  props: {
    animationType: {
      type: String,
      default: 'fadeInUp',
      validator: (value: string) => [
        'fadeIn', 'fadeInUp', 'fadeInDown', 'fadeInLeft', 'fadeInRight',
        'slideInUp', 'slideInDown', 'slideInLeft', 'slideInRight',
        'zoomIn', 'bounceIn', 'boxReveal'
      ].includes(value)
    },
    duration: {
      type: Number,
      default: 0.6
    },
    delay: {
      type: Number,
      default: 0
    },
    maskColor: {
      type: String,
      default: '#409eff'
    },
    threshold: {
      type: Number,
      default: 0.1
    },
    triggerOnce: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const containerRef = ref<HTMLElement | null>(null);
    const isVisible = ref(false);
    const hasAnimated = ref(false);
    const observer = ref<IntersectionObserver | null>(null);
    
    // 性能优化配置
    const reducedMotion = ref(prefersReducedMotion());
    const animationConfig = getAnimationConfig({
      duration: props.duration,
      delay: props.delay
    });

    // 计算样式
    const containerStyle = computed(() => ({
      '--animation-duration': `${animationConfig.duration}s`,
      '--animation-delay': `${animationConfig.delay}s`
    }));

    const showMask = computed(() => props.animationType === 'boxReveal');

    const maskStyle = computed(() => ({
      backgroundColor: props.maskColor,
      animationDuration: `${props.duration}s`,
      animationDelay: `${props.delay}s`
    }));

    // 初始化 Intersection Observer
    const initObserver = () => {
      if (!containerRef.value) return;

      // 如果禁用动画，直接设为可见
      if (reducedMotion.value) {
        isVisible.value = true;
        hasAnimated.value = true;
        return;
      }

      observer.value = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting && !hasAnimated.value) {
              isVisible.value = true;
              hasAnimated.value = true;
              
              if (props.triggerOnce && observer.value) {
                observer.value.disconnect();
              }
            } else if (!props.triggerOnce && !entry.isIntersecting) {
              isVisible.value = false;
            }
          });
        },
        {
          threshold: props.threshold,
          rootMargin: '50px'
        }
      );

      observer.value.observe(containerRef.value);
    };

    onMounted(() => {
      nextTick(() => {
        initObserver();
      });
    });

    onUnmounted(() => {
      if (observer.value) {
        observer.value.disconnect();
      }
    });

    return {
      containerRef,
      isVisible,
      hasAnimated,
      containerStyle,
      showMask,
      maskStyle
    };
  }
});
</script>

<style lang="scss" scoped>
.animated-container {
  position: relative;
  overflow: hidden;
  
  .content-wrapper {
    opacity: 1; /* 默认可见，避免表单不显示 */
    transform: translateY(30px);
    transition: all var(--animation-duration) ease-out var(--animation-delay);
  }
  
  /* 只有在动画激活时才隐藏 */
  &:not(.is-visible) .content-wrapper {
    opacity: 0;
  }

  // 滑动遮罩
  .slide-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 10;
    transform: translateX(-100%);
  }

  // 可见状态
  &.is-visible {
    .content-wrapper {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

// 动画类型定义
.animation-fadeIn {
  &.is-visible .content-wrapper {
    opacity: 1;
    transform: none;
  }
}

.animation-fadeInUp {
  .content-wrapper {
    transform: translateY(50px);
  }
  
  &.is-visible .content-wrapper {
    transform: translateY(0);
  }
}

.animation-fadeInDown {
  .content-wrapper {
    transform: translateY(-50px);
  }
  
  &.is-visible .content-wrapper {
    transform: translateY(0);
  }
}

.animation-fadeInLeft {
  .content-wrapper {
    transform: translateX(-50px);
  }
  
  &.is-visible .content-wrapper {
    transform: translateX(0);
  }
}

.animation-fadeInRight {
  .content-wrapper {
    transform: translateX(50px);
  }
  
  &.is-visible .content-wrapper {
    transform: translateX(0);
  }
}

.animation-slideInUp {
  .content-wrapper {
    transform: translateY(100%);
  }
  
  &.is-visible .content-wrapper {
    transform: translateY(0);
  }
}

.animation-slideInDown {
  .content-wrapper {
    transform: translateY(-100%);
  }
  
  &.is-visible .content-wrapper {
    transform: translateY(0);
  }
}

.animation-slideInLeft {
  .content-wrapper {
    transform: translateX(-100%);
  }
  
  &.is-visible .content-wrapper {
    transform: translateX(0);
  }
}

.animation-slideInRight {
  .content-wrapper {
    transform: translateX(100%);
  }
  
  &.is-visible .content-wrapper {
    transform: translateX(0);
  }
}

.animation-zoomIn {
  .content-wrapper {
    transform: scale(0.3);
  }
  
  &.is-visible .content-wrapper {
    transform: scale(1);
  }
}

.animation-bounceIn {
  .content-wrapper {
    transform: scale(0.3);
    transition-timing-function: cubic-bezier(0.68, -0.55, 0.265, 1.55);
  }
  
  &.is-visible .content-wrapper {
    transform: scale(1);
  }
}

.animation-boxReveal {
  .content-wrapper {
    opacity: 0;
    transform: translateY(75px);
  }
  
  &.is-visible {
    .content-wrapper {
      opacity: 1;
      transform: translateY(0);
      transition-delay: calc(var(--animation-delay) + 0.25s);
    }
    
    .slide-mask {
      animation: slideOut var(--animation-duration) ease-in var(--animation-delay) forwards;
    }
  }
}

// 滑动遮罩动画
@keyframes slideOut {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// 响应式优化
@media (max-width: 768px) {
  .animated-container {
    .content-wrapper {
      transform: translateY(20px);
    }
  }
  
  .animation-fadeInUp .content-wrapper,
  .animation-fadeInDown .content-wrapper {
    transform: translateY(30px);
  }
  
  .animation-fadeInLeft .content-wrapper,
  .animation-fadeInRight .content-wrapper {
    transform: translateX(30px);
  }
}

// 减少动画偏好
@media (prefers-reduced-motion: reduce) {
  .animated-container {
    .content-wrapper {
      transition: opacity 0.3s ease;
      transform: none !important;
    }
    
    .slide-mask {
      display: none;
    }
  }
}
</style>
