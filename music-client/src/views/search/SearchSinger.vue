<template>
  <div class="search-singer">
    <play-list :playList="playList" path="singer-detail"></play-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted, getCurrentInstance } from "vue";
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
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const playList = ref([]);
    const searchWord = computed(() => store.getters.searchWord);

    watch(searchWord, (value) => {
      getSearchSinger(value);
    });

    async function getSearchSinger(value: string) {
      if (!value) {
        playList.value = [];
        return;
      }
      // 后端暂无按姓名检索歌手接口，先取全量再前端过滤
      const result = (await HttpManager.getAllSinger()) as ResponseBody;
      const all = Array.isArray(result?.data) ? result.data : [];
      const keywords = String(value).trim().toLowerCase();
      const list = all.filter((s: any) => String(s.name || "").toLowerCase().includes(keywords));
      if (list.length === 0) {
        playList.value = [];
        (proxy as any).$message({
          message: props.noResultText,
          type: "warning",
        });
      } else {
        playList.value = list;
      }
    }

    onMounted(() => {
      getSearchSinger((proxy as any)?.$route?.query?.keywords || "");
    });

    return {
      playList,
    };
  },
});
</script>
