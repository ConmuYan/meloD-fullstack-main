<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(playlist.pic)" />
      <h3 class="album-info">{{ playlist.title }}</h3>
    </el-aside>
    <el-main class="album-main">
      <h1>简介</h1>
      <p>{{ playlist.description || '精选推荐歌单，为您带来优质音乐体验' }}</p>
      
      <!-- 推荐歌单不显示收藏功能 -->
      
      <!--评分-->
      <div class="album-score">
        <div>
          <h3>歌单评分</h3>
          <el-rate v-model="rank" allow-half disabled></el-rate>
        </div>
        <span>{{ rank * 2 }}</span>
        <div>
          <h3>{{ assistText }} {{ score * 2 }}</h3>
          <el-rate allow-half v-model="score" :disabled="disabledRank" @change="pushValue"></el-rate>
        </div>
      </div>
      
      <!--歌曲-->
      <song-list class="album-body" :songList="currentSongList"></song-list>
      <!-- 删除评论组件 -->
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import SongList from '@/components/SongList.vue';
// 删除 Comment 组件的导入
import { HttpManager } from '@/api';
import { ElMessage } from 'element-plus';
import mixin from '@/mixins/mixin';

const route = useRoute();
const router = useRouter();
const store = useStore();
const { checkStatus } = mixin();

const playlist = ref({
  id: '',
  title: '',
  description: '',
  pic: '',
  songCount: 0,
  songs: []
});

const currentSongList = ref([]);
const nowScore = ref(0);
const nowRank = ref(0);
const disabledRank = ref(false);
const assistText = ref('评价');
const singers = ref([]);

const nowUserId = computed(() => store.getters.userId);
const rank = computed(() => nowRank.value);
const score = computed({
  get: () => nowScore.value,
  set: (val) => nowScore.value = val
});

// 获取图片URL
const attachImageUrl = (pic: string) => {
  if (!pic) return '/img/songListPic/default.jpg';
  if (pic.startsWith('http')) return pic;
  return HttpManager.attachImageUrl(pic);
};

// 收集歌单里面的歌曲
async function getSongId(songs: any[]) {
  currentSongList.value = [];
  for (const song of songs) {
    currentSongList.value.push(song);
  }
}

// 获取评分
async function getRank(id: string) {
  try {
    const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
    nowRank.value = result.data / 2;
  } catch (error) {
    console.error('获取评分失败:', error);
    nowRank.value = 0;
  }
}

// 获取用户评分
async function getUserRank(userId: string, songListId: string) {
  try {
    const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
    nowScore.value = result.data / 2;
    disabledRank.value = true;
    assistText.value = '已评价';
  } catch (error) {
    console.error('获取用户评分失败:', error);
    nowScore.value = 0;
    disabledRank.value = false;
    assistText.value = '评价';
  }
}

// 提交评分
async function pushValue(value: number) {
  if (disabledRank.value || !checkStatus()) return;
  
  if (!value || value === 0) {
    ElMessage.warning('请选择评分');
    return;
  }

  const songListId = playlist.value.id;
  const consumerId = nowUserId.value;
  const score = value * 2;
  
  try {
    const result = (await HttpManager.setRank({songListId, consumerId, score})) as ResponseBody;
    
    ElMessage({
      message: result.message,
      type: result.success ? 'success' : 'error'
    });

    if (result.success) {
      await getRank(playlist.value.id);
      disabledRank.value = true;
      assistText.value = '已评价';
    } else {
      nowScore.value = 0;
    }
  } catch (error) {
    console.error('评分提交失败:', error);
    ElMessage.error('评分提交失败，请重试');
    nowScore.value = 0;
  }
}

// 加载推荐歌单数据
const loadPlaylistData = async () => {
  try {
    const currentPlaylist = store.state.currentRecommendationPlaylist;
    if (currentPlaylist) {
      playlist.value = currentPlaylist;
      if (currentPlaylist.songs && currentPlaylist.songs.length > 0) {
        await getSongId(currentPlaylist.songs);
      }
    } else {
      const theme = route.query.theme;
      const res = await HttpManager.getRecommendationThemes();
      if (res && (res as ResponseBody).data && (res as ResponseBody).data.themes) {
        const themes = (res as ResponseBody).data.themes;
        const targetTheme = themes.find((t: any) => t.theme === theme);
        if (targetTheme) {
          playlist.value = targetTheme;
          if (targetTheme.songs && targetTheme.songs.length > 0) {
            await getSongId(targetTheme.songs);
          }
        }
      }
    }
  } catch (error) {
    console.error('加载推荐歌单失败:', error);
  }
};

// 加载歌手数据
const loadSingers = async () => {
  try {
    const res = await HttpManager.getAllSinger();
    if (res && (res as ResponseBody).data) {
      singers.value = (res as ResponseBody).data;
    }
  } catch (error) {
    console.error('加载歌手数据失败:', error);
  }
};

onMounted(async () => {
  await loadPlaylistData();
  await loadSingers();
  
  if (playlist.value.id) {
    if (nowUserId.value) {
      await getUserRank(nowUserId.value, playlist.value.id);
    }
    await getRank(playlist.value.id);
  }
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
  width: 300px;
  background: #fafafa;
  
  .album-img {
    width: 200px;
    height: 200px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
  }
  
  .album-info {
    margin-top: 20px;
    font-size: 16px;
    color: #333;
    text-align: center;
    padding: 0 20px;
    word-wrap: break-word;
  }
}

.album-main {
  padding: 20px;
  
  h1 {
    margin-bottom: 10px;
    color: #333;
  }
  
  p {
    margin-bottom: 20px;
    color: #666;
    line-height: 1.6;
  }
  
  .album-score {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    
    > div {
      margin-right: 30px;
      
      h3 {
        margin-bottom: 10px;
        font-size: 14px;
        color: #666;
      }
    }
    
    > span {
      font-size: 24px;
      font-weight: bold;
      color: #ff6b6b;
      margin-right: 30px;
    }
  }
  
  .album-body {
    margin-bottom: 30px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .album-slide {
    width: 100%;
    padding: 20px;
    
    .album-img {
      width: 150px;
      height: 150px;
    }
  }
  
  .album-main {
    padding: 15px;
    
    .album-score {
      flex-direction: column;
      align-items: flex-start;
      
      > div, > span {
        margin-right: 0;
        margin-bottom: 15px;
      }
    }
  }
}
</style>