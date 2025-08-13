<template>
  <div class="song-container">
    <el-image class="song-pic" fit="contain" :src="attachImageUrl(songPic)" />
    <ul class="song-info">
      <li>歌手：
        <span 
          v-for="(singer, index) in singerList" 
          :key="index"
          class="singer-name"
          @click="goToSingerDetail(singer)"
        >
          {{ singer }}<span v-if="index < singerList.length - 1">&</span>
        </span>
      </li>
      <li>歌曲：{{ songTitle }}</li>
    </ul>
  </div>
  <div class="container">
    <div class="lyric-container">
      <div class="song-lyric">
        <transition-group name="lyric-fade">
          <!--有歌词-->
          <ul :style="{ top: lrcTop }" class="has-lyric" v-if="lyricArr.length" key="has-lyric">
            <li 
              v-for="(item, index) in lyricArr" 
              :key="index"
              class="lyric-line"
              @mouseenter="showJumpBtn(index)"
              @mouseleave="hideJumpBtn(index)"
            >
              <span class="lyric-text">{{ item[1] }}</span>
              <yin-icon 
                v-show="hoveredIndex === index"
                class="jump-btn"
                :icon="playIcon"
                @click="jumpToTime(item[0])"
              ></yin-icon>
            </li>
          </ul>
          <!--没歌词-->
          <div v-else class="no-lyric" key="no-lyric">
            <span>暂无歌词</span>
          </div>
        </transition-group>
      </div>
      <comment :playId="songId" :type="0"></comment>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import Comment from "@/components/Comment.vue";
import YinIcon from "@/components/layouts/YinIcon.vue";
import { parseLyric } from "@/utils";
import { HttpManager } from "@/api";
import { ElMessage } from "element-plus";
import { Icon } from "@/enums";

export default defineComponent({
  components: {
    Comment,
    YinIcon,
  },
  setup() {
    const store = useStore();
    const router = useRouter();

    const lrcTop = ref("80px"); // 歌词滑动
    const lyricArr = ref([]); // 当前歌曲的歌词
    const hoveredIndex = ref(-1); // 当前悬停的歌词行索引
    const songId = computed(() => store.getters.songId); // 歌曲ID
    const lyric = computed(() => store.getters.lyric); // 歌词
    const currentPlayList = computed(() => store.getters.currentPlayList); // 存放的音乐
    const currentPlayIndex = computed(() => store.getters.currentPlayIndex); // 当前歌曲在歌曲列表的位置
    const curTime = computed(() => store.getters.curTime);
    const songTitle = computed(() => store.getters.songTitle); // 歌名
    const singerName = computed(() => store.getters.singerName); // 歌手名
    const songPic = computed(() => store.getters.songPic); // 歌曲图片
    
    // 处理歌手列表，支持多人合作歌曲的&分隔符
    const singerList = computed(() => {
      if (!singerName.value) return [];
      return singerName.value.split('&').map(name => name.trim()).filter(name => name);
    });
    watch(songId, () => {
      lyricArr.value = parseLyric(currentPlayList.value[currentPlayIndex.value].lyric);
    });
    // 处理歌词位置及颜色
    watch(curTime, () => {
      if (lyricArr.value.length !== 0) {
        for (let i = 0; i < lyricArr.value.length; i++) {
          if (curTime.value >= lyricArr.value[i][0]) {
            for (let j = 0; j < lyricArr.value.length; j++) {
              (document.querySelectorAll(".has-lyric li") as NodeListOf<HTMLElement>)[j].style.color = "#000";
              (document.querySelectorAll(".has-lyric li") as NodeListOf<HTMLElement>)[j].style.fontSize = "14px";
            }
            if (i >= 0) {
              lrcTop.value = -i * 30 + 50 + "px";
              (document.querySelectorAll(".has-lyric li") as NodeListOf<HTMLElement>)[i].style.color = "#95d2f6";
              (document.querySelectorAll(".has-lyric li") as NodeListOf<HTMLElement>)[i].style.fontSize = "18px";
            }
          }
        }
      }
    });

    lyricArr.value = lyric.value ? parseLyric(lyric.value) : [];

    // 歌手跳转功能
    const goToSingerDetail = async (singerName: string) => {
      try {
        const result = await HttpManager.getSingerOfName(singerName) as ResponseBody;
        if (result.success && result.data && result.data.length > 0) {
          const singer = result.data[0];
          // 设置歌手详情到store
          store.commit('setSongDetails', singer);
          // 跳转到歌手详情页
          router.push({
            name: 'singer-detail',
            params: { id: singer.id }
          });
        } else {
          ElMessage.warning('暂无歌手信息');
        }
      } catch (error) {
        console.error('查询歌手信息失败:', error);
        ElMessage.warning('暂无歌手信息');
      }
    };

    // 歌词悬停功能
    const showJumpBtn = (index: number) => {
      hoveredIndex.value = index;
    };

    const hideJumpBtn = (index: number) => {
      hoveredIndex.value = -1;
    };

    // 歌词时间跳转功能
    const jumpToTime = (time: number) => {
      // 设置播放时间
      store.commit('setChangeTime', time);
      // 确保音乐处于播放状态
      if (!store.getters.isPlay) {
        store.commit('setIsPlay', true);
      }
    };

    return {
      songPic,
      singerName,
      singerList,
      songTitle,
      lrcTop,
      lyricArr,
      songId,
      hoveredIndex,
      playIcon: Icon.BOFANG,
      attachImageUrl: HttpManager.attachImageUrl,
      goToSingerDetail,
      showJumpBtn,
      hideJumpBtn,
      jumpToTime,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.song-container {
  position: fixed;
  top: 120px;
  left: 50px;
  display: flex;
  flex-direction: column;

  .song-pic {
    height: 300px;
    width: 300px;
    border: 4px solid white;
    border-radius: 12px;
  }

  .song-info {
    width: 300px;
    li {
      width: 100%;
      line-height: 40px;
      font-size: 18px;
      padding-left: 10%;
    }
    
    .singer-name {
      color: #409eff;
      cursor: pointer;
      transition: color 0.3s ease;
      
      &:hover {
        color: #66b1ff;
        text-decoration: underline;
      }
    }
  }
}

.lyric-container {
  font-family: $font-family;
  .song-lyric {
    position: relative;
    min-height: 300px;
    padding: 30px 0;
    overflow: auto;
    border-radius: 12px;
    background-color: $color-light-grey;
    display: flex;
    justify-content: center;
    .has-lyric {
      position: absolute;
      transition: all 1s;
      
      .lyric-line {
          width: 100%;
          height: 40px;
          text-align: center;
          font-size: 14px;
          line-height: 40px;
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0 80px;
          
          .lyric-text {
            flex: 1;
            text-align: center;
          }
          
          .jump-btn {
              position: absolute;
              right: 15px;
              opacity: 0.4;
              transition: opacity 0.3s ease;
              cursor: pointer;
              font-size: 10px;
              color: #409eff;
              width: 12px;
              height: 12px;
              
              &:hover {
                opacity: 0.8;
              }
            }
        }
    }
    .no-lyric {
      position: absolute;
      margin: 100px 0;

      span {
        font-size: 18px;
        text-align: center;
      }
    }
  }
}

.lyric-fade-enter,
.lyric-fade-leave-to {
  transform: translateX(30px);
  opacity: 0;
}

.lyric-fade-enter-active,
.lyric-fade-leave-active {
  transition: all 0.3s ease;
}

@media screen and (min-width: $sm) {
  .container {
    padding-top: 30px;
  }
  .lyric-container {
    margin: 0 150px 0px 400px;
  }
}

@media screen and (max-width: $sm) {
  .container {
    padding: 20px;
  }
  .song-container {
    display: none;
  }
}
</style>
