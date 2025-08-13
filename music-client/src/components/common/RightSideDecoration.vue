<template>
  <div class="right-side-decoration">
    <!-- 浮动音符装饰 -->
    <div class="floating-notes">
      <div class="note note-1">♪</div>
      <div class="note note-2">♫</div>
      <div class="note note-3">♪</div>
      <div class="note note-4">♬</div>
      <div class="note note-5">♩</div>
    </div>
    
    <!-- 渐变光圈 -->
    <div class="gradient-circles">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <!-- 流动线条 -->
    <div class="flowing-lines">
      <div class="line line-1"></div>
      <div class="line line-2"></div>
      <div class="line line-3"></div>
    </div>
    
    <!-- 音波效果 -->
    <div class="sound-waves">
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
      <div class="wave wave-3"></div>
      <div class="wave wave-4"></div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'RightSideDecoration',
});
</script>

<style lang="scss" scoped>
.right-side-decoration {
  position: fixed;
  right: 0;
  top: 0;
  width: 40vw;
  height: 100vh;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

// 浮动音符动画
.floating-notes {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .note {
    position: absolute;
    font-size: 24px;
    color: rgba(64, 158, 255, 0.3);
    animation: floatNote 8s ease-in-out infinite;
    opacity: 0;
  }
  
  .note-1 {
    right: 15%;
    top: 20%;
    animation-delay: 0s;
    font-size: 28px;
  }
  
  .note-2 {
    right: 25%;
    top: 45%;
    animation-delay: 2s;
    font-size: 20px;
  }
  
  .note-3 {
    right: 35%;
    top: 70%;
    animation-delay: 4s;
    font-size: 24px;
  }
  
  .note-4 {
    right: 10%;
    top: 60%;
    animation-delay: 6s;
    font-size: 22px;
  }
  
  .note-5 {
    right: 30%;
    top: 30%;
    animation-delay: 1s;
    font-size: 26px;
  }
}

@keyframes floatNote {
  0%, 100% {
    opacity: 0;
    transform: translateY(0) rotate(0deg);
  }
  20% {
    opacity: 0.6;
  }
  80% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.8;
    transform: translateY(-20px) rotate(10deg);
  }
}

// 渐变光圈
.gradient-circles {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
    animation: pulseCircle 6s ease-in-out infinite;
  }
  
  .circle-1 {
    width: 200px;
    height: 200px;
    right: 10%;
    top: 15%;
    animation-delay: 0s;
  }
  
  .circle-2 {
    width: 150px;
    height: 150px;
    right: 30%;
    top: 55%;
    animation-delay: 2s;
  }
  
  .circle-3 {
    width: 120px;
    height: 120px;
    right: 15%;
    top: 75%;
    animation-delay: 4s;
  }
}

@keyframes pulseCircle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.2);
  }
}

// 流动线条
.flowing-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .line {
    position: absolute;
    height: 2px;
    background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.4), transparent);
    animation: flowLine 4s ease-in-out infinite;
  }
  
  .line-1 {
    width: 200px;
    right: 20%;
    top: 25%;
    animation-delay: 0s;
  }
  
  .line-2 {
    width: 150px;
    right: 25%;
    top: 50%;
    animation-delay: 1.5s;
  }
  
  .line-3 {
    width: 180px;
    right: 15%;
    top: 75%;
    animation-delay: 3s;
  }
}

@keyframes flowLine {
  0%, 100% {
    opacity: 0;
    transform: translateX(-100px);
  }
  50% {
    opacity: 0.8;
    transform: translateX(0);
  }
}

// 音波效果
.sound-waves {
  position: absolute;
  right: 5%;
  top: 50%;
  transform: translateY(-50%);
  
  .wave {
    width: 4px;
    background: rgba(64, 158, 255, 0.4);
    margin: 0 3px;
    border-radius: 2px;
    display: inline-block;
    animation: soundWave 1.5s ease-in-out infinite;
  }
  
  .wave-1 {
    height: 30px;
    animation-delay: 0s;
  }
  
  .wave-2 {
    height: 50px;
    animation-delay: 0.2s;
  }
  
  .wave-3 {
    height: 40px;
    animation-delay: 0.4s;
  }
  
  .wave-4 {
    height: 25px;
    animation-delay: 0.6s;
  }
}

@keyframes soundWave {
  0%, 100% {
    transform: scaleY(0.3);
    opacity: 0.4;
  }
  50% {
    transform: scaleY(1);
    opacity: 0.8;
  }
}

// 响应式适配
@media screen and (max-width: 1200px) {
  .right-side-decoration {
    width: 35vw;
  }
  
  .floating-notes .note {
    font-size: 20px;
  }
  
  .gradient-circles .circle {
    &.circle-1 { width: 150px; height: 150px; }
    &.circle-2 { width: 120px; height: 120px; }
    &.circle-3 { width: 100px; height: 100px; }
  }
}

@media screen and (max-width: 768px) {
  .right-side-decoration {
    display: none; // 移动端隐藏装饰
  }
}
</style>
