<template>
  <audio :src="attachImageUrl(songUrl)" controls="controls" :ref="player" preload="true" @canplay="canplay" @timeupdate="timeupdate" @ended="ended">
    <!--（1）属性：controls，preload（2）事件：canplay，timeupdate，ended（3）方法：play()，pause() -->
    <!--controls：向用户显示音频控件（播放/暂停/进度条/音量）-->
    <!--preload：属性规定是否在页面加载后载入音频-->
    <!--canplay：当音频/视频处于加载过程中时，会发生的事件-->
    <!--timeupdate：当目前的播放位置已更改时-->
    <!--ended：当目前的播放列表已结束时-->
  </audio>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, watch } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";
import { onMounted } from 'vue';

export default defineComponent({
  setup() {

    const { proxy } = getCurrentInstance();
    const store = useStore();
    const divRef = ref<HTMLAudioElement>();
    const player = (el) => {
      divRef.value = el;
    };

     const muted = ref(true); // 添加一个 reactive 的 muted 属性

    const songUrl = computed(() => store.getters.songUrl); // 音乐链接
    const isPlay = computed(() => store.getters.isPlay); // 播放状态
    const volume = computed(() => store.getters.volume); // 音量
    const changeTime = computed(() => store.getters.changeTime); // 指定播放时刻
    const autoNext = computed(() => store.getters.autoNext); // 用于触发自动播放下一首

    // 监听播放还是暂停
    watch(isPlay, (newValue) => {
      if (divRef.value) {
        if (newValue) {
          divRef.value.play().catch(err => {
            console.error("播放失败:", err);
            proxy.$store.commit("setIsPlay", false);
          });
        } else {
          divRef.value.pause();
        }
      }
    });

    // 跳到指定时刻播放
    let isSeekingRef = ref(false);
    watch(changeTime, (newTime) => {
      if (divRef.value && newTime >= 0) {
        console.log('YinAudio changeTime watch:', newTime);
        isSeekingRef.value = true;
        // 确保音频已加载且时间有效
        if (divRef.value.readyState >= 2 && divRef.value.duration > 0) {
          // 限制时间范围在有效区间内
          const targetTime = Math.min(Math.max(0, newTime), divRef.value.duration);
          divRef.value.currentTime = targetTime;
          // 延迟重置seeking标志，避免timeupdate立即覆盖
          setTimeout(() => {
            isSeekingRef.value = false;
          }, 100);
        } else {
          // 如果音频未准备好，等待canplay事件后再设置
          const handleCanPlay = () => {
            if (divRef.value && newTime >= 0) {
              const targetTime = Math.min(Math.max(0, newTime), divRef.value.duration);
              divRef.value.currentTime = targetTime;
              setTimeout(() => {
                isSeekingRef.value = false;
              }, 100);
            }
            divRef.value.removeEventListener('canplay', handleCanPlay);
          };
          divRef.value.addEventListener('canplay', handleCanPlay);
        }
      }
    });

    watch(volume, (value) => {
      if (divRef.value) {
        divRef.value.volume = value;
      }
    });

    // 监听歌曲URL变化，重置音频播放位置
    watch(songUrl, (newUrl, oldUrl) => {
      if (newUrl !== oldUrl && divRef.value) {
        // 只有在真正切换歌曲时才重置时间
        // 延迟执行，避免与changeTime冲突
        setTimeout(() => {
          if (divRef.value && changeTime.value === 0) {
            divRef.value.currentTime = 0;
          }
        }, 100);
      }
    });

    // 获取歌曲链接后准备播放
    function canplay() {
      if (divRef.value) {
        //  记录音乐时长
        proxy.$store.commit("setDuration", divRef.value.duration);
        //  根据isPlay状态决定是否播放
        if (isPlay.value) {
          divRef.value.play().catch(err => {
            console.error("播放失败:", err);
            proxy.$store.commit("setIsPlay", false);
          });
        }
      }
    }
    // 音乐播放时记录音乐的播放位置
    function timeupdate() {
      // 避免在seek过程中更新store，防止覆盖进度条状态
      if (divRef.value && !isSeekingRef.value) {
        proxy.$store.commit("setCurTime", divRef.value.currentTime);
      }
    }
    // 音乐播放结束时触发
    function ended() {
      proxy.$store.commit("setIsPlay", false);
      proxy.$store.commit("setCurTime", 0);
      proxy.$store.commit("setAutoNext", !autoNext.value);
    }



    return {
      songUrl,
      player,
      canplay,
      timeupdate,
      ended,
      muted,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped>
audio {
  display: none;
}
</style>
