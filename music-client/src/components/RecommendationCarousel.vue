<template>
  <div class="recommendation-carousel">
    <div class="cards-container">
      <div 
        v-for="(theme, index) in themes.slice(0, 3)"
        :key="theme.id"
        class="theme-card"
        :class="`theme-${theme.theme}`"
        @click="handleThemeClick(theme)"
        :style="{ animationDelay: `${index * 0.2}s` }"
      >
        <div class="card-background">
          <div 
            class="background-image"
            :class="{
              'default-image': isDefaultImage(theme.pic),
              'custom-image': !isDefaultImage(theme.pic)
            }"
            :style="getImageStyle(theme)"
          >
            <img 
              v-if="!isDefaultImage(theme.pic)"
              :src="attachImageUrl(theme.pic)" 
              :alt="theme.title"
              class="custom-cover"
              @error="handleImageError"
            />
          </div>
          <div class="background-overlay"></div>
        </div>
        
        <div class="card-content">
          <div class="content-wrapper">
            <h3 class="theme-title">{{ theme.title }}</h3>
            <p class="theme-description">{{ theme.description }}</p>
            <div class="theme-stats">
              <span class="song-count">
                <i class="el-icon-headset"></i>
                {{ theme.songCount }} 首歌曲
              </span>
              <span class="theme-tag">{{ getThemeTag(theme.theme) }}</span>
            </div>
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="small"
                @click.stop="playTheme(theme)"
                class="play-btn"
              >
                <i class="el-icon-video-play"></i>
                播放
              </el-button>
              <el-button 
                size="small"
                @click.stop="viewThemeDetail(theme)"
                class="detail-btn"
              >
                详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { HttpManager } from '@/api/index'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const router = useRouter()

const themes = ref([])
const currentIndex = ref(0)
const loading = ref(true)
const carousel = ref(null)

const attachImageUrl = computed(() => {
  return (url) => {
    if (!url) return '/img/songListPic/default.jpg'
    return url.startsWith('http') ? url : HttpManager.attachImageUrl(url)
  }
})

// 检测是否为默认图片
const isDefaultImage = (pic) => {
  if (!pic) return true
  const defaultImages = [
    '/img/songListPic/default.jpg',
    'default.jpg',
    '/img/songListPic/theme-hot.jpg',
    '/img/songListPic/theme-new.jpg',
    '/img/songListPic/theme-classic.jpg'
  ]
  return defaultImages.some(defaultImg => pic.includes(defaultImg))
}

// 根据主题获取智能换色样式
const getImageStyle = (theme) => {
  if (!isDefaultImage(theme.pic)) {
    return {} // 自定义图片不进行换色处理
  }
  
  // 根据主题类型生成渐变背景
  const themeColors = {
    hot: {
      background: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a24 50%, #ff9ff3 100%)',
      pattern: 'radial-gradient(circle at 20% 80%, rgba(255,255,255,0.1) 0%, transparent 50%), radial-gradient(circle at 80% 20%, rgba(255,255,255,0.1) 0%, transparent 50%)'
    },
    new: {
      background: 'linear-gradient(135deg, #4a90e2 0%, #2d9cdb 50%, #74b9ff 100%)',
      pattern: 'radial-gradient(circle at 30% 70%, rgba(255,255,255,0.1) 0%, transparent 50%), radial-gradient(circle at 70% 30%, rgba(255,255,255,0.1) 0%, transparent 50%)'
    },
    classic: {
      background: 'linear-gradient(135deg, #a29bfe 0%, #74b9ff 50%, #fd79a8 100%)',
      pattern: 'radial-gradient(circle at 40% 60%, rgba(255,255,255,0.1) 0%, transparent 50%), radial-gradient(circle at 60% 40%, rgba(255,255,255,0.1) 0%, transparent 50%)'
    },
    personal: {
      background: 'linear-gradient(135deg, #fd79a8 0%, #fdcb6e 50%, #6c5ce7 100%)',
      pattern: 'radial-gradient(circle at 25% 75%, rgba(255,255,255,0.1) 0%, transparent 50%), radial-gradient(circle at 75% 25%, rgba(255,255,255,0.1) 0%, transparent 50%)'
    }
  }
  
  const colors = themeColors[theme.theme] || themeColors.hot
  
  return {
    background: colors.background,
    backgroundImage: colors.pattern,
    backgroundSize: '100% 100%, 200px 200px, 150px 150px',
    backgroundPosition: 'center, 0 0, 100% 100%'
  }
}

const fetchRecommendationThemes = async () => {
  try {
    loading.value = true
    const response = await HttpManager.getRecommendationThemes()
    if (response.success && response.data) {
      if (Array.isArray(response.data)) {
        themes.value = response.data
      } else if (response.data.themes && Array.isArray(response.data.themes)) {
        themes.value = response.data.themes
      } else {
        themes.value = getDefaultThemes()
      }
    } else {
      themes.value = getDefaultThemes()
    }
  } catch (error) {
    console.error('获取推荐主题失败:', error)
    themes.value = getDefaultThemes()
    ElMessage.warning('推荐主题加载失败，使用默认数据')
  } finally {
    loading.value = false
  }
}

const getDefaultThemes = () => {
  return [
    {
      id: 'default-hot',
      title: '今日热门',
      description: '最受欢迎的音乐推荐',
      pic: '/img/songListPic/theme-hot.jpg',
      theme: 'hot',
      songCount: 15,
      songs: []
    },
    {
      id: 'default-new',
      title: '新歌推荐',
      description: '最新发布的精选音乐',
      pic: '/img/songListPic/theme-new.jpg',
      theme: 'new',
      songCount: 15,
      songs: []
    },
    {
      id: 'default-classic',
      title: '经典回顾',
      description: '永不过时的经典音乐',
      pic: '/img/songListPic/theme-classic.jpg',
      theme: 'classic',
      songCount: 15,
      songs: []
    }
  ]
}

const getThemeTag = (theme) => {
  const tags = {
    hot: '热门',
    new: '最新',
    classic: '经典',
    personal: '个性化'
  }
  return tags[theme] || '推荐'
}

const handleSlideChange = (index) => {
  currentIndex.value = index
}

const handleThemeClick = (theme) => {
  viewThemeDetail(theme)
}

const playTheme = (theme) => {
  if (theme.songs && theme.songs.length > 0) {
    store.dispatch('playMusic', {
      id: theme.songs[0].id,
      url: theme.songs[0].url,
      pic: theme.songs[0].pic,
      index: 0,
      name: theme.songs[0].name,
      lyric: theme.songs[0].lyric,
      currentSongList: theme.songs
    })
    ElMessage.success(`开始播放：${theme.title}`)
  } else {
    ElMessage.warning('该主题暂无可播放歌曲')
  }
}

const viewThemeDetail = (theme) => {
  // 将推荐主题数据存储到store中，供RecommendationPlaylist页面使用
  store.commit('setCurrentRecommendationPlaylist', {
    id: theme.id,
    title: theme.title,
    description: theme.description,
    pic: theme.pic,
    songCount: theme.songCount,
    songs: theme.songs || [],
    theme: theme.theme
  })
  
  // 跳转到推荐歌单详情页面
  router.push({
    path: '/recommendation-playlist',
    query: {
      theme: theme.theme,
      id: theme.id
    }
  })
}

const handleImageError = (event) => {
  event.target.src = '/img/songListPic/default.jpg'
}

onMounted(() => {
  fetchRecommendationThemes()
})
</script>

<style lang="scss" scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.recommendation-carousel {
  width: 100%;
  margin-top: 50px;
  margin-bottom: 50px;
  position: relative;
  
  .cards-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: clamp(16px, 3vw, 32px);
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 clamp(20px, 5vw, 60px);
    width: 100%;
  }
  
  .theme-card {
    position: relative;
    height: 400px;
    border-radius: 20px;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    animation: fadeInUp 0.6s ease-out both;
    
    &:hover {
      transform: translateY(-12px) scale(1.02);
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
      
      .background-image {
        transform: scale(1.1);
      }
      
      .card-content {
        background: linear-gradient(
          180deg,
          rgba(0, 0, 0, 0.1) 0%,
          rgba(0, 0, 0, 0.8) 100%
        );
      }
      
      .theme-title {
        animation: float 2s ease-in-out infinite;
      }
      
      .action-buttons {
        .play-btn, .detail-btn {
          transform: translateY(-2px);
        }
      }
    }
    
    .card-background {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      
      .background-image {
        width: 100%;
        height: 100%;
        position: relative;
        transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
        
        &.default-image {
          background-size: cover;
          background-position: center;
          background-repeat: no-repeat;
          
          &::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 60px;
            height: 60px;
            background: rgba(232, 226, 226, 0.93);
            border-radius: 50%;
            border: 2px solid rgba(255, 255, 255, 0.3);
            backdrop-filter: blur(10px);
          }
          
          &::after {
            content: '♪';
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 1.8rem;
            color: rgba(255, 255, 255, 0.9);
            font-weight: bold;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
          }
        }
        
        &.custom-image {
          .custom-cover {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
          }
        }
      }
      
      .background-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(
          180deg,
          rgba(0, 0, 0, 0.2) 0%,
          rgba(0, 0, 0, 0.6) 100%
        );
      }
    }
    
    .card-content {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 24px;
      background: linear-gradient(
        180deg,
        rgba(0, 0, 0, 0.1) 0%,
        rgba(0, 0, 0, 0.7) 100%
      );
      color: white;
      transition: all 0.3s ease;
      
      .content-wrapper {
        .theme-title {
          font-size: 1.5rem;
          font-weight: 700;
          margin-bottom: 8px;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
          line-height: 1.3;
          transition: all 0.3s ease;
        }
        
        .theme-description {
          font-size: 0.9rem;
          margin-bottom: 16px;
          opacity: 0.9;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .theme-stats {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;
          
          .song-count {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.95rem;
            opacity: 0.9;
            
            i {
              font-size: 1rem;
            }
          }
          
          .theme-tag {
            background: rgba(216, 16, 16, 0.2);
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 0.75rem;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
          }
        }
        
        .action-buttons {
          display: flex;
          gap: 8px;
          
          .play-btn {
            flex: 1;
            background: linear-gradient(135deg,rgba(117, 222, 72, 0.6) 0%,rgba(18, 85, 173, 0.74) 100%);
            border: none;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.85rem;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
            
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: -100%;
              width: 100%;
              height: 100%;
              background: linear-gradient(
                90deg,
                transparent,
                rgba(255, 255, 255, 0.2),
                transparent
              );
              transition: left 0.6s ease;
            }
            
            &:hover {
              box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
              
              &::before {
                left: 100%;
              }
            }
          }
          
          .detail-btn {
            flex: 1;
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.2);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            backdrop-filter: blur(10px);
            font-size: 0.85rem;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            
            &:hover {
              background: rgba(255, 255, 255, 0.25);
              border-color: rgba(255, 255, 255, 0.4);
              box-shadow: 0 4px 15px rgba(255, 255, 255, 0.1);
            }
          }
        }
      }
    }
    
    &.theme-hot {
      .card-background .background-overlay {
        background: linear-gradient(
          180deg,
          rgba(255, 107, 107, 0.3) 0%,
          rgba(238, 90, 36, 0.7) 100%
        );
      }
    }
    
    &.theme-new {
      .card-background .background-overlay {
        background: linear-gradient(
          180deg,
          rgba(74, 144, 226, 0.3) 0%,
          rgba(45, 156, 219, 0.7) 100%
        );
      }
    }
    
    &.theme-classic {
      .card-background .background-overlay {
        background: linear-gradient(
          180deg,
          rgba(162, 155, 254, 0.3) 0%,
          rgba(116, 185, 255, 0.7) 100%
        );
      }
    }
  }
}

@media (max-width: 1024px) {
  .recommendation-carousel {
    .cards-container {
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
    }
    
    .theme-card {
      height: 280px;
    }
  }
}

@media (max-width: 1024px) {
  .recommendation-carousel {
    .cards-container {
      grid-template-columns: repeat(2, 1fr);
      gap: clamp(12px, 2.5vw, 24px);
      padding: 0 clamp(16px, 4vw, 40px);
    }
    
    .theme-card {
      height: 280px;
    }
  }
}

@media (max-width: 768px) {
  .recommendation-carousel {
    .cards-container {
      grid-template-columns: 1fr;
      gap: 16px;
      padding: 0 16px;
    }
    
    .theme-card {
      height: 240px;
      
      .card-content {
        padding: 20px;
        
        .content-wrapper {
          .theme-title {
            font-size: 1.3rem;
          }
          
          .theme-description {
            font-size: 0.85rem;
            margin-bottom: 12px;
          }
          
          .theme-stats {
            margin-bottom: 12px;
            
            .song-count {
              font-size: 0.8rem;
            }
            
            .theme-tag {
              font-size: 0.7rem;
              padding: 3px 8px;
            }
          }
          
          .action-buttons {
            .play-btn, .detail-btn {
              padding: 6px 12px;
              font-size: 0.8rem;
            }
          }
        }
      }
    }
  }
}
</style>