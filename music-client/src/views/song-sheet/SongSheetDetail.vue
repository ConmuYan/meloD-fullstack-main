<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <h3 class="album-info">{{ songDetails.title }}</h3>
    </el-aside>
    <el-main class="album-main">
      <h1>简介</h1>
      <p>{{ songDetails.introduction }}</p>
      <!--收藏歌单-->
      <div class="collection-section">
        <el-button 
          :type="isCollected ? 'danger' : 'primary'"
          :icon="isCollected ? 'el-icon-star-on' : 'el-icon-star-off'"
          @click="toggleCollection"
          round
        >
          {{ isCollected ? '取消收藏' : '收藏歌单' }}
        </el-button>
      </div>
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
      <comment :playId="songListId" :type="1"></comment>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance, onMounted } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";
import { ElMessage } from "element-plus";

export default defineComponent({
  components: {
    SongList,
    Comment,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { checkStatus } = mixin();

    const currentSongList = ref([]); // 存放的音乐
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);
    const nowRank = ref(0);
    const disabledRank = ref(false);
    const assistText = ref("评价");
    const isCollected = ref(false); // 收藏状态
    // const evaluateList = ref(["很差", "较差", "还行", "推荐", "力推"]);
    const songDetails = computed(() => store.getters.songDetails); // 单个歌单信息
    const nowUserId = computed(() => store.getters.userId);
  
    nowSongListId.value = songDetails.value.id; // 给歌单ID赋值
  
    // 收集歌单里面的歌曲
    async function getSongId(id) {
      const result = (await HttpManager.getListSongOfSongId(id)) as ResponseBody;
      // 获取歌单里的歌曲信息
      for (const item of result.data) {
        // 获取单里的歌曲
        const resultSong = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        currentSongList.value.push(resultSong.data[0]);
      }
    }
    // 获取评分
    async function getRank(id) {
      const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
      nowRank.value = result.data / 2;
    }
    async function getUserRank(userId, songListId) {
      const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
      nowScore.value = result.data / 2;
      disabledRank.value = true;
      assistText.value = "已评价";
    }
    // 检查收藏状态
    async function checkCollectionStatus() {
      if (!checkStatus()) return;
      try {
        const result = (await HttpManager.getSongListCollectionOfUser(nowUserId.value)) as ResponseBody;
        if (result.success && result.data) {
          isCollected.value = result.data.some(item => item.songListId == nowSongListId.value);
        } else {
          isCollected.value = false;
        }
      } catch (error) {
        console.error(error);
        isCollected.value = false;
      }
    }

    // 切换收藏状态
    async function toggleCollection() {
      if (!checkStatus()) return;
      try {
        let result;
        if (isCollected.value) {
          // 取消收藏
          result = (await HttpManager.deleteSongListCollection(nowUserId.value, nowSongListId.value)) as ResponseBody;
        } else {
          // 添加收藏
          result = (await HttpManager.setCollection({
            userId: nowUserId.value,
            type: 1,
            songId: null,
            songListId: nowSongListId.value
          })) as ResponseBody;
        }
        
        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });
        
        if (result.success) {
          // 重新检查收藏状态以确保同步
          await checkCollectionStatus();
        }
      } catch (error) {
        console.error(error);
        ElMessage.error('操作失败');
      }
    }

    // 提交评分
    async function pushValue(value) {
      if (disabledRank.value || !checkStatus()) return;
      
      // 如果评分为0，不允许提交
      if (!value || value === 0) {
        ElMessage.warning('请选择评分');
        return;
      }

      const songListId = nowSongListId.value;
      var consumerId = nowUserId.value;
      const score = value * 2; // 使用传入的value而不是nowScore.value
      
      try {
        const result = (await HttpManager.setRank({songListId,consumerId,score})) as ResponseBody;
        
        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          await getRank(nowSongListId.value); // 重新获取平均评分
          disabledRank.value = true;
          assistText.value = "已评价";
        } else {
          // 如果评分失败，重置评分值
          nowScore.value = 0;
        }
      } catch (error) {
        console.error('评分提交失败:', error);
        ElMessage.error('评分提交失败，请重试');
        // 重置评分值
        nowScore.value = 0;
      }
    }

    onMounted(() => {
      getUserRank(nowUserId.value, nowSongListId.value);
      getRank(nowSongListId.value); // 获取评分
      getSongId(nowSongListId.value); // 获取歌单里面的歌曲ID
      checkCollectionStatus(); // 检查收藏状态
    });

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,
      disabledRank,
      assistText,
      currentSongList,
      songListId: nowSongListId,
      isCollected,
      attachImageUrl: HttpManager.attachImageUrl,
      pushValue,
      toggleCollection,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;

  .album-img {
    height: 250px;
    width: 250px;
    border-radius: 10%;
  }

  .album-info {
    width: 70%;
    padding-top: 2rem;
  }
}

.album-main {
  h1 {
    font-size: 22px;
  }

  p {
    color: rgba(0, 0, 0, 0.5);
    margin: 10px 0 20px 0px;
  }
  /*收藏歌单*/
  .collection-section {
    margin: 20px 0;
    
    .el-button {
      font-size: 16px;
      padding: 12px 24px;
    }
  }

  /*歌单打分*/
  .album-score {
    display: flex;
    align-items: center;
    margin: 1vw;

    h3 {
      margin: 10px 0;
    }
    span {
      font-size: 60px;
    }
    & > div:last-child {
      margin-left: 10%;
    }
  }

  .album-body {
    margin: 20px 0 20px 0px;
  }
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 400px;
  }
  .album-main {
    min-width: 600px;
    padding-right: 10vw;
    margin-left: 400px;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }
}
</style>
