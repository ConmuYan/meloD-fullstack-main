<template>
  <!-- Hero Section with Modern Design -->
  <div class="hero-section">
    <div class="hero-background">
      <div class="gradient-overlay"></div>
      <div class="floating-elements">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
        <!-- 添加更多微妙的装饰元素 -->
        <div class="floating-dot dot-1"></div>
        <div class="floating-dot dot-2"></div>
        <div class="floating-dot dot-3"></div>
      </div>
    </div>
    <div class="hero-content">
      <h1 class="hero-title">
        <span class="title-line">发现你的</span>
        <span class="title-line highlight">音乐世界</span>
      </h1>
      <p class="hero-subtitle">探索无限音乐可能，让每一首歌都成为你的专属回忆</p>
      <div class="hero-actions">
        <!-- 只保留开始探索按钮 -->
        <button class="cta-button primary" @click="exploreMusic">
          <span>开始探索</span>
          <div class="button-glow"></div>
        </button>
      </div>
    </div>
  </div>

  <!-- Enhanced Carousel -->
  <div class="featured-section" v-if="swiperList.length">
    <h2 class="section-title">精选推荐</h2>
    <el-carousel class="modern-carousel" type="card" height="22vw" :interval="5000" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in swiperList" :key="index" class="carousel-item">
        <div class="carousel-content">
          <img :src="HttpManager.attachImageUrl(item.pic)" class="carousel-image" />
          <div class="carousel-overlay">
            <div class="overlay-content">
              <h3>{{ item.title || '精选内容' }}</h3>
              <p>{{ item.description || '发现更多精彩音乐' }}</p>
              <button class="overlay-button">立即收听</button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>

  <!-- Enhanced Sections -->
  <div class="content-sections">
    <enhanced-play-list 
      class="section-container" 
      title="热门歌单" 
      subtitle="精心挑选的音乐合集" 
      path="song-sheet-detail" 
      :playList="songList"
      :maxDisplay="8"
    ></enhanced-play-list>
    
    <enhanced-play-list 
      class="section-container" 
      title="推荐歌手" 
      subtitle="才华横溢的音乐人" 
      path="singer-detail" 
      :playList="singerList"
      :maxDisplay="8"
    ></enhanced-play-list>
  </div>

  <!-- Stats Section -->
  <div class="stats-section">
    <div class="stats-container">
      <div class="stat-item" v-for="(stat, index) in stats" :key="index">
        <div class="stat-number">{{ stat.number }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

import EnhancedPlayList from "@/components/EnhancedPlayList.vue";
import { NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const router = useRouter();
const songList = ref([]);
const singerList = ref([]);
const swiperList = ref([]);
const { changeIndex } = mixin();

// 统计数据
const stats = ref([
  { number: "10K+", label: "精选歌曲" },
  { number: "500+", label: "优质歌单" },
  { number: "200+", label: "知名歌手" },
  { number: "50K+", label: "活跃用户" }
]);

// 修复导航栏状态的探索音乐方法
const exploreMusic = () => {
  changeIndex(NavName.SongSheet); // 设置导航栏状态为歌单
  router.push('/song-sheet');
};

// 数据加载 - 保持原有的业务逻辑不变
try {
  HttpManager.getBannerList().then((res) => {
    swiperList.value = (res as ResponseBody).data.sort();
  });

  HttpManager.getSongList().then((res) => {
    songList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  HttpManager.getAllSinger().then((res) => {
    singerList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  onMounted(() => {
    changeIndex(NavName.Home);
    
    // 添加滚动动画观察器
    const observerOptions = {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-in');
        }
      });
    }, observerOptions);
    
    // 观察所有需要动画的元素
    setTimeout(() => {
      document.querySelectorAll('.section-container, .stats-section').forEach(el => {
        observer.observe(el);
      });
    }, 100);
  });
} catch (error) {
  console.error(error);
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

// 高级黑白灰风格的Hero Section
.hero-section {
  position: relative;
  height: 75vh;
  min-height: 550px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  
  .hero-background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 50%, #404040 100%);
    
    .gradient-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(0,0,0,0.3), transparent);
    }
    
    .floating-elements {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      
      .floating-circle {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        animation: float 8s ease-in-out infinite;
        
        &.circle-1 {
          width: 200px;
          height: 200px;
          top: 10%;
          left: 10%;
          animation-delay: 0s;
        }
        
        &.circle-2 {
          width: 150px;
          height: 150px;
          top: 60%;
          right: 15%;
          animation-delay: 2s;
        }
        
        &.circle-3 {
          width: 100px;
          height: 100px;
          bottom: 20%;
          left: 60%;
          animation-delay: 4s;
        }
      }
      
      // 添加微妙的装饰点
      .floating-dot {
        position: absolute;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.4);
        animation: twinkle 4s ease-in-out infinite;
        
        &.dot-1 {
          top: 25%;
          left: 20%;
          animation-delay: 1s;
        }
        
        &.dot-2 {
          top: 70%;
          right: 30%;
          animation-delay: 2.5s;
        }
        
        &.dot-3 {
          bottom: 30%;
          left: 80%;
          animation-delay: 4s;
        }
      }
    }
  }
  
  .hero-content {
    text-align: center;
    color: white;
    z-index: 2;
    max-width: 800px;
    padding: 0 2rem;
    
    .hero-title {
      font-size: clamp(2.5rem, 8vw, 4.5rem);
      font-weight: 700;
      margin-bottom: 1.5rem;
      line-height: 1.2;
      
      .title-line {
        display: block;
        opacity: 0;
        transform: translateY(30px);
        animation: slideInUp 1s ease forwards;
        
        &:nth-child(2) {
          animation-delay: 0.3s;
        }
        
        &.highlight {
          background: linear-gradient(45deg, #ffffff, #e0e0e0);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }
    }
    
    .hero-subtitle {
      font-size: 1.25rem;
      margin-bottom: 3rem;
      opacity: 0;
      color: #b0b0b0;
      animation: slideInUp 1s ease 0.6s forwards;
    }
    
    .hero-actions {
      display: flex;
      justify-content: center;
      opacity: 0;
      animation: slideInUp 1s ease 0.9s forwards;
      
      .cta-button {
        position: relative;
        padding: 1rem 2.5rem;
        border: none;
        border-radius: 8px;
        font-size: 1.1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        overflow: hidden;
        
        &.primary {
          background: rgba(255, 255, 255, 0.1);
          color: white;
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);
          
          .button-glow {
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
            transition: left 0.6s ease;
          }
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 255, 255, 0.1);
            background: rgba(255, 255, 255, 0.15);
            
            .button-glow {
              left: 100%;
            }
          }
        }
      }
    }
  }
}

// 高级黑白灰风格的Featured Section
.featured-section {
  padding: 4rem 2rem;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  
  .section-title {
    text-align: center;
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 3rem;
    color: #2c2c2c;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -10px;
      left: 50%;
      transform: translateX(-50%);
      width: 80px;
      height: 4px;
      background: linear-gradient(90deg, #666666, #999999);
      border-radius: 2px;
    }
  }
  
  .modern-carousel {
    max-width: 1200px;
    margin: 0 auto;
    
    .carousel-item {
      border-radius: 12px;
      overflow: hidden;
      
      .carousel-content {
        position: relative;
        height: 100%;
        
        .carousel-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s ease;
          filter: grayscale(20%);
        }
        
        .carousel-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: linear-gradient(45deg, rgba(0,0,0,0.8), rgba(0,0,0,0.4));
          display: flex;
          align-items: flex-end;
          padding: 2rem;
          opacity: 0;
          transition: opacity 0.3s ease;
          
          .overlay-content {
            color: white;
            
            h3 {
              font-size: 1.5rem;
              margin-bottom: 0.5rem;
              font-weight: 600;
            }
            
            p {
              margin-bottom: 1rem;
              opacity: 0.9;
              color: #e0e0e0;
            }
            
            .overlay-button {
              padding: 0.75rem 1.5rem;
              background: rgba(255, 255, 255, 0.1);
              border: 1px solid rgba(255, 255, 255, 0.3);
              color: white;
              border-radius: 6px;
              cursor: pointer;
              backdrop-filter: blur(10px);
              transition: all 0.3s ease;
              
              &:hover {
                background: rgba(255, 255, 255, 0.2);
                transform: translateY(-2px);
              }
            }
          }
        }
        
        &:hover {
          .carousel-image {
            transform: scale(1.03);
            filter: grayscale(0%);
          }
          
          .carousel-overlay {
            opacity: 1;
          }
        }
      }
    }
  }
}

// 高级黑白灰风格的Content Sections
.content-sections {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  background: #fafafa;
  
  .section-container {
    margin-bottom: 4rem;
    opacity: 0;
    transform: translateY(40px);
    transition: all 0.8s ease;
    background: white;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    
    &.animate-in {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

// 修复并美化Stats Section
.stats-section {
  padding: 5rem 2rem;
  background: linear-gradient(135deg, #2c2c2c 0%, #1a1a1a 100%);
  color: white;
  opacity: 0;
  transform: translateY(50px);
  transition: all 0.8s ease;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.02"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
    pointer-events: none;
  }
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 3rem;
    max-width: 1000px;
    margin: 0 auto;
    position: relative;
    z-index: 2;
    
    .stat-item {
      text-align: center;
      padding: 2rem 1rem;
      border-radius: 8px;
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.1);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-5px);
        background: rgba(255, 255, 255, 0.08);
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
      }
      
      .stat-number {
        font-size: 3rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
        background: linear-gradient(45deg, #ffffff, #e0e0e0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        animation: numberGlow 2s ease-in-out infinite alternate;
      }
      
      .stat-label {
        font-size: 1.1rem;
        opacity: 0.9;
        color: #b0b0b0;
        font-weight: 500;
      }
    }
  }
}

// 动画定义
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

@keyframes numberGlow {
  0% {
    text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
  }
  100% {
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.6);
  }
}

// 响应式设计
@media (max-width: $sm) {
  .hero-section {
    height: 65vh;
    min-height: 450px;
    
    .hero-content {
      .hero-actions {
        .cta-button {
          width: 100%;
          max-width: 300px;
        }
      }
    }
  }
  
  .featured-section {
    padding: 3rem 1rem;
    
    .modern-carousel {
      height: 35vw !important;
    }
  }
  
  .content-sections {
    padding: 1.5rem 1rem;
    
    .section-container {
      margin-bottom: 3rem;
      padding: 1.5rem;
    }
  }
  
  .stats-section {
    padding: 3rem 1rem;
    
    .stats-container {
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      
      .stat-item {
        padding: 1.5rem 1rem;
        
        .stat-number {
          font-size: 2rem;
        }
      }
    }
  }
}
</style>
