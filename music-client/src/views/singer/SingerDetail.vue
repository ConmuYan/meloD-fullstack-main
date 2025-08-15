<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="singer-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <div class="album-info">
        <h2>基本资料</h2>
        <ul>
          <li v-if="songDetails.sex !== 2">性别：{{ getUserSex(songDetails.sex) }}</li>
          <li>生日：{{ getBirth(songDetails.birth) }}</li>
          <li>故乡：{{ songDetails.location }}</li>
        </ul>
      </div>
    </el-aside>
    <el-main class="album-main">
      <h1>{{ songDetails.name }}</h1>
      <p>{{ songDetails.introduction }}</p>
      <song-list :songList="currentSongList"></song-list>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";
import { getBirth } from "@/utils";

export default defineComponent({
  components: {
    SongList,
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const { getUserSex } = mixin();

    const currentSongList = ref([]);
    const songDetails = ref({});
    const loading = ref(false);
    
    // 从路由参数获取歌手ID
    const singerId = computed(() => route.params.id as string);

    // 获取歌手详情信息
    async function getSingerDetails(id: string) {
      if (!id) return;
      
      loading.value = true;
      try {
        // 获取所有歌手信息，然后根据ID过滤
        const allSingersResult = (await HttpManager.getAllSinger()) as ResponseBody;
        const foundSinger = allSingersResult.data.find((s: any) => s.id == id);
        
        if (foundSinger) {
          songDetails.value = foundSinger;
          // 更新store中的歌手详情
          store.commit('setSongDetails', foundSinger);
          
          // 获取歌手的歌曲列表
          const songsResult = (await HttpManager.getSongOfSingerId(id)) as ResponseBody;
          currentSongList.value = songsResult.data;
        } else {
          console.error('未找到指定ID的歌手:', id);
        }
      } catch (error) {
        console.error('获取歌手详情失败:', error);
      } finally {
        loading.value = false;
      }
    }

    // 监听路由参数变化
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          getSingerDetails(newId as string);
        }
      },
      { immediate: true }
    );

    // 如果store中已有数据，优先使用store数据
    const storeDetails = computed(() => store.getters.songDetails);
    watch(
      storeDetails,
      (newDetails) => {
        if (newDetails && Object.keys(newDetails).length > 0) {
          songDetails.value = newDetails;
        }
      },
      { immediate: true }
    );

    return {
      songDetails,
      currentSongList,
      loading,
      attachImageUrl: HttpManager.attachImageUrl,
      getBirth,
      getUserSex,
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

  .singer-img {
    height: 250px;
    width: 250px;
    border-radius: 10%;
  }

  .album-info {
    width: 60%;
    padding-top: 2rem;
    li {
      width: 100%;
      height: 30px;
      line-height: 30px;
    }
  }
}

.album-main {
  p {
    color: rgba(0, 0, 0, 0.5);
    margin: 10px 0 20px 0px;
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
