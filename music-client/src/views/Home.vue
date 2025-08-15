<template>
  <!-- Hero Section -->
  <div class="hero-section">
    <div class="hero-background">
      <div class="gradient-overlay"></div>
      <div class="floating-elements">
        <div class="floating-circle circle-1"></div>
        <div class="floating-circle circle-2"></div>
        <div class="floating-circle circle-3"></div>
        <div class="floating-dot dot-1"></div>
        <div class="floating-dot dot-2"></div>
        <div class="floating-dot dot-3"></div>
      </div>
    </div>
    
    <div class="hero-decorations">
      <div class="decoration decoration-1">♪</div>
      <div class="decoration decoration-2">♫</div>
      <div class="decoration decoration-3">♪</div>
      <div class="decoration decoration-4">♫</div>
    </div>
    
    <div class="hero-content">
      <h1 class="hero-title">
        <span class="title-line">发现你的</span>
        <span class="title-line highlight">音乐世界</span>
      </h1>
      <p class="hero-subtitle">探索无限音乐可能，让每一首歌都成为你的专属回忆</p>
      <div class="hero-actions">
        <button class="cta-button primary" @click="exploreMusic">
          <span>开始探索</span>
          <div class="button-glow"></div>
        </button>
      </div>
    </div>
  </div>

  <!-- 修复轮播图：只显示推荐歌单 -->
  <div class="featured-section" v-if="recommendationThemes.length">
    <h2 class="section-title">精选推荐</h2>
    <el-carousel class="modern-carousel" type="card" height="22vw" :interval="5000" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in recommendationThemes.slice(0, 3)" :key="index" class="carousel-item">
        <div class="carousel-content" @click="handleCarouselClick(item)">
          <img :src="getImageUrl(item.pic)" class="carousel-image" alt="推荐歌单" />
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

  <!-- 统计数据区域 -->
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
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

import EnhancedPlayList from "@/components/EnhancedPlayList.vue";
import { NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const router = useRouter();
const store = useStore();
const songList = ref([]);
const singerList = ref([]);
const swiperList = ref([]);
const recommendationThemes = ref([]);
const { changeIndex } = mixin();

// 修复图片URL处理
const getImageUrl = (pic: string) => {
  if (!pic) return '/img/songListPic/default.jpg';
  if (pic.startsWith('http')) return pic;
  return HttpManager.attachImageUrl(pic);
};

// 计算属性：检查用户是否登录
const isLoggedIn = computed(() => {
  return store.getters.isLogin;
});

// 统计数据
const stats = ref([
  { number: "10K+", label: "精选歌曲" },
  { number: "500+", label: "优质歌单" },
  { number: "200+", label: "知名歌手" },
  { number: "50K+", label: "活跃用户" }
]);

// 修复导航栏状态的探索音乐方法
const exploreMusic = () => {
  changeIndex(NavName.SongSheet);
  router.push('/song-sheet');
};

// 获取推荐主题（用于轮播图）
const getRecommendationThemes = async () => {
  try {
    // 添加时间戳参数，避免缓存
    const timestamp = new Date().getTime();
    const res = await HttpManager.getRecommendationThemes();
    if (res && (res as ResponseBody).data && (res as ResponseBody).data.themes) {
      recommendationThemes.value = (res as ResponseBody).data.themes;
    }
  } catch (error) {
    console.error('获取推荐主题失败:', error);
  }
};

// 修复轮播图点击事件
const handleCarouselClick = (item: any) => {
  // 推荐歌单，跳转到推荐歌单详情页
  store.commit('setCurrentRecommendationPlaylist', item);
  router.push({
    path: '/recommendation-playlist',
    query: {
      id: item.id,
      theme: item.theme
    }
  });
};

// 数据加载
try {
  HttpManager.getSongList().then((res) => {
    songList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  HttpManager.getAllSinger().then((res) => {
    singerList.value = (res as ResponseBody).data.sort().slice(0, 8);
  });

  onMounted(() => {
    changeIndex(NavName.Home);
    getRecommendationThemes();
    
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
  
  .hero-decorations {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .decoration {
      position: absolute;
      font-size: 2rem;
      color: rgba(255, 255, 255, 0.1);
      animation: float 6s ease-in-out infinite;
      
      &.decoration-1 {
        top: 15%;
        left: 15%;
        animation-delay: 0s;
      }
      
      &.decoration-2 {
        top: 25%;
        right: 20%;
        animation-delay: 1.5s;
      }
      
      &.decoration-3 {
        bottom: 30%;
        left: 25%;
        animation-delay: 3s;
      }
      
      &.decoration-4 {
        bottom: 20%;
        right: 15%;
        animation-delay: 4.5s;
      }
    }
  }
  
  .hero-content {
    position: relative;
    z-index: 10;
    text-align: center;
    color: white;
    max-width: 800px;
    padding: 0 2rem;
    
    .hero-title {
      font-size: 4rem;
      font-weight: 800;
      margin-bottom: 1.5rem;
      line-height: 1.2;
      
      .title-line {
        display: block;
        
        &.highlight {
          background: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1);
          background-size: 200% 200%;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          animation: gradientShift 3s ease-in-out infinite;
        }
      }
    }
    
    .hero-subtitle {
      font-size: 1.25rem;
      margin-bottom: 2.5rem;
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

// 统计数据区域样式 - 修复布局
.stats-section {
  padding: 5rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
    opacity: 0.3;
  }
  
  .stats-container {
    position: relative;
    z-index: 2;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 3rem;
    max-width: 1200px;
    margin: 0 auto;
    
    .stat-item {
      text-align: center;
      color: white;
      padding: 2rem;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 15px;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-10px);
        background: rgba(255, 255, 255, 0.15);
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
      }
      
      .stat-number {
        font-size: 3.5rem;
        font-weight: 800;
        margin-bottom: 0.5rem;
        background: linear-gradient(45deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-shadow: 0 0 30px rgba(255, 255, 255, 0.5);
        animation: numberGlow 2s ease-in-out infinite alternate;
      }
      
      .stat-label {
        font-size: 1.2rem;
        opacity: 0.9;
        font-weight: 500;
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
