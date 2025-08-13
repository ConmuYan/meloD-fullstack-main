<template>
  <div class="play-bar" :class="{ show: !toggle }">
    <div class="fold" :class="{ turn: !toggle }">
      <yin-icon :icon="iconList.ZHEDIE" @click="toggle = !toggle"></yin-icon>
    </div>
    <!--播放进度：使用重构的进度条组件-->
    <yin-progress-bar
      ref="progressBarRef"
      class="progress"
      :current-time="curTime"
      :duration="duration"
      :song-id="songId"
      :disabled="!songId"
      @seek="handleSeek"
    />
    <div class="control-box">
      <div class="info-box">
        <!--歌曲图片-->
      <div @click="goPlayerPage">
         <el-image :src="attachImageUrl(songPic)" class="song-bar-img" fit="contain"/>
      </div>
        <!--播放开始结束时间-->
        <div v-if="songId">
          <div class="song-info">{{ this.songTitle }} - {{ this.singerName }}</div>
          <div class="time-info">{{ startTime }} / {{ endTime }}</div>
        </div>
      </div>
      <div class="song-ctr">
        <yin-icon class="yin-play-show" :class="{ 'danqu-size': playState === playStateList[2] }" :icon="playStateList[playStateIndex]" @click="changePlayState"></yin-icon>
        <!--上一首-->
        <yin-icon class="yin-play-show" :icon="iconList.SHANGYISHOU" @click="prev"></yin-icon>
        <!--播放-->
        <yin-icon :icon="playBtnIcon" @click="togglePlay"></yin-icon>
        <!--下一首-->
        <yin-icon class="yin-play-show" :icon="iconList.XIAYISHOU" @click="next"></yin-icon>
        <!--音量-->
        <el-dropdown class="yin-play-show" trigger="hover">
          <yin-icon v-if="volume !== 0" :icon="iconList.YINLIANG" @click.stop="toggleMute"></yin-icon>
          <yin-icon v-else :icon="iconList.JINGYIN" @click.stop="toggleMute"></yin-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-slider class="yin-slider" style="height: 150px; margin: 10px 0" v-model="volume"
                         :vertical="true"></el-slider>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="song-ctr song-edit">
        <!--收藏-->
        <yin-icon
            class="yin-play-show"
            :class="{ active: isCollection }"
            :icon="isCollection ? iconList.like : iconList.dislike"
            @click="changeCollection"
        ></yin-icon>
        <!--下载-->
        <yin-icon
            class="yin-play-show"
            :icon="iconList.download"
            @click="
            downloadMusic({
              songUrl,
              songName: singerName + '-' + songTitle,
            })
          "
        ></yin-icon>
        <!--歌曲列表-->
        <yin-icon :icon="iconList.LIEBIAO" @click="changeAside"></yin-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, onMounted, ref, watch} from "vue";
import {mapGetters, useStore} from "vuex";
import mixin from "@/mixins/mixin";
import YinIcon from "./YinIcon.vue";
import YinProgressBar from "@/components/common/YinProgressBar.vue";
import {HttpManager} from "@/api";
import {formatSeconds} from "@/utils";
import {Icon, RouterName} from "@/enums";

export default defineComponent({
  components: {
    YinIcon,
    YinProgressBar,
  },
  setup() {
    const {proxy} = getCurrentInstance();
    const store = useStore();
    const {routerManager, playMusic, checkStatus, downloadMusic} = mixin();
    async function playRandomSong(autoPlay = true) {
      // 从当前播放列表中随机选择一首歌曲
      let currentPlayList = store.getters.currentPlayList;

      // 如果当前播放列表为空，尝试从全局歌曲列表中获取
      if (!currentPlayList || currentPlayList.length === 0) {
        try {
          const data = await HttpManager.getAllSongs();
          // 添加类型检查，确保data是数组且不为空
          if (data && Array.isArray(data) && data.length > 0) {
            currentPlayList = data;
            // 更新当前播放列表
            store.commit("setCurrentPlayList", currentPlayList);
          }
        } catch (error) {
          console.error('获取全局歌曲列表失败:', error);
        }
      }

      if (currentPlayList && currentPlayList.length > 0) {
        const randomIndex = Math.floor(Math.random() * currentPlayList.length);
        const song = currentPlayList[randomIndex];

        // 调用playMusic方法播放歌曲
        playMusic({
          id: song.id,
          url: song.url,
          pic: song.pic,
          index: randomIndex,
          name: song.name,
          lyric: song.lyric,
          currentSongList: currentPlayList,
          autoPlay,
          });
      } else {
        // 如果没有播放列表，显示提示
        (proxy as any).$message({
          message: "暂无歌曲可播放",
          type: "warning",
        });
      }
    }

    const isCollection = ref(false); // 是否收藏

    const userIdVO = computed(() => store.getters.userId);
    const songIdVO = computed(() => store.getters.songId);
    const token = computed(() => store.getters.token);

    watch(songIdVO, () => {
      initCollection();
    });
    watch(token, (value) => {
      if (!value) isCollection.value = false;
    });


    async function initCollection() {
      if (!checkStatus(false)) return;

      const userId = userIdVO.value;
      const songId = songIdVO.value;
      isCollection.value = ((await HttpManager.isCollection({userId, songId})) as ResponseBody).data;
    }

    async function changeCollection() {
      if (!checkStatus()) return;

      const userId = userIdVO.value;
      const songId = songIdVO.value;

      const result = isCollection.value
          ? ((await HttpManager.deleteCollection(userIdVO.value, songIdVO.value)) as ResponseBody)
          : ((await HttpManager.setCollection({userId, type: 0, songId, songListId: null})) as ResponseBody);
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.data == true || result.data == false) isCollection.value = result.data;
    }

    onMounted(() => {
      if (songIdVO.value) initCollection();
    });

    const progressBarRef = ref(null);

    return {
      isCollection,
      playMusic,
      routerManager,
      checkStatus,
      attachImageUrl: HttpManager.attachImageUrl,
      changeCollection,
      downloadMusic,
      progressBarRef
    };
  },
  data() {
    return {
      startTime: "00:00",
      endTime: "00:00",
      toggle: true,
      volume: 50,
      prevVolume: 50, // 记录静音前的音量，用于恢复
      playState: Icon.XUNHUAN,
      // 播放模式：列表循环 -> 随机 -> 单曲循环
      playStateList: [Icon.XUNHUAN, Icon.LUANXU, Icon.DANQU],
      playStateIndex: 0,
      iconList: {
        download: Icon.XIAZAI,
        ZHEDIE: Icon.ZHEDIE,
        SHANGYISHOU: Icon.SHANGYISHOU,
        XIAYISHOU: Icon.XIAYISHOU,
        YINLIANG: Icon.YINLIANG1,
        JINGYIN: Icon.JINGYIN,
        LIEBIAO: Icon.LIEBIAO,
        dislike: Icon.Dislike,
        like: Icon.Like,
      },
    };
  },
  computed: {
    ...mapGetters([
      "userId",
      "isPlay", // 播放状态
      "playBtnIcon", // 播放状态的图标
      "songId", // 音乐id
      "songUrl", // 音乐地址
      "songTitle", // 歌名
      "singerName", // 歌手名
      "songPic", // 歌曲图片
      "curTime", // 当前音乐的播放位置
      "duration", // 音乐时长
      "currentPlayList",
      "currentPlayIndex", // 当前歌曲在歌曲列表的位置
      "showAside", // 是否显示侧边栏
      "autoNext", // 用于触发自动播放下一首
    ]),
  },
  watch: {
    // 播放状态变化时的处理（图标通过getter自动计算，无需手动设置）
    isPlay(value) {
      // 图标已通过store的getter自动计算，这里可以添加其他需要的逻辑
    },
    volume() {
      this.$store.commit("setVolume", this.volume / 100);
    },
    // 播放时间的开始和结束
    curTime() {
      this.startTime = formatSeconds(this.curTime);
      this.endTime = formatSeconds(this.duration);
    },
    
    // 自动播放结束后的行为：根据模式选择
    autoNext() {
      if (this.playState === Icon.DANQU) {
        this.repeatCurrent();
      } else {
        this.next();
      }
    },
  },
  mounted() {
    window.addEventListener('keydown', this.onGlobalKeydown);
  },
  beforeUnmount() {
    window.removeEventListener('keydown', this.onGlobalKeydown);
  },
  methods: {
    changeAside() {
      this.$store.commit("setShowAside", !this.showAside);
    },
    // 控制音乐播放 / 暂停
    togglePlay() {
      this.$store.commit("setIsPlay", this.isPlay ? false : true);
    },
    // 全局键盘事件：空格键播放/暂停
    onGlobalKeydown(e) {
      const target = e.target;
      const tag = target && target.tagName ? target.tagName.toLowerCase() : '';
      const isEditable = (target && (target.isContentEditable || tag === 'input' || tag === 'textarea' || tag === 'select'));
      if (isEditable) return;
      if (e.code === 'Space' || e.key === ' ') {
        e.preventDefault();
        this.togglePlay();
      }
    },
    // 点击音量图标：静音/取消静音
    toggleMute() {
      if (this.volume === 0) {
        const restore = this.prevVolume && this.prevVolume > 0 ? this.prevVolume : 50;
        this.volume = restore;
      } else {
        this.prevVolume = this.volume;
        this.volume = 0;
      }
    },
    // 处理进度条跳转
    handleSeek(time) {
      this.$store.commit("setChangeTime", time);
    },
    changePlayState() {
      this.playStateIndex = this.playStateIndex >= this.playStateList.length - 1 ? 0 : ++this.playStateIndex;
      this.playState = this.playStateList[this.playStateIndex];
    },
    // 上一首
    prev() {
      // 列表保护
      const len = this.currentPlayList?.length || 0;
      if (len === 0) return;
      if (this.playState === Icon.LUANXU) {
        // 随机模式：选择与当前不同的索引，并做边界处理
        let playIndex = Math.floor(Math.random() * len);
        if (len > 1 && playIndex === this.currentPlayIndex) {
          playIndex = (playIndex + 1) % len;
        }
        this.$store.commit("setCurrentPlayIndex", playIndex);
        const song = this.currentPlayList[playIndex];
        if (song && song.url) this.toPlay(song.url);
      } else if (this.currentPlayIndex !== -1 && this.currentPlayList.length > 1) {
        let targetIndex = this.currentPlayIndex > 0
          ? this.currentPlayIndex - 1
          : this.currentPlayList.length - 1;
        this.$store.commit("setCurrentPlayIndex", targetIndex);
        const song = this.currentPlayList[targetIndex];
        if (song && song.url) this.toPlay(song.url);
      }
    },
    // 下一首
    next() {
      const len = this.currentPlayList?.length || 0;
      if (len === 0) return;
      if (this.playState === Icon.LUANXU) {
        let playIndex = Math.floor(Math.random() * len);
        if (len > 1 && playIndex === this.currentPlayIndex) {
          playIndex = (playIndex + 1) % len;
        }
        this.$store.commit("setCurrentPlayIndex", playIndex);
        const song = this.currentPlayList[playIndex];
        if (song && song.url) this.toPlay(song.url);
      } else if (this.currentPlayIndex !== -1 && this.currentPlayList.length > 1) {
        let targetIndex = this.currentPlayIndex < this.currentPlayList.length - 1
          ? this.currentPlayIndex + 1
          : 0;
        this.$store.commit("setCurrentPlayIndex", targetIndex);
        const song = this.currentPlayList[targetIndex];
        if (song && song.url) this.toPlay(song.url);
      }
    },
    // 单曲循环：重新播放当前歌曲
    repeatCurrent() {
      const len = this.currentPlayList?.length || 0;
      if (len === 0) return;
      const idx = this.currentPlayIndex;
      if (idx < 0 || idx >= len) return;
      // 不依赖切换 URL，直接将播放时间置为 0 并开始播放
      this.$store.commit("setChangeTime", 0);
      this.$store.commit("setIsPlay", true);
    },
    // 选中播放
    toPlay(url) {
      if (url && url !== this.songUrl) {
        const song = this.currentPlayList[this.currentPlayIndex];
        this.playMusic({
          id: song.id,
          url,
          pic: song.pic,
          index: this.currentPlayIndex,
          name: song.name,
          lyric: song.lyric,
          currentSongList: this.currentPlayList,
          autoPlay: true,
        });
      }
    },
    goPlayerPage() {
      this.routerManager(RouterName.Lyric, {path: `${RouterName.Lyric}/${this.songId}`});
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/yin-play-bar.scss";
.danqu-size { font-size: 1.4em; }
</style>

