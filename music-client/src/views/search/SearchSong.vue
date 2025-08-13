<template>
  <div class="search-song">
    <song-list :songList="currentSongList"></song-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ComponentInternalInstance, onMounted, ref, computed, watch } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    SongList,
  },
  props: {
    noResultText: {
      type: String,
      default: "暂时没有相关内容",
    },
  },
  setup(props) {
    const { proxy } = getCurrentInstance() as ComponentInternalInstance;
    const route = useRoute();
    const store = useStore();
    const singer = ref('');
    const currentSongList = ref([] as any[]);
    const searchWord = computed(() => store.getters.searchWord);

    // 搜索歌曲：后端暂无通用关键词接口，先取全量再前端过滤
    const searchSong = async (keywords: string) => {
      const kw = String(keywords || '').trim().toLowerCase();
      if (!kw) {
        currentSongList.value = [];
        return;
      }
      const result = (await HttpManager.getAllSongs()) as ResponseBody;
      const all = Array.isArray(result?.data) ? result.data : [];
      const list = all.filter((s: any) => {
        const name = String(s.name || '').toLowerCase();
        const intro = String(s.introduction || '').toLowerCase();
        return name.includes(kw) || intro.includes(kw);
      });
      if (list.length === 0) {
        currentSongList.value = [];
        (proxy as any).$message({
          message: props.noResultText,
          type: "warning",
        });
      } else {
        currentSongList.value = list;
      }
    };

    // 监听全局搜索词
    watch(searchWord, (val) => {
      searchSong(val);
    });

    onMounted(() => {
      singer.value = route.query.keywords as string;
      const initKw = (route.query.keywords as string) || searchWord.value || "";
      searchSong(initKw);
    });

    return {
      currentSongList,
    };
  },
});
</script>