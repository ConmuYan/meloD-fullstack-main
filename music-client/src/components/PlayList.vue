<template>
  <div class="play-list">
    <div class="play-title" v-if="title">{{ title }}</div>
    <ul
      class="play-body"
      :class="{ masonry: masonry && !gridColumns, grid: !!gridColumns }"
      :style="[
        masonry && !gridColumns ? { 'column-count': columns } : {},
        gridColumns ? { '--grid-columns': gridColumns } : {}
      ]"
    >
      <li class="card-frame" v-for="(item, index) in playList" :key="index">
        <div class="card-container">
          <div class="card" @click="goAblum(item)">
            <el-image class="card-img" fit="contain" :src="attachImageUrl(item.pic)" />
            <div class="mask" @click="goAblum(item)">
              <yin-icon class="mask-icon" :icon="BOFANG"></yin-icon>
            </div>
          </div>
        </div>
        <p class="card-name">{{ item.name || item.title }}</p>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, PropType } from "vue";

import YinIcon from "@/components/layouts/YinIcon.vue";
import mixin from "@/mixins/mixin";
import { Icon } from "@/enums";
import { HttpManager } from "@/api";

// 声明ResponseBody类型
interface ResponseBody {
  success: boolean;
  message: string;
  data: any;
  type?: string;
}
export default defineComponent({
  components: {
    YinIcon,
  },
  props: {
    title: String,
    playList: {
      type: Array as PropType<Array<any>>, // 容忍不同来源数据结构
      default: () => [],
    },
    path: String,
    masonry: {
      type: Boolean,
      default: false,
    },
    columns: {
      type: Number,
      default: 5,
    },
    // 固定列网格模式：每行固定N列
    gridColumns: {
      type: Number as PropType<number | null>,
      default: null,
    },
  },
  setup(props) {
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();

    const { path } = toRefs(props);

    function goAblum(item) {
      proxy.$store.commit("setSongDetails", item);
      routerManager(path.value, { path: `/${path.value}/${item.id}` });
    }
    return {
      BOFANG: Icon.BOFANG,
      goAblum,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.play-list {
  padding: 0 1rem;

  .play-title {
    height: 60px;
    line-height: 60px;
    font-size: 28px;
    font-weight: 500;
    text-align: center;
    color: $color-black;
    box-sizing: border-box;
  }

  .play-body {
    @include layout(flex-start, stretch, row, wrap);
  }
}

.play-body.masonry {
  column-gap: 1rem;
}

.play-body.masonry .card-frame {
  break-inside: avoid;
  display: inline-block;
  width: 100% !important;
  margin: 0 0 1rem;
}

.play-body.masonry .card {
  width: 100%;
}

.play-body.masonry .card .card-img {
  width: 100%;
  height: auto;
}

/* 固定列网格模式 */
.play-body.grid {
  display: grid;
  grid-template-columns: repeat(var(--grid-columns, 6), 1fr);
  gap: 12px;
}
.play-body.grid .card-frame {
  width: 100% !important;
  margin: 0;
}

.card-frame {
  .card-container {
    position: relative;
    
    .collect-btn {
      position: absolute;
      top: 8px;
      left: 8px;
      width: 32px;
      height: 32px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      z-index: 10;
      transition: all 0.3s ease;
      backdrop-filter: blur(10px);
      
      .collect-icon {
        font-size: 16px;
        color: #666;
        transition: all 0.3s ease;
      }
      
      &:hover {
        background: rgba(255, 255, 255, 1);
        transform: scale(1.1);
        
        .collect-icon {
          color: #ff6b6b;
        }
      }
      
      &.collected {
        background: rgba(255, 107, 107, 0.9);
        
        .collect-icon {
          color: #fff;
        }
        
        &:hover {
          background: rgba(255, 107, 107, 1);
          
          .collect-icon {
            color: #fff;
          }
        }
      }
    }
  }

  .card {
    position: relative;
    height: 0;
    padding-bottom: 100%;
    overflow: hidden;
    border-radius: 5px;

    .card-img {
      width: 100%;
      transition: all 0.4s ease;
    }
  }

  .card-name {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    line-clamp: 2; /* 标准属性，兼容性更好 */
    margin: 0.5rem 0;
  }

  &:hover .card-img {
    transform: scale(1.2);
  }
}

.mask {
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 5px;
  background-color: rgba(52, 47, 41, 0.4);
  @include layout(center, center);
  transition: all 0.3s ease-in-out;
  opacity: 0;

  .mask-icon {
    @include icon(2em, rgba(240, 240, 240, 1));
  }

  &:hover {
    opacity: 1;
    cursor: pointer;
  }
}

@media screen and (min-width: $sm) {
  .card-frame {
    width: 18%;
    margin: 0.5rem 1%;
  }
}

@media screen and (max-width: $sm) {
  .card-frame {
    width: 46%;
    margin: 0.5rem 2%;
    
    .card-container {
      .collect-btn {
        top: 6px;
        left: 6px;
        width: 28px;
        height: 28px;
        
        .collect-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
