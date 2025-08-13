<template>
  <div v-if="showMonitor" class="performance-monitor">
    <div class="monitor-header">
      <span>性能监控</span>
      <button @click="toggleMonitor" class="close-btn">×</button>
    </div>
    <div class="monitor-content">
      <div class="metric">
        <span class="label">活跃动画:</span>
        <span class="value">{{ animationCount }}</span>
      </div>
      <div class="metric">
        <span class="label">FPS:</span>
        <span class="value" :class="fpsClass">{{ fps }}</span>
      </div>
      <div class="metric">
        <span class="label">内存使用:</span>
        <span class="value">{{ memoryUsage }}MB</span>
      </div>
      <div class="metric">
        <span class="label">减少动画:</span>
        <span class="value">{{ reducedMotion ? '是' : '否' }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue';
import { AnimationPerformanceMonitor, prefersReducedMotion } from '@/utils/animation';

export default defineComponent({
  name: 'PerformanceMonitor',
  props: {
    enabled: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const showMonitor = ref(props.enabled && process.env.NODE_ENV === 'development');
    const animationCount = ref(0);
    const fps = ref(60);
    const memoryUsage = ref(0);
    const reducedMotion = ref(prefersReducedMotion());
    
    let frameCount = 0;
    let lastTime = performance.now();
    let animationFrame: number | null = null;
    
    const monitor = AnimationPerformanceMonitor.getInstance();
    
    const fpsClass = computed(() => {
      if (fps.value >= 50) return 'good';
      if (fps.value >= 30) return 'warning';
      return 'poor';
    });
    
    const updateMetrics = () => {
      const currentTime = performance.now();
      frameCount++;
      
      if (currentTime - lastTime >= 1000) {
        fps.value = Math.round(frameCount * 1000 / (currentTime - lastTime));
        frameCount = 0;
        lastTime = currentTime;
        
        // 更新动画计数
        animationCount.value = monitor.getCurrentCount();
        
        // 更新内存使用（如果支持）
        if ('memory' in performance) {
          const memory = (performance as any).memory;
          memoryUsage.value = Math.round(memory.usedJSHeapSize / 1024 / 1024);
        }
      }
      
      if (showMonitor.value) {
        animationFrame = requestAnimationFrame(updateMetrics);
      }
    };
    
    const toggleMonitor = () => {
      showMonitor.value = !showMonitor.value;
      if (showMonitor.value) {
        startMonitoring();
      } else {
        stopMonitoring();
      }
    };
    
    const startMonitoring = () => {
      if (animationFrame) return;
      updateMetrics();
    };
    
    const stopMonitoring = () => {
      if (animationFrame) {
        cancelAnimationFrame(animationFrame);
        animationFrame = null;
      }
    };
    
    onMounted(() => {
      if (showMonitor.value) {
        startMonitoring();
      }
      
      // 监听键盘快捷键 Ctrl+Shift+P 开启/关闭监控
      const handleKeydown = (event: KeyboardEvent) => {
        if (event.ctrlKey && event.shiftKey && event.key === 'P') {
          event.preventDefault();
          toggleMonitor();
        }
      };
      
      window.addEventListener('keydown', handleKeydown);
      
      return () => {
        window.removeEventListener('keydown', handleKeydown);
      };
    });
    
    onUnmounted(() => {
      stopMonitoring();
    });
    
    return {
      showMonitor,
      animationCount,
      fps,
      memoryUsage,
      reducedMotion,
      fpsClass,
      toggleMonitor
    };
  }
});
</script>

<style lang="scss" scoped>
.performance-monitor {
  position: fixed;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.9);
  color: white;
  border-radius: 8px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  z-index: 9999;
  min-width: 200px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  
  .monitor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    
    span {
      font-weight: bold;
      color: #409eff;
    }
    
    .close-btn {
      background: none;
      border: none;
      color: white;
      cursor: pointer;
      font-size: 16px;
      padding: 0;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &:hover {
        color: #ff4757;
      }
    }
  }
  
  .monitor-content {
    .metric {
      display: flex;
      justify-content: space-between;
      margin-bottom: 4px;
      
      .label {
        color: #ccc;
      }
      
      .value {
        font-weight: bold;
        
        &.good {
          color: #2ed573;
        }
        
        &.warning {
          color: #ffa502;
        }
        
        &.poor {
          color: #ff4757;
        }
      }
    }
  }
}

// 移动端隐藏
@media (max-width: 768px) {
  .performance-monitor {
    display: none;
  }
}
</style>
