<template>
  <div 
    class="enhanced-input-wrapper"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    @mousemove="handleMouseMove"
    :style="backgroundStyle"
  >
    <el-input
      v-bind="$attrs"
      :model-value="modelValue"
      @update:model-value="$emit('update:modelValue', $event)"
      :class="['enhanced-input', { 'is-focused': isFocused }]"
      @focus="handleFocus"
      @blur="handleBlur"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue';
import { prefersReducedMotion } from '@/utils/animation';

export default defineComponent({
  name: 'EnhancedInput',
  inheritAttrs: false,
  props: {
    modelValue: {
      type: [String, Number],
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue'],
  setup(props) {
    const isVisible = ref(false);
    const isFocused = ref(false);
    const mouseX = ref(0);
    const mouseY = ref(0);
    const radius = 100;
    const reducedMotion = ref(prefersReducedMotion());
    
    // 性能优化：节流鼠标移动事件
    let mouseMoveThrottle: number | null = null;

    const backgroundStyle = computed(() => {
      if (!isVisible.value || reducedMotion.value || props.disabled) return {};
      
      return {
        background: `radial-gradient(
          ${radius}px circle at ${mouseX.value}px ${mouseY.value}px,
          rgba(64, 158, 255, 0.1),
          transparent 80%
        )`
      };
    });

    const handleMouseEnter = () => {
      if (!reducedMotion.value && !props.disabled) {
        isVisible.value = true;
      }
    };

    const handleMouseLeave = () => {
      isVisible.value = false;
      if (mouseMoveThrottle) {
        cancelAnimationFrame(mouseMoveThrottle);
        mouseMoveThrottle = null;
      }
    };

    const handleMouseMove = (event: MouseEvent) => {
      if (reducedMotion.value || props.disabled) return;
      
      if (mouseMoveThrottle) {
        cancelAnimationFrame(mouseMoveThrottle);
      }
      
      mouseMoveThrottle = requestAnimationFrame(() => {
        try {
          const target = event.currentTarget as HTMLElement;
          if (!target || !target.getBoundingClientRect) return;
          
          const rect = target.getBoundingClientRect();
          if (rect) {
            mouseX.value = event.clientX - rect.left;
            mouseY.value = event.clientY - rect.top;
          }
        } catch (error) {
          console.warn('EnhancedInput mouse move error:', error);
        }
      });
    };

    const handleFocus = () => {
      isFocused.value = true;
    };

    const handleBlur = () => {
      isFocused.value = false;
    };

    // 组件卸载时清理
    onUnmounted(() => {
      if (mouseMoveThrottle) {
        cancelAnimationFrame(mouseMoveThrottle);
        mouseMoveThrottle = null;
      }
    });

    return {
      isVisible,
      isFocused,
      mouseX,
      mouseY,
      backgroundStyle,
      handleMouseEnter,
      handleMouseLeave,
      handleMouseMove,
      handleFocus,
      handleBlur
    };
  }
});
</script>

<style lang="scss" scoped>
.enhanced-input-wrapper {
  position: relative;
  width: 100%;
  border-radius: 8px;
  padding: 2px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-1px);
  }

  :deep(.enhanced-input) {
    width: 100%;
    .el-input__wrapper {
      width: 100%;
      background-color: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(64, 158, 255, 0.2);
      border-radius: 6px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

      &:hover {
        border-color: rgba(64, 158, 255, 0.4);
        box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
      }

      &.is-focus {
        border-color: #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }

    .el-input__inner {
      color: #333;
      font-weight: 500;

      &::placeholder {
        color: rgba(51, 51, 51, 0.6);
        transition: color 0.3s ease;
      }
    }

    &.is-focused {
      .el-input__wrapper {
        border-color: #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }

      .el-input__inner::placeholder {
        color: rgba(51, 51, 51, 0.4);
      }
    }
  }
}

// 动画关键帧
@keyframes glow {
  0% {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  50% {
    box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
  }
  100% {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

.enhanced-input-wrapper:hover {
  animation: glow 2s ease-in-out infinite;
}
</style>
