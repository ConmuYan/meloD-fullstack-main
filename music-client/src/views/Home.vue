<template>
  <!-- Hero Section -->
  <div class="hero-section">
    <div class="hero-background">
      <!-- 视频背景 -->
      <video
        :class="['hero-video', { 'loaded': videoLoaded }]"
        autoplay
        muted
        loop
        playsinline
        @loadstart="onVideoLoadStart"
        @canplay="onVideoCanPlay"
        @error="onVideoError"
      >
        <source :src="videoUrl" type="video/mp4">
        <!-- 如果视频加载失败，显示备用背景 -->
      </video>
      
      <!-- 视频遮罩层 -->
      <div class="video-overlay"></div>
      
      <!-- 原有的渐变遮罩层 -->
      <div class="gradient-overlay"></div>
      
      <!-- 浮动动效元素 - 已注释 -->
      <!-- <div class="floating-elements">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
        <div class="floating-dot dot-1"></div>
        <div class="floating-dot dot-2"></div>
        <div class="floating-dot dot-3"></div>
      </div> -->
    </div>
    <div class="hero-content">
      <h1 class="hero-title" style="opacity: 0.46;">
        <span class="title-line ">Hello meloD!</span>
      </h1>
      <p class="hero-subtitle" style="opacity: 0.6;">探索无限音乐可能，让每一首歌都成为你的专属回忆</p>
      <div class="hero-actions">
        <button class="cta-button primary" @click="exploreMusic">
          <span>开始探索</span>
          <div class="button-glow"></div>
        </button>
      </div>
    </div>
  </div>

  <!-- Stats Section - 移到精选推荐上方 -->
  <div class="stats-section">
    <div class="stats-container">
      <div class="stat-item" v-for="(stat, index) in stats" :key="index">
        <div class="stat-number" :ref="el => statRefs[index] = el">{{ stat.displayNumber }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>
  </div>

  <!-- 新的推荐主题轮播图 -->
  <div class="recommendation-section">
    <RecommendationCarousel />
  </div>

  <!-- Enhanced Carousel -->
  <div class="featured-section" v-if="swiperList.length">
    <h2 class="section-title">精选推荐</h2>
    <el-carousel class="modern-carousel" type="card" height="23vw" :interval="5000" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in swiperList" :key="index" class="carousel-item">
        <div class="carousel-content">
          <img :src="HttpManager.attachImageUrl(item.pic)" class="carousel-image" />
          <div class="carousel-overlay">
            <div class="overlay-content">
              <h3>{{ item.title || '精选内容' }}</h3>
              <p>{{ item.description || '发现更多精彩音乐' }}</p>
              <button class="overlay-button" @click="handleBannerClick(item)">立即收听</button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>

  <!-- 修复内容区域布局 -->
  <div class="content-sections">
    <div class="section-container">
      <enhanced-play-list 
        title="热门歌单" 
        subtitle="精心挑选的音乐合集" 
        path="song-sheet-detail" 
        :playList="songList"
        :maxDisplay="8"
      ></enhanced-play-list>
    </div>
    
    <div class="section-container">
      <enhanced-play-list 
        title="推荐歌手" 
        subtitle="才华横溢的音乐人" 
        path="singer-detail" 
        :playList="singerList"
        :maxDisplay="8"
      ></enhanced-play-list>
    </div>
  </div>


</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

import EnhancedPlayList from "@/components/EnhancedPlayList.vue";
import RecommendationCarousel from "@/components/RecommendationCarousel.vue";
import { NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const router = useRouter();
const store = useStore();
const songList = ref([]);
const singerList = ref([]);
const swiperList = ref([]);
const { changeIndex, routerManager } = mixin();

// 视频背景相关
const videoUrl = ref('/img/video/hero-background.mp4'); // 视频URL
const videoLoaded = ref(false);
const videoError = ref(false);

// 处理轮播图点击事件
const handleBannerClick = (banner: any) => {
  if (banner.category) {
    // 设置导航栏状态为歌单
    changeIndex(NavName.SongSheet);
    // 跳转到歌单列表页面，并传递分类参数
    router.push({
      path: '/song-sheet',
      query: { 
        category: banner.category
      }
    });
  }
};

// 统计数据
const stats = ref([
  { number: 0, displayNumber: "0", label: "精选歌曲", suffix: "+" },
  { number: 0, displayNumber: "0", label: "优质歌单", suffix: "+" },
  { number: 0, displayNumber: "0", label: "知名歌手", suffix: "+" },
  { number: 0, displayNumber: "0", label: "活跃用户", suffix: "+" }
]);

// 统计数字引用
const statRefs = ref([]);

// 数字翻滚动画函数
const animateNumber = (targetNumber: number, index: number, duration = 2000) => {
  const startNumber = 0;
  const startTime = Date.now();
  
  const updateNumber = () => {
    const currentTime = Date.now();
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    
    // 使用缓动函数
    const easeOutQuart = 1 - Math.pow(1 - progress, 4);
    const currentNumber = Math.floor(startNumber + (targetNumber - startNumber) * easeOutQuart);
    
    // 格式化数字显示
    let displayText = '';
    if (currentNumber >= 10000) {
      displayText = Math.floor(currentNumber / 1000) + 'K';
    } else if (currentNumber >= 1000) {
      displayText = (currentNumber / 1000).toFixed(1) + 'K';
    } else {
      displayText = currentNumber.toString();
    }
    
    stats.value[index].displayNumber = displayText + stats.value[index].suffix;
    
    if (progress < 1) {
      requestAnimationFrame(updateNumber);
    }
  };
  
  requestAnimationFrame(updateNumber);
};

// 获取统计数据
const getStatsData = async () => {
  try {
    // 并行获取所有统计数据
    const [songsRes, songListRes, singersRes, usersRes] = await Promise.all([
      HttpManager.getAllSongs(),
      HttpManager.getSongList(),
      HttpManager.getAllSinger(),
      HttpManager.getAllUser()
    ]);
    
    // 更新统计数据
    const songCount = (songsRes as ResponseBody).data?.length || 0;
    const songListCount = (songListRes as ResponseBody).data?.length || 0;
    const singerCount = (singersRes as ResponseBody).data?.length || 0;
    const userCount = (usersRes as ResponseBody).data?.length || 0;
    
    stats.value[0].number = songCount;
    stats.value[1].number = songListCount;
    stats.value[2].number = singerCount;
    stats.value[3].number = userCount;
    
    // 延迟启动动画，确保DOM已渲染
    setTimeout(() => {
      stats.value.forEach((stat, index) => {
        animateNumber(stat.number, index, 2000 + index * 200);
      });
    }, 500);
    
  } catch (error) {
    console.error('获取统计数据失败:', error);
    // 使用默认数据
    stats.value[0].displayNumber = "10K+";
    stats.value[1].displayNumber = "500+";
    stats.value[2].displayNumber = "200+";
    stats.value[3].displayNumber = "50K+";
  }
};

// 修复导航栏状态的探索音乐方法
const exploreMusic = () => {
  changeIndex(NavName.SongSheet);
  router.push('/song-sheet');
};

// 视频事件处理方法
const onVideoLoadStart = () => {
  console.log('视频开始加载');
};

const onVideoCanPlay = () => {
  videoLoaded.value = true;
  videoError.value = false;
  console.log('视频可以播放');
};

const onVideoError = (event: Event) => {
  videoError.value = true;
  videoLoaded.value = false;
  console.error('视频加载失败:', event);
  // 视频加载失败时，保持原有的渐变背景
};

// 数据加载 - 保持原有的业务逻辑不变
try {
  HttpManager.getActiveBannerList().then((res) => {
    if (res.success) {
      swiperList.value = res.data;
    }
  });

  HttpManager.getSongList().then((res) => {
    songList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  HttpManager.getAllSinger().then((res) => {
    singerList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  onMounted(() => {
    changeIndex(NavName.Home);
    
    // 获取统计数据
    getStatsData();
    
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
    
    const animatedElements = document.querySelectorAll('.section-container, .stats-section');
    animatedElements.forEach(el => observer.observe(el));
  });
} catch (error) {
  console.error('数据加载失败:', error);
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

// Hero Section样式
.hero-section {
  position: relative;
  height: 85vh;
  min-height: 400px;
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
    overflow: hidden;
    
    // 视频背景样式
    .hero-video {
      position: absolute;
      top: 50%;
      left: 50%;
      min-width: 100%;
      min-height: 100%;
      width: auto;
      height: auto;
      transform: translate(-50%, -50%);
      z-index: 1;
      object-fit: cover;
      opacity: 0;
      transition: opacity 1s ease-in-out;
      
      // 视频加载完成后显示
      &.loaded {
        opacity: 0.8;
      }
    }
    
    // 视频遮罩层 - 确保文字可读性
    .video-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, 
        rgba(0, 0, 0, 0.4) 0%, 
        rgba(0, 0, 0, 0.2) 50%, 
        rgba(0, 0, 0, 0.3) 100%
      );
      z-index: 2;
    }
    
    .gradient-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(0,0,0,0.2), transparent);
      z-index: 3;
    }
    
    // 浮动动效元素样式 - 已注释
    /* .floating-elements {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 4;
      
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
    } */
  }
  
  .hero-content {
    position: relative;
    z-index: 10;
    text-align: center;
    color: white;
    z-index: 5; // 确保内容在所有背景元素之上
    max-width: 800px;
    padding: 0 2rem;
    position: relative;
    
    .hero-title {
      font-size: 5rem;
      font-weight: 3000;
      margin-bottom: 0.5rem;
      line-height: 1.2;
      
      .title-line {
        display: block;
        
        &.highlight {
          background: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1);
          background-size: 200% 200%;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        
        &.glass-effect {
          position: relative;
          backdrop-filter: blur(10px);
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          border-radius: 12px;
          padding: 0.5rem 1rem;
          margin: 0.25rem 0;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
          
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
            border-radius: 12px;
            z-index: -1;
          }
        }
      }
    }
    
    .hero-subtitle {
      font-size: 1rem;
      margin-bottom: 0.8rem;
      opacity: 0.9;
      line-height: 1.6;
    }
    

    
    .hero-actions {
      .cta-button {
        position: relative;
        padding: 1rem 2.5rem;
        font-size: 1.1rem;
        border: none;
        border-radius: 50px;
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

// 轮播图样式
.featured-section {
  padding: 4rem 2rem;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  
  .section-title {
    text-align: center;
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 3rem;
    color: #2c3e50;
  }
  
  .modern-carousel {
    max-width: 1800px;
    margin: 0 auto;
    
    .carousel-item {
      .carousel-content {
        position: relative;
        height: 100%;
        border-radius: 12px;
        overflow: hidden;
        cursor: pointer;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
        }
        
        .carousel-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .carousel-overlay {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: linear-gradient(transparent, rgba(0,0,0,0.8));
          color: white;
          padding: 2rem;
          
          .overlay-content {
            h3 {
              font-size: 1.5rem;
              margin-bottom: 0.5rem;
            }
            
            p {
              margin-bottom: 1rem;
              opacity: 0.9;
            }
            
            .overlay-button {
              background: rgba(255,255,255,0.2);
              border: 1px solid rgba(255,255,255,0.3);
              color: white;
              padding: 0.5rem 1rem;
              border-radius: 6px;
              cursor: pointer;
              transition: all 0.3s ease;
              
              &:hover {
                background: rgba(255,255,255,0.3);
              }
            }
          }
        }
      }
    }
  }
}

// 内容区域样式 - 修复布局
.content-sections {
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  
  .section-container {
    margin-bottom: 4rem;
    padding: 3rem 2rem;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 20px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    }
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

// 优化的Stats Section - 移到精选推荐上方
.stats-section {
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23000000" opacity="0.02"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
    pointer-events: none;
  }
  
  .stats-container {
    position: relative;
    z-index: 2;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 2rem;
    max-width: 1000px;
    margin: 0 auto;
    
    .stat-item {
      text-align: center;
      padding: 2rem 1rem;
      border-radius: 12px;
      background: white;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      border: 1px solid rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(0,0,0,0.03), transparent);
        transition: left 0.6s ease;
      }
      
      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
        
        &::before {
          left: 100%;
        }
      }
      
      .stat-number {
        font-size: 3.5rem;
        font-weight: 800;
        margin-bottom: 0.5rem;
        color: #2c2c2c;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
      }
      
      .stat-label {
        font-size: 1.1rem;
        color: #666666;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
    }
  }
}

// 动画定义
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes twinkle {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 1; }
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes numberGlow {
  0% {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  }
  100% {
    text-shadow: 0 0 25px rgba(255, 255, 255, 0.8), 0 0 35px rgba(255, 255, 255, 0.4);
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

// 响应式设计
@media (max-width: $sm) {
  .hero-section {
    height: 65vh;
    min-height: 450px;
    
    .hero-section {
      height: 65vh;
      min-height: 450px;
      
      .hero-content {
        .hero-title {
          font-size: 2.5rem;
        }
        
        .hero-subtitle {
          font-size: 1rem;
        }
        
        .hero-actions {
          .cta-button {
            width: 100%;
            max-width: 300px;
            padding: 0.8rem 2rem;
          }
        }
      }
    }
    
    .featured-section {
      padding: 3rem 1rem;
      
      .section-title {
        font-size: 2rem;
      }
      
      .modern-carousel {
        height: 35vw !important;
      }
    }
    
    .recommendation-section {
      padding: 2rem 1rem;
      margin-bottom: 2rem;
    }
    
    .content-sections {
      padding: 2rem 1rem;
      
      .section-container {
        margin-bottom: 2rem;
        padding: 2rem 1rem;
      }
    }
    
    .stats-section {
      padding: 3rem 1rem;
      
      .stats-container {
        grid-template-columns: repeat(2, 1fr);
        gap: 1.5rem;
        
        .stat-item {
          padding: 1.5rem 1rem;
          
          .stat-number {
            font-size: 2.5rem;
          }
          
          .stat-label {
            font-size: 1rem;
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .stats-section {
    .stats-container {
      grid-template-columns: 1fr;
      gap: 1rem;
    }
  }
}
</style>
