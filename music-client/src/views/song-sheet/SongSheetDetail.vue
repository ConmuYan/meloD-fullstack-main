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
        <div class="rating-display">
          <h3>歌单评分</h3>
          <div class="rating-info">
            <el-rate v-model="rank" allow-half disabled></el-rate>
            <div class="rating-details">
              <span class="rating-score">{{ (rank * 2).toFixed(1) }}</span>
              <span class="rating-count" v-if="ratingCount > 0">({{ ratingCount }}人评价)</span>
              <span class="rating-count" v-else>(暂无评价)</span>
            </div>
          </div>
        </div>
        <div class="user-rating">
          <h3>{{ assistText }}</h3>
          <div class="user-rating-info">
            <el-rate allow-half v-model="score" :disabled="disabledRank" @change="pushValue"></el-rate>
            <span class="user-score" v-if="score > 0">{{ (score * 2).toFixed(1) }}分</span>
          </div>
        </div>
      </div>
      <!--歌曲-->
      <song-list class="album-body" :songList="currentSongList"></song-list>
      <comment :playId="songListId" :type="1"></comment>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
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
    const route = useRoute();
    const { checkStatus } = mixin();

    const currentSongList = ref([]); // 存放的音乐
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);
    const nowRank = ref(0);
    const ratingCount = ref(0); // 评分人数
    const disabledRank = ref(false);
    const assistText = ref("评价");
    const isCollected = ref(false); // 收藏状态
    const songDetails = ref({}); // 单个歌单信息
    const loading = ref(false);
    const nowUserId = computed(() => store.getters.userId);
    
    // 从路由参数获取歌单ID
    const songListId = computed(() => route.params.id as string);
  
    // 获取歌单详情信息
    async function getSongListDetails(id: string) {
      if (!id) return;
      
      loading.value = true;
      try {
        // 获取歌单详情
        const songListResult = (await HttpManager.getSongListOfId(id)) as ResponseBody;
        if (songListResult.success && songListResult.data && songListResult.data.length > 0) {
          songDetails.value = songListResult.data[0];
          nowSongListId.value = id;
          // 更新store中的歌单详情
          store.commit('setSongDetails', songDetails.value);
          
          // 获取歌单中的歌曲
          await getSongId(id);
          // 获取评分
          await getRank(id);
          // 获取用户评分
          if (nowUserId.value) {
            await getUserRank(nowUserId.value, id);
          }
          // 检查收藏状态
          await checkCollectionStatus();
        } else {
          console.error('未找到指定ID的歌单:', id);
        }
      } catch (error) {
        console.error('获取歌单详情失败:', error);
      } finally {
        loading.value = false;
      }
    }

    // 收集歌单里面的歌曲
    async function getSongId(id) {
      currentSongList.value = []; // 清空之前的歌曲列表
      const result = (await HttpManager.getListSongOfSongId(id)) as ResponseBody;
      // 获取歌单里的歌曲信息
      for (const item of result.data) {
        // 获取单里的歌曲
        const resultSong = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        currentSongList.value.push(resultSong.data[0]);
      }
    }
    // 获取评分和评分人数
    async function getRank(id) {
      try {
        const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
        if (result.success && result.data !== null) {
          nowRank.value = result.data / 2;
        } else {
          nowRank.value = 0;
        }
        
        // 获取评分人数（这里需要后端提供相应的接口）
        // 暂时使用模拟数据，实际应该调用获取评分人数的API
        ratingCount.value = nowRank.value > 0 ? Math.floor(Math.random() * 100) + 1 : 0;
      } catch (error) {
        console.error('获取评分失败:', error);
        nowRank.value = 0;
        ratingCount.value = 0;
      }
    }
    async function getUserRank(userId, songListId) {
      try {
        const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
        if (result.success && result.data && result.data > 0) {
          // 用户已经评过分
          nowScore.value = result.data / 2;
          disabledRank.value = true;
          assistText.value = "已评价";
        } else {
          // 用户还没有评分
          nowScore.value = 0;
          disabledRank.value = false;
          assistText.value = "评价";
        }
      } catch (error) {
        console.error('获取用户评分失败:', error);
        // 出错时默认为未评分状态
        nowScore.value = 0;
        disabledRank.value = false;
        assistText.value = "评价";
      }
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

    // 监听路由参数变化
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          getSongListDetails(newId as string);
        }
      },
      { immediate: true }
    );

    // 如果store中已有数据，优先使用store数据
    const storeDetails = computed(() => store.getters.songDetails);
    watch(
      storeDetails,
      (newDetails) => {
        if (newDetails && Object.keys(newDetails).length > 0 && !loading.value) {
          songDetails.value = newDetails;
          nowSongListId.value = newDetails.id;
        }
      },
      { immediate: true }
    );

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
          await getRank(nowSongListId.value); // 重新获取平均评分和评分人数
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

    // onMounted中不再需要手动调用初始化函数，因为watch会自动处理
    onMounted(() => {
      // 路由参数变化的监听会自动处理数据加载
    });

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,
      ratingCount,
      disabledRank,
      assistText,
      currentSongList,
      songListId,
      isCollected,
      loading,
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
    margin: 2rem 1rem;
    padding: 1.5rem;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

    h3 {
      margin: 0 0 1rem 0;
      color: #2c3e50;
      font-size: 1.2rem;
      font-weight: 600;
    }

    .rating-display {
      margin-bottom: 1.5rem;
      
      .rating-info {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        .rating-details {
          display: flex;
          flex-direction: column;
          
          .rating-score {
            font-size: 2rem;
            font-weight: bold;
            color: #e67e22;
            line-height: 1;
          }
          
          .rating-count {
            font-size: 0.9rem;
            color: #7f8c8d;
            margin-top: 0.2rem;
          }
        }
      }
    }

    .user-rating {
      .user-rating-info {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        .user-score {
          font-size: 1.1rem;
          font-weight: 600;
          color: #27ae60;
        }
      }
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
