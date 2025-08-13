<template>
  <div class="search">
    <yin-nav :styleList="searchNavList" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <component class="search-list" :is="currentView" :noResultText="noResultText"></component>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ComponentInternalInstance, onMounted } from "vue";
import { useRoute } from "vue-router";
import YinNav from "@/components/layouts/YinNav.vue";
import SearchSong from "./SearchSong.vue";
import SearchSongList from "./SearchSongList.vue";
import SearchSinger from "./SearchSinger.vue";

export default defineComponent({
  components: {
    YinNav,
    SearchSong,
    SearchSongList,
    SearchSinger,
  },
  data() {
    return {
      searchNavList: [
        {
          name: "歌曲",
          value: "SearchSong",
        },
        {
          name: "歌单",
          value: "SearchSongList",
        },
        {
          name: "歌手",
          value: "SearchSinger",
        },
      ],
      activeName: "歌曲",
      currentView: "SearchSong",
    };
  },
  computed: {
    noResultText() {
      if (this.activeName === "歌曲") return "暂时没有相关歌曲";
      if (this.activeName === "歌单") return "暂时没有相关歌单";
      if (this.activeName === "歌手") return "暂时没有相关歌手";
      return "暂时没有相关内容";
    },
  },
  setup() {
    const route = useRoute();
    
    return {
      route,
    };
  },
  created() {
    // 在首次渲染前根据来源页设置默认选项卡，避免先挂载"歌曲"组件导致重复空结果提示
    const from = (this.route?.query?.from as string) || "";
    if (from === "歌单") {
      this.activeName = "歌单";
      this.currentView = "SearchSongList";
    } else if (from === "歌手") {
      this.activeName = "歌手";
      this.currentView = "SearchSinger";
    } else {
      this.activeName = "歌曲";
      this.currentView = "SearchSong";
    }
  },
  mounted() {
    // 进入搜索页：清空顶部导航激活，取消黑色下划线
    this.$store.commit('setActiveNavName', '');
  },
  methods: {
    handleChangeView(item) {
      this.activeName = item.name;
      this.currentView = item.value;
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.search {
  margin: auto;
  width: 900px;
  
  .search-list {
    min-height: 480px;
  }
}
</style>
