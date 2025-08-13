<template>
  <div class="enhanced-play-list">
    <div class="section-header">
      <div class="header-content">
        <h2 class="section-title">{{ title }}</h2>
        <p class="section-subtitle" v-if="subtitle">{{ subtitle }}</p>
      </div>
      <!-- 优化后的查看全部按钮 - 更小更精致，移除图标 -->
      <button class="view-all-btn" @click="viewAll" v-if="showViewAll">
        <span>查看全部</span>
      </button>
    </div>
    
    <div class="cards-container">
      <div 
        class="enhanced-card" 
        v-for="(item, index) in displayList" 
        :key="index"
        @click="goAblum(item)"
        :style="{ '--delay': index * 0.08 + 's' }"
      >
        <div class="card-image-container">
          <el-image class="card-image" fit="cover" :src="attachImageUrl(item.pic)" />
          <div class="image-overlay">
            <div class="play-button">
              <yin-icon class="play-icon" :icon="BOFANG"></yin-icon>
            </div>
          </div>
          <div class="card-badge" v-if="index < 3">热门</div>
          <!-- 添加微妙的光效 -->
          <div class="card-shimmer"></div>
        </div>
        
        <div class="card-content">
          <h3 class="card-title">{{ item.name || item.title }}</h3>
          <p class="card-subtitle" v-if="item.singer || item.introduction">{{ item.singer || item.introduction }}</p>
        </div>
        
        <div class="card-glow"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, reactive, computed } from "vue";
import YinIcon from "@/components/layouts/YinIcon.vue";
import mixin from "@/mixins/mixin";
import { Icon, NavName } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    YinIcon,
  },
  props: {
    title: String,
    subtitle: String,
    playList: {
      type: Array as () => Array<any>,
      default: () => []
    },
    path: String,
    showViewAll: {
      type: Boolean,
      default: true
    },
    maxDisplay: {
      type: Number,
      default: 8
    }
  },
  setup(props) {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();
    const { path, playList, maxDisplay } = toRefs(props);
    
    const iconList = reactive({
      ARROW_RIGHT: Icon.BOFANG, // 使用播放图标替代箭头图标
    });

    // 限制显示数量，避免首页过长
    const displayList = computed(() => {
      return playList.value ? playList.value.slice(0, maxDisplay.value) : [];
    });

    function goAblum(item) {
      proxy.$store.commit("setSongDetails", item);
      routerManager(path.value, { path: `/${path.value}/${item.id}` });
    }
    
    function viewAll() {
      // 修复导航栏状态并确保路由跳转正确
      if (path.value === 'song-sheet-detail') {
        changeIndex(NavName.SongSheet); // 设置导航栏状态为歌单
        routerManager('song-sheet', { path: '/song-sheet' });
      } else if (path.value === 'singer-detail') {
        changeIndex(NavName.Singer); // 设置导航栏状态为歌手
        routerManager('singer', { path: '/singer' });
      } else {
        routerManager(path.value, { path: `/${path.value}` });
      }
    }

    return {
      BOFANG: Icon.BOFANG,
      iconList,
      displayList,
      goAblum,
      viewAll,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.enhanced-play-list {
  padding: 0;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 2rem;
    padding: 0 1rem;
    
    .header-content {
      .section-title {
        font-size: 1.8rem;
        font-weight: 700;
        color: #2c2c2c;
        margin-bottom: 0.5rem;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 0;
          width: 50px;
          height: 3px;
          background: linear-gradient(90deg, #666666, #999999);
          border-radius: 2px;
        }
      }
      
      .section-subtitle {
        color: #666666;
        font-size: 0.95rem;
        margin: 0;
      }
    }
    
    // 优化后的查看全部按钮 - 更小更精致，无图标
    .view-all-btn {
      display: inline-flex;
      align-items: center;
      padding: 0.3rem 0.6rem;
      background: transparent;
      border: 1px solid rgba(102, 102, 102, 0.15);
      color: #666666;
      border-radius: 3px;
      cursor: pointer;
      font-weight: 400;
      font-size: 0.75rem;
      transition: all 0.25s ease;
      position: relative;
      overflow: hidden;
      min-width: auto;
      
      span {
        position: relative;
        z-index: 2;
        white-space: nowrap;
      }
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(102, 102, 102, 0.03), transparent);
        transition: left 0.4s ease;
      }
      
      &:hover {
        background: rgba(102, 102, 102, 0.02);
        border-color: rgba(102, 102, 102, 0.3);
        color: #555555;
        transform: translateY(-0.5px);
        box-shadow: 0 1px 4px rgba(102, 102, 102, 0.08);
        
        &::before {
          left: 100%;
        }
      }
      
      &:active {
        transform: translateY(0);
        box-shadow: 0 0 2px rgba(102, 102, 102, 0.1);
      }
    }
  }
  
  .cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 1.5rem;
    padding: 0 1rem;
    
    .enhanced-card {
      position: relative;
      cursor: pointer;
      border-radius: 8px;
      overflow: hidden;
      background: white;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      opacity: 0;
      transform: translateY(30px);
      animation: slideInUp 0.6s ease forwards;
      animation-delay: var(--delay);
      
      .card-image-container {
        position: relative;
        height: 180px;
        overflow: hidden;
        
        .card-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.4s ease;
          filter: grayscale(10%);
        }
        
        // 添加微妙的光效
        .card-shimmer {
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
          transition: left 0.8s ease;
        }
        
        .image-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s ease;
          
          .play-button {
            width: 45px;
            height: 45px;
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.8);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            
            .play-icon {
              color: white;
              font-size: 1.1rem;
            }
            
            &:hover {
              background: rgba(255, 255, 255, 0.25);
              transform: scale(1.1);
            }
          }
        }
        
        .card-badge {
          position: absolute;
          top: 8px;
          left: 8px;
          background: linear-gradient(45deg, #666666, #888888);
          color: white;
          padding: 0.15rem 0.5rem;
          border-radius: 3px;
          font-size: 0.65rem;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        }
      }
      
      .card-content {
        padding: 1rem;
        
        .card-title {
          font-size: 0.95rem;
          font-weight: 600;
          color: #2c2c2c;
          margin-bottom: 0.4rem;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          line-clamp: 2; /* 标准属性 */
          line-height: 1.4;
        }
        
        .card-subtitle {
          color: #666666;
          font-size: 0.8rem;
          margin: 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
      
      .card-glow {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(45deg, rgba(102, 102, 102, 0.1), rgba(153, 153, 153, 0.1));
        opacity: 0;
        transition: opacity 0.3s ease;
        pointer-events: none;
        border-radius: 8px;
        filter: blur(12px);
        z-index: -1;
      }
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
        
        .card-image {
          transform: scale(1.02);
          filter: grayscale(0%);
        }
        
        .card-shimmer {
          left: 100%;
        }
        
        .image-overlay {
          opacity: 1;
        }
        
        .card-glow {
          opacity: 0.15;
        }
      }
    }
  }
}

@keyframes slideInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: $sm) {
  .enhanced-play-list {
    .section-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 1rem;
      
      .view-all-btn {
        align-self: flex-end;
        font-size: 0.75rem;
        padding: 0.35rem 0.7rem;
      }
    }
    
    .cards-container {
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      gap: 1rem;
      
      .enhanced-card {
        .card-image-container {
          height: 140px;
        }
        
        .card-content {
          padding: 0.8rem;
        }
      }
    }
  }
}
</style>