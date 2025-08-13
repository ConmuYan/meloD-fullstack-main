<template>
  <div class="background-effects">
    <!-- 流体背景渐变 -->
    <div class="gradient-orb orb-1"></div>
    <div class="gradient-orb orb-2"></div>
    <div class="gradient-orb orb-3"></div>
    
    <!-- 粒子效果 -->
    <div class="particles">
      <div 
        v-for="i in particleCount" 
        :key="i" 
        class="particle"
        :style="getParticleStyle(i)"
      ></div>
    </div>
    
    <!-- 波纹效果 -->
    <div class="ripple-container">
      <div 
        v-for="i in rippleCount" 
        :key="i" 
        class="ripple"
        :style="getRippleStyle(i)"
      ></div>
    </div>
    
    <!-- 音乐波形效果 -->
    <div class="music-waves">
      <div 
        v-for="i in waveCount" 
        :key="i" 
        class="wave-bar"
        :style="getWaveStyle(i)"
      ></div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue';
import { prefersReducedMotion } from '@/utils/animation';
import { getPerformanceBasedSettings, isMobile } from '@/utils/responsive';

export default defineComponent({
  name: 'BackgroundEffects',
  props: {
    enableParticles: {
      type: Boolean,
      default: true
    },
    enableComplexAnimations: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const performanceSettings = getPerformanceBasedSettings();
    const reducedMotion = ref(prefersReducedMotion());
    const mobile = ref(isMobile());
    
    // 根据性能和设备调整效果数量
    const particleCount = computed(() => {
      if (reducedMotion.value || !props.enableParticles || !performanceSettings.enableParticles) return 0;
      return mobile.value ? 10 : 20;
    });
    const rippleCount = computed(() => reducedMotion.value ? 0 : (mobile.value ? 4 : 8));
    const waveCount = computed(() => reducedMotion.value ? 0 : (mobile.value ? 6 : 12));
    
    // 动画控制
    const animationFrame = ref<number | null>(null);
    const time = ref(0);
    const isVisible = ref(true);

    // 粒子样式计算
    const getParticleStyle = (index: number) => {
      const count = particleCount.value;
      if (count === 0) return {};
      const angle = (index / count) * 360;
      const radius = 150 + Math.sin(time.value * 0.001 + index) * 50;
      const x = Math.cos(angle * Math.PI / 180) * radius;
      const y = Math.sin(angle * Math.PI / 180) * radius;
      const scale = 0.5 + Math.sin(time.value * 0.002 + index) * 0.3;
      
      return {
        transform: `translate(${x}px, ${y}px) scale(${scale})`,
        animationDelay: `${index * 0.1}s`,
        opacity: 0.3 + Math.sin(time.value * 0.003 + index) * 0.2
      };
    };

    // 波纹样式计算
    const getRippleStyle = (index: number) => {
      const size = 100 + index * 60;
      const opacity = 0.15 - index * 0.02;
      const delay = index * 0.3;
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        animationDelay: `${delay}s`,
        opacity: opacity,
        borderColor: `rgba(64, 158, 255, ${opacity})`
      };
    };

    // 音乐波形样式计算
    const getWaveStyle = (index: number) => {
      const height = 20 + Math.sin(time.value * 0.005 + index * 0.5) * 15;
      const delay = index * 0.1;
      
      return {
        height: `${height}px`,
        animationDelay: `${delay}s`,
        left: `${index * 8}%`
      };
    };

    // 动画循环
    const animate = () => {
      time.value = Date.now();
      animationFrame.value = requestAnimationFrame(animate);
    };

    onMounted(() => {
      animate();
    });

    onUnmounted(() => {
      if (animationFrame.value) {
        cancelAnimationFrame(animationFrame.value);
      }
    });

    return {
      particleCount,
      rippleCount,
      waveCount,
      getParticleStyle,
      getRippleStyle,
      getWaveStyle
    };
  }
});
</script>

<style lang="scss" scoped>
.background-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

// 流体背景渐变球
.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.6;
  animation: float 8s ease-in-out infinite;

  &.orb-1 {
    width: 200px;
    height: 200px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    top: 20%;
    left: 10%;
    animation-delay: 0s;
  }

  &.orb-2 {
    width: 150px;
    height: 150px;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    top: 60%;
    right: 15%;
    animation-delay: -2s;
  }

  &.orb-3 {
    width: 120px;
    height: 120px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    bottom: 30%;
    left: 20%;
    animation-delay: -4s;
  }
}

// 粒子效果
.particles {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 1px;
  height: 1px;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: radial-gradient(circle, #409eff, transparent);
  border-radius: 50%;
  animation: twinkle 3s ease-in-out infinite;
}

// 波纹效果
.ripple-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.ripple {
  position: absolute;
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: ripple-expand 4s ease-out infinite;
}

// 音乐波形效果
.music-waves {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: flex-end;
  gap: 2px;
  width: 120px;
}

.wave-bar {
  width: 3px;
  background: linear-gradient(to top, #409eff, #67c23a);
  border-radius: 2px;
  animation: wave-bounce 1.5s ease-in-out infinite;
  opacity: 0.7;
}

// 动画定义
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(90deg);
  }
  50% {
    transform: translateY(-10px) rotate(180deg);
  }
  75% {
    transform: translateY(-30px) rotate(270deg);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.5);
  }
}

@keyframes ripple-expand {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 0.8;
  }
  100% {
    transform: translate(-50%, -50%) scale(4);
    opacity: 0;
  }
}

@keyframes wave-bounce {
  0%, 100% {
    transform: scaleY(0.5);
  }
  50% {
    transform: scaleY(1.5);
  }
}

// 响应式优化
@media (max-width: 768px) {
  .gradient-orb {
    filter: blur(20px);
    
    &.orb-1 {
      width: 120px;
      height: 120px;
    }
    
    &.orb-2 {
      width: 100px;
      height: 100px;
    }
    
    &.orb-3 {
      width: 80px;
      height: 80px;
    }
  }
  
  .particles {
    display: none; // 移动端隐藏粒子效果以提升性能
  }
}

// 减少动画偏好设置
@media (prefers-reduced-motion: reduce) {
  .gradient-orb,
  .particle,
  .ripple,
  .wave-bar {
    animation: none;
  }
}
</style>
