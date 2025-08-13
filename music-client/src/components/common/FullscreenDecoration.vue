<template>
  <div class="fullscreen-decoration">
    <!-- 右侧装饰区域 - 所有动效集中于此 -->
    <div class="right-decoration-zone">
      <!-- 动态渐变背景 -->
      <div class="gradient-orbs">
        <div class="gradient-orb orb-1"></div>
        <div class="gradient-orb orb-2"></div>
        <div class="gradient-orb orb-3"></div>
      </div>
      
      <!-- 黑胶唱片 -->
      <div class="vinyl-record">
        <div class="record-center"></div>
        <div class="record-groove groove-1"></div>
        <div class="record-groove groove-2"></div>
        <div class="record-groove groove-3"></div>
      </div>
      

      
      <!-- 浮动音符 -->
      <div class="floating-notes">
        <div class="music-note note-1">♪</div>
        <div class="music-note note-2">♫</div>
        <div class="music-note note-3">♪</div>
        <div class="music-note note-4">♬</div>
        <div class="music-note note-5">♩</div>
        <div class="music-note note-6">♫</div>
        <div class="music-note note-7">♪</div>
        <div class="music-note note-8">♬</div>
      </div>
      
      <!-- 脉冲光环 -->
      <div class="pulse-rings">
        <div class="ring ring-1"></div>
        <div class="ring ring-2"></div>
        <div class="ring ring-3"></div>
      </div>
      
      <!-- 音波可视化 -->
      <div class="sound-visualizer">
        <div class="wave-line" v-for="i in 15" :key="i" :style="{ animationDelay: i * 0.1 + 's' }"></div>
      </div>
      
      <!-- 几何图形 -->
      <div class="floating-shapes">
        <div class="shape hexagon-shape"></div>
        <div class="shape triangle-shape"></div>
        <div class="shape circle-shape"></div>
        <div class="shape square-shape"></div>
        <div class="shape diamond-shape"></div>
      </div>
      
      <!-- 流动粒子 -->
      <div class="particle-system">
        <div class="particle" v-for="i in 25" :key="i" 
             :style="{ 
               top: Math.random() * 100 + '%',
               animationDelay: Math.random() * 8 + 's',
               animationDuration: (4 + Math.random() * 3) + 's'
             }"></div>
      </div>
      
      <!-- 连接线条 -->
      <div class="flowing-lines">
        <div class="line line-1"></div>
        <div class="line line-2"></div>
        <div class="line line-3"></div>
        <div class="line line-4"></div>
      </div>
      
      <!-- 品牌Logo中心装饰 -->
      <div class="brand-logo-center">
        <div class="logo-container">
          <!-- <img src="@/assets/images/meloD.png" alt="meloD" class="logo-image" /> -->
        </div>
        <div class="logo-particles">
          <div class="logo-particle" v-for="i in 12" :key="i" 
               :style="{ 
                 animationDelay: i * 0.3 + 's',
                 transform: 'rotate(' + (i * 30) + 'deg)'
               }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'FullscreenDecoration',
});
</script>

<style lang="scss" scoped>
.fullscreen-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

// 右侧装饰区域 - 所有动效的容器
.right-decoration-zone {
  position: absolute;
  right: 0;
  top: 0;
  width: 45vw;
  height: 100vh;
  overflow: hidden;
}

// 动态渐变背景
.gradient-orbs {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(40px);
    opacity: 0.4;
    animation: floatOrb 12s ease-in-out infinite;
  }
  
  .orb-1 {
    width: 300px;
    height: 300px;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.5), rgba(147, 197, 253, 0.3));
    top: 10%;
    right: -50px;
    animation-delay: 0s;
  }
  
  .orb-2 {
    width: 200px;
    height: 200px;
    background: linear-gradient(225deg, rgba(139, 92, 246, 0.4), rgba(196, 181, 253, 0.3));
    bottom: 20%;
    right: 20%;
    animation-delay: 4s;
  }
  
  .orb-3 {
    width: 150px;
    height: 150px;
    background: linear-gradient(45deg, rgba(59, 130, 246, 0.4), rgba(147, 197, 253, 0.2));
    top: 60%;
    left: 10%;
    animation-delay: 8s;
  }
}

@keyframes floatOrb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(20px, -15px) scale(1.1); }
  50% { transform: translate(-15px, 20px) scale(0.9); }
  75% { transform: translate(15px, 15px) scale(1.05); }
}

// 黑胶唱片装饰
.vinyl-record {
  position: absolute;
  top: 15%;
  left: 20%;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2), rgba(64, 158, 255, 0.1));
  border-radius: 50%;
  animation: vinylSpin 25s linear infinite;
  
  .record-center {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 16px;
    height: 16px;
    background: rgba(64, 158, 255, 0.7);
    border-radius: 50%;
  }
  
  .record-groove {
    position: absolute;
    border: 1px solid rgba(64, 158, 255, 0.3);
    border-radius: 50%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    
    &.groove-1 {
      width: 50px;
      height: 50px;
    }
    
    &.groove-2 {
      width: 70px;
      height: 70px;
    }
    
    &.groove-3 {
      width: 90px;
      height: 90px;
    }
  }
}

// 音乐均衡器
.equalizer {
  position: absolute;
  bottom: 25%;
  left: 15%;
  display: flex;
  gap: 3px;
  align-items: flex-end;
  
  .eq-bar {
    width: 5px;
    background: linear-gradient(to top, rgba(64, 158, 255, 0.8), rgba(64, 158, 255, 0.4));
    border-radius: 2px;
    animation: equalizerBounce 1.2s ease-in-out infinite;
  }
}

@keyframes vinylSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes equalizerBounce {
  0%, 100% { height: 15px; }
  50% { height: 35px; }
}

// 浮动音符
.floating-notes {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .music-note {
    position: absolute;
    font-size: 1.5rem;
    color: rgba(64, 158, 255, 0.7);
    animation: noteFloat 6s ease-in-out infinite;
    
    &.note-1 { left: 10%; top: 8%; animation-delay: 0s; }
    &.note-2 { right: 15%; top: 20%; animation-delay: 1s; }
    &.note-3 { left: 25%; top: 35%; animation-delay: 2s; }
    &.note-4 { right: 25%; top: 50%; animation-delay: 3s; }
    &.note-5 { left: 5%; bottom: 40%; animation-delay: 4s; }
    &.note-6 { right: 10%; bottom: 25%; animation-delay: 5s; }
    &.note-7 { left: 35%; bottom: 10%; animation-delay: 6s; }
    &.note-8 { right: 30%; bottom: 5%; animation-delay: 7s; }
  }
}

// 脉冲光环
.pulse-rings {
  position: absolute;
  top: 45%;
  right: 25%;
  
  .ring {
    position: absolute;
    border: 1px solid rgba(64, 158, 255, 0.4);
    border-radius: 50%;
    animation: ringPulse 5s ease-in-out infinite;
    
    &.ring-1 {
      width: 80px;
      height: 80px;
      margin: -40px 0 0 -40px;
      animation-delay: 0s;
    }
    
    &.ring-2 {
      width: 120px;
      height: 120px;
      margin: -60px 0 0 -60px;
      animation-delay: 1.5s;
    }
    
    &.ring-3 {
      width: 160px;
      height: 160px;
      margin: -80px 0 0 -80px;
      animation-delay: 3s;
    }
  }
}

@keyframes noteFloat {
  0%, 100% { 
    opacity: 0.5;
    transform: translateY(0) rotate(0deg);
  }
  50% { 
    opacity: 0.9;
    transform: translateY(-20px) rotate(10deg);
  }
}

@keyframes ringPulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(0.9);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

// 音波可视化
.sound-visualizer {
  position: absolute;
  top: 35%;
  left: 8%;
  display: flex;
  flex-direction: column;
  gap: 2px;
  
  .wave-line {
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.7), transparent);
    border-radius: 1px;
    animation: waveFlow 2.5s ease-in-out infinite;
  }
}

// 几何图形
.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .shape {
    position: absolute;
    animation: shapeFloat 8s ease-in-out infinite;
    
    &.hexagon-shape {
      width: 16px;
      height: 14px;
      background: rgba(64, 158, 255, 0.5);
      top: 25%;
      right: 15%;
      animation-delay: 0s;
      
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
        border-bottom: 4px solid rgba(64, 158, 255, 0.5);
      }
      
      &::after {
        top: 100%;
        border-top: 4px solid rgba(64, 158, 255, 0.5);
      }
    }
    
    &.triangle-shape {
      width: 0;
      height: 0;
      border-left: 8px solid transparent;
      border-right: 8px solid transparent;
      border-bottom: 14px solid rgba(64, 158, 255, 0.5);
      top: 60%;
      left: 30%;
      animation-delay: 2s;
    }
    
    &.circle-shape {
      width: 12px;
      height: 12px;
      background: rgba(64, 158, 255, 0.5);
      border-radius: 50%;
      bottom: 30%;
      right: 20%;
      animation-delay: 4s;
    }
    
    &.square-shape {
      width: 10px;
      height: 10px;
      background: rgba(64, 158, 255, 0.5);
      transform: rotate(45deg);
      bottom: 15%;
      left: 40%;
      animation-delay: 1s;
    }
    
    &.diamond-shape {
      width: 8px;
      height: 8px;
      background: rgba(64, 158, 255, 0.5);
      transform: rotate(45deg);
      top: 10%;
      left: 45%;
      animation-delay: 6s;
    }
  }
}

@keyframes waveFlow {
  0%, 100% {
    width: 40px;
    opacity: 0.4;
  }
  50% {
    width: 80px;
    opacity: 0.8;
  }
}

@keyframes shapeFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-15px) rotate(180deg);
    opacity: 1;
  }
}

// 流动粒子系统
.particle-system {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .particle {
    position: absolute;
    right: 0;
    width: 3px;
    height: 3px;
    background: rgba(64, 158, 255, 0.6);
    border-radius: 50%;
    animation: particleFloat 6s linear infinite;
  }
}

@keyframes particleFloat {
  0% {
    opacity: 0;
    transform: translateY(100vh) translateX(0);
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    opacity: 0;
    transform: translateY(-100px) translateX(-50px);
  }
}

// 流动线条
.flowing-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .line {
    position: absolute;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.5), transparent);
    animation: lineFlow 4s ease-in-out infinite;
  }
  
  .line-1 {
    width: 60px;
    top: 20%;
    right: 5%;
    animation-delay: 0s;
  }
  
  .line-2 {
    width: 80px;
    top: 40%;
    right: 8%;
    animation-delay: 1s;
  }
  
  .line-3 {
    width: 50px;
    top: 60%;
    right: 12%;
    animation-delay: 2s;
  }
  
  .line-4 {
    width: 70px;
    top: 80%;
    right: 6%;
    animation-delay: 3s;
  }
}

@keyframes lineFlow {
  0%, 100% {
    opacity: 0.3;
    transform: translateX(-30px);
  }
  50% {
    opacity: 0.8;
    transform: translateX(0);
  }
}

// 品牌Logo中心装饰
.brand-logo-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(calc(-50% + 70px), calc(-50% + 10px));
  z-index: 10;
  
  .logo-container {
    position: relative;
    text-align: center;
    // animation: logoBreath 4s ease-in-out infinite;
  }
  
//   .logo-glow-ring {
//     position: absolute;
//     top: 50%;
//     left: 50%;
//     transform: translate(-50%, -50%);
//     width: 120px;
//     height: 120px;
//     border: 2px solid transparent;
//     border-radius: 50%;
//     background: linear-gradient(45deg, rgba(64, 158, 255, 0.8), rgba(147, 197, 253, 0.6), rgba(139, 92, 246, 0.7)) border-box;
//     background-clip: padding-box;
//     animation: logoGlowRotate 8s linear infinite;
    
//     &::before {
//       content: '';
//       position: absolute;
//       top: -2px;
//       left: -2px;
//       right: -2px;
//       bottom: -2px;
//       border-radius: 50%;
//       background: linear-gradient(45deg, rgba(64, 158, 255, 0.4), rgba(147, 197, 253, 0.3), rgba(139, 92, 246, 0.4));
//       z-index: -1;
//       filter: blur(8px);
//       animation: logoGlowPulse 3s ease-in-out infinite;
//     }
//   }
  
  .logo-image {
    width: 300px;
    height: 300px;
    object-fit: contain;
    // filter: drop-shadow(0 0 20px rgba(64, 158, 255, 0.6)) 
    //         drop-shadow(0 0 40px rgba(147, 197, 253, 0.4))
    //         drop-shadow(0 0 60px rgba(139, 92, 246, 0.3));
    // animation: logoImageGlow 4s ease-in-out infinite;
    // transition: all 0.3s ease;
    
    // &:hover {
    //   transform: scale(1.1);
    //   filter: drop-shadow(0 0 25px rgba(64, 158, 255, 0.8)) 
    //           drop-shadow(0 0 50px rgba(147, 197, 253, 0.6))
    //           drop-shadow(0 0 75px rgba(139, 92, 246, 0.5));
    // }
  }
  
  .logo-subtitle {
    font-size: 0.9rem;
    color: rgba(64, 158, 255, 0.8);
    opacity: 0.9;
    font-weight: 400;
    letter-spacing: 1px;
    animation: logoSubtitleFloat 5s ease-in-out infinite;
  }
  
  .logo-particles {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 200px;
    height: 200px;
    
    .logo-particle {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 4px;
      height: 4px;
      background: rgba(64, 158, 255, 0.6);
      border-radius: 50%;
      transform-origin: 0 80px;
      animation: logoParticleOrbit 12s linear infinite;
      
      &::after {
        content: '';
        position: absolute;
        top: -2px;
        left: -2px;
        width: 8px;
        height: 8px;
        background: rgba(64, 158, 255, 0.3);
        border-radius: 50%;
        filter: blur(2px);
      }
    }
  }
}

// Logo动画关键帧
@keyframes logoBreath {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
  }
}

@keyframes logoGlowRotate {
  0% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  100% {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes logoGlowPulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

@keyframes logoImageGlow {
  0%, 100% {
    filter: drop-shadow(0 0 20px rgba(64, 158, 255, 0.6)) 
            drop-shadow(0 0 40px rgba(147, 197, 253, 0.4))
            drop-shadow(0 0 60px rgba(139, 92, 246, 0.3));
    transform: scale(1);
  }
  50% {
    filter: drop-shadow(0 0 30px rgba(64, 158, 255, 0.8)) 
            drop-shadow(0 0 60px rgba(147, 197, 253, 0.6))
            drop-shadow(0 0 90px rgba(139, 92, 246, 0.5));
    transform: scale(1.05);
  }
}

@keyframes logoSubtitleFloat {
  0%, 100% {
    opacity: 0.8;
    transform: translateY(0);
  }
  50% {
    opacity: 1;
    transform: translateY(-3px);
  }
}

@keyframes logoParticleOrbit {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
    transform: rotate(360deg);
  }
}

// 响应式适配
@media screen and (max-width: 1200px) {
  .right-decoration-zone {
    width: 40vw;
  }
  
  .vinyl-record {
    width: 80px;
    height: 80px;
    
    .record-center {
      width: 12px;
      height: 12px;
    }
    
    .record-groove {
      &.groove-1 { width: 40px; height: 40px; }
      &.groove-2 { width: 60px; height: 60px; }
      &.groove-3 { width: 75px; height: 75px; }
    }
  }
  
  .floating-notes .music-note {
    font-size: 1.2rem;
  }
  
  .pulse-rings .ring {
    &.ring-1 { width: 60px; height: 60px; margin: -30px 0 0 -30px; }
    &.ring-2 { width: 90px; height: 90px; margin: -45px 0 0 -45px; }
    &.ring-3 { width: 120px; height: 120px; margin: -60px 0 0 -60px; }
  }
  
  .brand-logo-center {
    .logo-glow-ring {
      width: 100px;
      height: 100px;
    }
    
    .logo-image {
      width: 60px;
      height: 60px;
    }
    
    .logo-subtitle {
      font-size: 0.8rem;
    }
    
    .logo-particles {
      width: 160px;
      height: 160px;
      
      .logo-particle {
        transform-origin: 0 60px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .fullscreen-decoration {
    display: none;
  }
}
</style>
