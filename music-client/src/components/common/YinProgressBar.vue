<template>
  <div class="yin-progress-bar">
    <el-slider
      v-model="localProgress"
      :max="100"
      :min="0"
      size="small"
      :disabled="!isReady"
      @start="handleSliderStart"
      @input="handleSliderInput"
      @change="handleSliderEnd"
      class="progress-slider"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

// Props
const props = defineProps({
  // 当前播放时间（秒）
  currentTime: {
    type: Number,
    default: 0
  },
  // 总时长（秒）
  duration: {
    type: Number,
    default: 0
  },
  // 歌曲ID，用于绑定进度条状态
  songId: {
    type: [String, Number],
    default: null
  },
  // 是否可用
  disabled: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['seek'])

// 本地状态
const localProgress = ref(0) // 进度条显示的值（0-100）
const isDragging = ref(false) // 是否正在拖拽
const isReady = ref(false) // 是否准备就绪
const lastSongId = ref(null) // 上一个歌曲ID

// 计算属性
const progressPercent = computed(() => {
  if (props.duration <= 0) return 0
  return Math.min(Math.max(0, (props.currentTime / props.duration) * 100), 100)
})

// 监听器
watch(() => props.songId, (newSongId, oldSongId) => {
  // 歌曲切换时重置状态
  if (newSongId !== oldSongId) {
    resetProgressBar()
    lastSongId.value = newSongId
  }
}, { immediate: true })

watch(() => props.duration, (newDuration) => {
  // 当获取到有效时长时，标记为准备就绪
  isReady.value = newDuration > 0
  if (newDuration <= 0) {
    resetProgressBar()
  }
})

watch(progressPercent, (newPercent) => {
  // 只有在非拖拽状态下才更新进度条显示
  if (!isDragging.value) {
    localProgress.value = newPercent
  }
})

watch(() => props.disabled, (disabled) => {
  if (disabled) {
    resetProgressBar()
  }
})

// 方法
function resetProgressBar() {
  isDragging.value = false
  isReady.value = props.duration > 0
  // 重置后立即同步当前进度
  localProgress.value = progressPercent.value
}

function handleSliderStart() {
  if (!isReady.value) return
  isDragging.value = true
}

function handleSliderInput(value) {
  if (!isReady.value) return
  isDragging.value = true
  // 确保值在有效范围内
  localProgress.value = Math.min(Math.max(0, value), 100)
}

function handleSliderEnd() {
  if (props.duration <= 0) {
    isDragging.value = false
    localProgress.value = 0
    return
  }
  
  // 计算目标时间
  const targetPercent = Math.min(Math.max(0, localProgress.value), 100)
  const targetTime = (targetPercent / 100) * props.duration
  const safeTargetTime = Math.min(Math.max(0, targetTime), props.duration)
  
  // 先发射seek事件
  emit('seek', safeTargetTime)
  
  // 延迟结束拖拽状态，确保seek事件先处理
  setTimeout(() => {
    isDragging.value = false
  }, 100)
}

// 公开方法（供父组件调用）
function forceUpdate() {
  if (!isDragging.value && isReady.value) {
    localProgress.value = progressPercent.value
  }
}

// 暴露给父组件
defineExpose({
  forceUpdate,
  resetProgressBar
})

// 生命周期
onMounted(() => {
  resetProgressBar()
})

onUnmounted(() => {
  isDragging.value = false
})
</script>

<style scoped>
.yin-progress-bar {
  width: 100%;
  padding: 0 10px;
}

.progress-slider {
  width: 100%;
}

.progress-slider :deep(.el-slider__runway) {
  height: 4px;
  background-color: rgba(255, 255, 255, 0.3);
}

.progress-slider :deep(.el-slider__bar) {
  background-color: #409eff;
}

.progress-slider :deep(.el-slider__button) {
  width: 12px;
  height: 12px;
  border: 2px solid #409eff;
  background-color: #fff;
}

.progress-slider :deep(.el-slider__button:hover) {
  transform: scale(1.2);
}

.progress-slider.is-disabled :deep(.el-slider__runway) {
  background-color: rgba(255, 255, 255, 0.1);
}

.progress-slider.is-disabled :deep(.el-slider__bar) {
  background-color: rgba(64, 158, 255, 0.5);
}
</style>