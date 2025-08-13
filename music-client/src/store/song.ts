import { Icon } from "@/enums";
import { HttpManager } from "@/api/index";

export default {
  state: {
    /** 音乐信息 */
    songId: "", // 音乐 ID
    songTitle: "", // 歌名
    songUrl: "", // 音乐 URL
    songPic: `/img/songPic/tubiao.jpg`, // 歌曲图片
    singerName: "", //  歌手名
    lyric: [], // 处理后的歌词数据

    /** 音乐播放信息 */
    isPlay: false, // 播放状态
    volume: 0, // 音量
    duration: 0, // 音乐时长
    curTime: 0, // 当前音乐的播放位置
    changeTime: 0, // 指定播放时刻
    autoNext: true, // 用于触发自动播放下一首

    /** 音乐列表信息 */
    currentPlayList: [], // 当前播放列表
    songDetails: null, // 单个歌单信息
    currentPlayIndex: -1, // 当前歌曲在歌曲列表的位置
  },
  getters: {
    songId: (state) => state.songId,
    songTitle: (state) => state.songTitle,
    songUrl: (state) => state.songUrl,
    songPic: (state) => state.songPic,
    singerName: (state) => state.singerName,
    lyric: (state) => state.lyric,

    isPlay: (state) => state.isPlay,
    playBtnIcon: (state) => (state.isPlay ? Icon.ZANTING : Icon.BOFANG),
    volume: (state) => state.volume,
    duration: (state) => state.duration,
    curTime: (state) => state.curTime,
    changeTime: (state) => state.changeTime,
    autoNext: (state) => state.autoNext,

    currentPlayList: (state) => state.currentPlayList,
    songDetails: (state) => state.songDetails,
    currentPlayIndex: (state) => state.currentPlayIndex,
  },
  mutations: {
    setSongId: (state, songId) => {
      state.songId = songId;
    },
    setSongTitle: (state, songTitle) => {
      state.songTitle = songTitle;
    },
    setSongUrl: (state, songUrl) => {
      state.songUrl = songUrl;
    },
    setSongPic: (state, songPic) => {
      state.songPic = songPic;
    },
    setSingerName: (state, singerName) => {
      state.singerName = singerName;
    },
    setAutoNext: (state, autoNext) => {
      state.autoNext = autoNext;
    },
    setLyric: (state, lyric) => {
      state.lyric = lyric;
    },

    setIsPlay: (state, isPlay) => {
      state.isPlay = isPlay;
    },
    setVolume: (state, volume) => {
      state.volume = volume;
    },
    setDuration: (state, duration) => {
      state.duration = duration;
    },
    setCurTime: (state, curTime) => {
      state.curTime = curTime;
    },
    setChangeTime: (state, changeTime) => {
      state.changeTime = changeTime;
    },

    setCurrentPlayList: (state, currentPlayList) => {
      state.currentPlayList = currentPlayList;
    },
    setSongDetails: (state, songDetails) => {
      state.songDetails = songDetails;
    },
    setCurrentPlayIndex: (state, currentPlayIndex) => {
      state.currentPlayIndex = currentPlayIndex;
    },
    setPlayBtnIcon: (state, playBtnIcon) => {
      // 这个mutation应该用于设置播放按钮图标，但实际上图标是通过getter计算的
      // 所以这个mutation可能不需要，或者可以移除
      // 如果需要直接控制播放状态，应该使用setIsPlay
    },
  },
  actions: {
    playMusic: ({ commit }, { id, url, pic, index, songTitle, singerName, lyric, currentSongList, autoPlay = true }) => {
      commit("setSongId", id);
      commit("setSongUrl", url);
      commit("setSongPic", pic);
      commit("setCurrentPlayIndex", index);
      commit("setSongTitle", songTitle);
      commit("setSingerName", singerName);
      commit("setLyric", lyric);
      commit("setCurrentPlayList", currentSongList);
      
      // 重置时间相关状态，确保新歌曲从头开始
      commit("setCurTime", 0);
      commit("setChangeTime", 0);
      commit("setDuration", 0);
      
      commit("setIsPlay", autoPlay);
    },
    async initRandomSong({ commit, dispatch, getters }) {
      // 如果已经有歌曲在播放器中，不需要初始化
      if (getters.songId) return;

      try {
        // 获取所有歌曲
        const result = await HttpManager.getAllSongs() as ResponseBody;
        
        // 检查响应是否成功
        if (!result.success) {
          console.warn("获取歌曲列表失败:", result.message);
          return;
        }
        
        const allSongs = result.data;
        
        // 如果没有歌曲，直接返回
        if (!allSongs || !Array.isArray(allSongs) || allSongs.length === 0) {
          console.warn("没有可用的歌曲");
          return;
        }
        
        // 随机选择一首歌曲
        const randomIndex = Math.floor(Math.random() * allSongs.length);
        const randomSong = allSongs[randomIndex];
        
        // 检查歌曲数据是否完整
        if (!randomSong || !randomSong.id) {
          console.warn("随机歌曲数据不完整:", randomSong);
          return;
        }
        
        // 调用 playMusic action 实际播放歌曲
        dispatch("playMusic", {
          id: randomSong.id,
          url: randomSong.url,
          pic: randomSong.pic,
          index: randomIndex,
          songTitle: randomSong.name,
          singerName: randomSong.singerName || "未知歌手",
          lyric: [],
          currentSongList: allSongs,
          autoPlay: false // 确保第一次加载时不自动播放
        });
      } catch (error) {
        console.error("初始化随机歌曲失败:", error);
      }
    }
  },
};
