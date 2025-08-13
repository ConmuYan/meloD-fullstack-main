<template>
  <div class="search-song-list">
    <play-list :playList="playList" path="song-sheet-detail"></play-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ComponentInternalInstance, onMounted, ref, computed, watch } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import PlayList from "@/components/PlayList.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    PlayList,
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
    const playList = ref([] as any[]);
    const searchWord = computed(() => store.getters.searchWord);

    // 按标题模糊搜索歌单
    const getSearchList = async (keywords: string) => {
      const kw = String(keywords || '').trim();
      if (!kw) {
        playList.value = [];
        return;
      }
      const result = (await HttpManager.getSongListOfLikeTitle(kw)) as ResponseBody;
      const list = Array.isArray(result?.data) ? result.data : [];
      if (list.length === 0) {
        playList.value = [];
        (proxy as any).$message({
          message: props.noResultText,
          type: "warning",
        });
      } else {
        playList.value = list;
      }
    };

    // 监听全局搜索词
    watch(searchWord, (val) => {
      getSearchList(val);
    });

    onMounted(() => {
      singer.value = route.query.keywords as string;
      const initKw = (route.query.keywords as string) || searchWord.value || "";
      getSearchList(initKw);
    });

    return {
      playList,
    };
  },
});
</script>