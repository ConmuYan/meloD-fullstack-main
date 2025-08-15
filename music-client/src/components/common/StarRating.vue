<template>
  <div class="star-rating">
    <!-- 显示平均评分 -->
    <div v-if="showAverage" class="average-rating">
      <h3>{{ averageTitle }}</h3>
      <el-rate v-model="displayAverage" allow-half disabled></el-rate>
      <span class="score-text">{{ averageScore }}</span>
      <span v-if="showCount" class="count-text">({{ ratingCount }}人评分)</span>
    </div>
    
    <!-- 用户评分 -->
    <div v-if="showUserRating" class="user-rating">
      <h3>{{ userTitle }} {{ userScore }}</h3>
      <el-rate 
        allow-half 
        v-model="internalUserRating" 
        :disabled="disabled" 
        @change="handleRatingChange"
      ></el-rate>
      <span v-if="isDisabled && internalUserRating > 0" class="rated-text">已评价</span>
      <span v-else-if="!isDisabled" class="rate-text">点击星星评分</span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted } from 'vue';
import { HttpManager } from '@/api';
import { ElMessage } from 'element-plus';

export default defineComponent({
  name: 'StarRating',
  props: {
    // 歌曲和用户ID
    songId: {
      type: Number,
      default: null
    },
    userId: {
      type: Number,
      default: null
    },
    
    // 平均评分相关
    averageRating: {
      type: Number,
      default: 0
    },
    ratingCount: {
      type: Number,
      default: 0
    },
    showAverage: {
      type: Boolean,
      default: true
    },
    averageTitle: {
      type: String,
      default: '平均评分'
    },
    showCount: {
      type: Boolean,
      default: true
    },
    
    // 用户评分相关
    userRating: {
      type: Number,
      default: 0
    },
    showUserRating: {
      type: Boolean,
      default: true
    },
    userTitle: {
      type: String,
      default: '我的评分'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    
    // 评分制度 (5星制或10分制)
    maxScore: {
      type: Number,
      default: 10
    }
  },
  emits: ['rating-change'],
  setup(props, { emit }) {
    const internalUserRating = ref(props.userRating / (props.maxScore / 5));
    const currentAverageRating = ref(props.averageRating);
    const currentRatingCount = ref(props.ratingCount);
    const isDisabled = ref(props.disabled);
    
    // 计算显示的平均评分 (转换为5星制)
    const displayAverage = computed(() => {
      return currentAverageRating.value / (props.maxScore / 5);
    });
    
    // 计算显示的平均分数
    const averageScore = computed(() => {
      return currentAverageRating.value.toFixed(1);
    });
    
    // 计算显示的用户分数
    const userScore = computed(() => {
      return (internalUserRating.value * (props.maxScore / 5)).toFixed(1);
    });
    
    // 监听用户评分变化
    watch(() => props.userRating, (newVal) => {
      internalUserRating.value = newVal / (props.maxScore / 5);
    }, { immediate: true });
    
    // 获取歌曲平均评分
    const getSongAverageRating = async () => {
      if (!props.songId || props.songId <= 0) {
        console.warn('无效的歌曲ID，跳过获取平均评分');
        return;
      }
      
      try {
        const result = await HttpManager.getRankOfSongId(props.songId) as ResponseBody;
        if (result.success && result.data !== null) {
          currentAverageRating.value = result.data;
        }
      } catch (error) {
        console.error('获取歌曲平均评分失败:', error);
        // 设置默认值，避免显示异常
        currentAverageRating.value = 0;
      }
    };
    
    // 获取用户对歌曲的评分
    const getUserSongRating = async () => {
      if (!props.songId || props.songId <= 0 || !props.userId || props.userId <= 0) {
        console.warn('无效的歌曲ID或用户ID，禁用评分功能');
        isDisabled.value = true;
        internalUserRating.value = 0;
        return;
      }
      
      try {
        const result = await HttpManager.getUserSongRank(props.userId, props.songId) as ResponseBody;
        if (result.success && result.data > 0) {
          // 用户已评分
          internalUserRating.value = result.data / (props.maxScore / 5);
          isDisabled.value = true;
        } else {
          // 用户未评分
          internalUserRating.value = 0;
          isDisabled.value = false;
        }
      } catch (error) {
          console.error('获取用户歌曲评分失败:', error);
          // 出错时禁用评分功能
          isDisabled.value = true;
          internalUserRating.value = 0;
        }
    };
    
    // 组件挂载时获取评分数据
    onMounted(() => {
      getSongAverageRating();
      getUserSongRating();
    });
    
    // 处理评分变化
    const handleRatingChange = async (value: number) => {
      if (isDisabled.value || !props.songId || !props.userId) return;
      
      const actualScore = value * (props.maxScore / 5);
      
      try {
        const result = await HttpManager.setSongRank({
          songId: props.songId,
          consumerId: props.userId,
          score: actualScore
        }) as ResponseBody;
        
        if (result.success) {
          ElMessage.success(`评分成功：${actualScore}分`);
          isDisabled.value = true;
          // 重新获取平均评分
          await getSongAverageRating();
          emit('rating-change', actualScore);
        } else {
          ElMessage.error(result.message || '评分失败');
          // 恢复原来的评分
          await getUserSongRating();
        }
      } catch (error) {
        console.error('提交歌曲评分失败:', error);
        ElMessage.error('评分失败，请重试');
        // 恢复原来的评分
        await getUserSongRating();
      }
    };
    
    return {
      internalUserRating,
      displayAverage,
      averageScore,
      userScore,
      isDisabled,
      handleRatingChange
    };
  }
});
</script>

<style lang="scss" scoped>
.star-rating {
  .average-rating, .user-rating {
    margin-bottom: 20px;
    
    h3 {
      margin: 0 0 8px 0;
      font-size: 16px;
      color: #333;
    }
    
    .el-rate {
      margin-right: 10px;
    }
    
    .score-text {
      font-size: 18px;
      font-weight: bold;
      color: #409eff;
      margin-right: 10px;
    }
    
    .count-text {
      font-size: 14px;
      color: #666;
    }
  }
  
  .user-rating {
    .el-rate {
      &:not(.is-disabled) {
        cursor: pointer;
      }
    }
    
    .rated-text {
      font-size: 14px;
      color: #67c23a;
      margin-left: 10px;
    }
    
    .rate-text {
      font-size: 14px;
      color: #909399;
      margin-left: 10px;
    }
  }
}
</style>