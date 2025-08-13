<template>
  <div class="left-side-decoration">
    <!-- 音乐频谱装饰 -->
    <div class="spectrum-bars">
      <div class="bar bar-1"></div>
      <div class="bar bar-2"></div>
      <div class="bar bar-3"></div>
      <div class="bar bar-4"></div>
      <div class="bar bar-5"></div>
      <div class="bar bar-6"></div>
    </div>
    
    <!-- 轻柔粒子效果 -->
    <div class="floating-particles">
      <div class="particle particle-1"></div>
      <div class="particle particle-2"></div>
      <div class="particle particle-3"></div>
      <div class="particle particle-4"></div>
      <div class="particle particle-5"></div>
    </div>
    
    <!-- 优雅的几何图形 -->
    <div class="geometric-shapes">
      <div class="shape triangle"></div>
      <div class="shape hexagon"></div>
      <div class="shape diamond"></div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'LeftSideDecoration',
});
</script>

<style lang="scss" scoped>
.left-side-decoration {
  position: fixed;
  left: 0;
  top: 0;
  width: 25vw;
  height: 100vh;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

// 音乐频谱装饰
.spectrum-bars {
  position: absolute;
  left: 8%;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: flex-end;
  gap: 4px;
  
  .bar {
    width: 6px;
    background: linear-gradient(to top, rgba(64, 158, 255, 0.6), rgba(64, 158, 255, 0.2));
    border-radius: 3px;
    animation: spectrumPulse 2s ease-in-out infinite;
  }
  
  .bar-1 {
    height: 40px;
    animation-delay: 0s;
  }
  
  .bar-2 {
    height: 60px;
    animation-delay: 0.2s;
  }
  
  .bar-3 {
    height: 35px;
    animation-delay: 0.4s;
  }
  
  .bar-4 {
    height: 50px;
    animation-delay: 0.6s;
  }
  
  .bar-5 {
    height: 45px;
    animation-delay: 0.8s;
  }
  
  .bar-6 {
    height: 30px;
    animation-delay: 1s;
  }
}

@keyframes spectrumPulse {
  0%, 100% {
    transform: scaleY(0.3);
    opacity: 0.4;
  }
  50% {
    transform: scaleY(1);
    opacity: 0.8;
  }
}

// 轻柔粒子效果
.floating-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .particle {
    position: absolute;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(64, 158, 255, 0.3);
    animation: floatParticle 10s ease-in-out infinite;
  }
  
  .particle-1 {
    left: 15%;
    top: 20%;
    animation-delay: 0s;
  }
  
  .particle-2 {
    left: 10%;
    top: 45%;
    animation-delay: 2s;
    width: 6px;
    height: 6px;
  }
  
  .particle-3 {
    left: 20%;
    top: 70%;
    animation-delay: 4s;
    width: 10px;
    height: 10px;
  }
  
  .particle-4 {
    left: 5%;
    top: 60%;
    animation-delay: 6s;
    width: 7px;
    height: 7px;
  }
  
  .particle-5 {
    left: 18%;
    top: 35%;
    animation-delay: 8s;
    width: 9px;
    height: 9px;
  }
}

@keyframes floatParticle {
  0%, 100% {
    opacity: 0.3;
    transform: translateY(0) translateX(0);
  }
  25% {
    opacity: 0.7;
    transform: translateY(-15px) translateX(10px);
  }
  50% {
    opacity: 0.5;
    transform: translateY(-8px) translateX(-5px);
  }
  75% {
    opacity: 0.8;
    transform: translateY(-20px) translateX(8px);
  }
}

// 优雅的几何图形
.geometric-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .shape {
    position: absolute;
    animation: rotateShape 15s linear infinite;
  }
  
  .triangle {
    left: 12%;
    top: 25%;
    width: 0;
    height: 0;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-bottom: 17px solid rgba(64, 158, 255, 0.2);
    animation-delay: 0s;
  }
  
  .hexagon {
    left: 8%;
    top: 55%;
    width: 16px;
    height: 14px;
    background: rgba(64, 158, 255, 0.2);
    position: relative;
    animation-delay: 5s;
    
    &::before,
    &::after {
      content: '';
      position: absolute;
      width: 0;
      border-left: 8px solid transparent;
      border-right: 8px solid transparent;
    }
    
    &::before {
      bottom: 100%;
      border-bottom: 4px solid rgba(64, 158, 255, 0.2);
    }
    
    &::after {
      top: 100%;
      border-top: 4px solid rgba(64, 158, 255, 0.2);
    }
  }
  
  .diamond {
    left: 15%;
    top: 75%;
    width: 12px;
    height: 12px;
    background: rgba(64, 158, 255, 0.2);
    transform: rotate(45deg);
    animation-delay: 10s;
  }
}

@keyframes rotateShape {
  0% {
    transform: rotate(0deg);
    opacity: 0.3;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    transform: rotate(360deg);
    opacity: 0.3;
  }
}

// 响应式适配
@media screen and (max-width: 1200px) {
  .left-side-decoration {
    width: 20vw;
  }
  
  .spectrum-bars {
    left: 5%;
    
    .bar {
      width: 5px;
    }
  }
  
  .floating-particles .particle {
    width: 6px;
    height: 6px;
  }
}

@media screen and (max-width: 768px) {
  .left-side-decoration {
    display: none; // 移动端隐藏装饰
  }
}
</style>
