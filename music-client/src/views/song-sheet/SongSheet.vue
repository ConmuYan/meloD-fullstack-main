<template>
  <div class="play-list-container">
    <yin-nav :styleList="songStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <play-list
      :playList="visibleList"
      path="song-sheet-detail"
      :masonry="false"
      :gridColumns="gridColumns"
    ></play-list>
    <div ref="loadMoreTrigger" class="load-more-trigger"></div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount, nextTick, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { SONGSTYLE } from "@/enums";
import { HttpManager } from "@/api";

// 声明ResponseBody类型
interface ResponseBody {
  success: boolean;
  message: string;
  data: any;
  type?: string;
}

export default defineComponent({
  components: {
    YinNav,
    PlayList,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const activeName = ref("全部歌单");
    const songStyle = ref(SONGSTYLE); // 歌单导航栏类别
    const allPlayList = ref<any[]>([]); // 全量歌单
    const visibleList = ref<any[]>([]); // 已显示数据
    const batchSize = ref(20);

    // 动态列数
    const gridColumns = ref(6);
    function updateColumns() {
      const w = window.innerWidth;
      if (w >= 1200) gridColumns.value = 6;       // 桌面端固定8列
      else if (w >= 992) gridColumns.value = 6;    // 大平板/小桌面
      else if (w >= 768) gridColumns.value = 4;    // 平板
      else gridColumns.value = 2;                  // 手机
    }

    // 获取全部歌单
    async function getSongList() {
      allPlayList.value = (((await HttpManager.getSongList()) as ResponseBody).data) || [];
      resetAndFill();
    }
    // 通过类别获取歌单
    async function getSongListOfStyle(style) {
      allPlayList.value = (((await HttpManager.getSongListOfStyle(style)) as ResponseBody).data) || [];
      resetAndFill();
    }

    // 移除初始化时的getSongList调用，改为通过路由监听器处理
    // 这样可以确保从轮播图进入时正确加载对应分类的数据

    // 获取歌单
    async function handleChangeView(item) {
      // 通过路由跳转来切换分类，确保URL正确反映当前分类
      if (item.name === "全部歌单") {
        await router.push({ path: '/song-sheet' });
      } else {
        await router.push({ 
          path: '/song-sheet', 
          query: { category: item.name }
        });
      }
    }

    // 无限滚动
    const loadMoreTrigger = ref<HTMLElement | null>(null);
    let observer: IntersectionObserver | null = null;
    let handleScrollRef: ((ev: Event) => void) | null = null;

    function fillMore() {
      const current = visibleList.value.length;
      if (current >= allPlayList.value.length) return;
      const next = allPlayList.value.slice(current, current + batchSize.value);
      visibleList.value = visibleList.value.concat(next);
    }

    function canScroll(): boolean {
      const el = document.scrollingElement || document.documentElement;
      return el.scrollHeight > el.clientHeight + 2;
    }

    async function prefillUntilScrollable() {
      let guard = 0;
      while (!canScroll() && visibleList.value.length < allPlayList.value.length && guard < 10) {
        fillMore();
        guard++;
        await nextTick();
      }
    }

    function resetAndFill() {
      visibleList.value = [];
      fillMore();
      nextTick(() => prefillUntilScrollable());
    }

    // 处理路由参数category
    async function handleRouteCategory() {
      const categoryParam = route.query.category as string;
      activeName.value = categoryParam || "全部歌单";
      allPlayList.value = [];
      visibleList.value = [];
      
      try {
        if (!categoryParam || categoryParam === "全部歌单") {
          await getSongList();
        } else {
          await getSongListOfStyle(categoryParam);
        }
      } catch (error) {
        console.error('Error loading song list:', error);
      }
    }

    // 监听路由变化
    watch(
      () => route.fullPath,
      () => {
        // 当路由发生变化时，重新处理
        handleRouteCategory();
      },
      { immediate: true }
    );
    
    // 额外监听category参数变化
    watch(
      () => route.query.category,
      (newCategory, oldCategory) => {
        // 当category参数发生变化时，重新处理
        if (newCategory !== oldCategory) {
          handleRouteCategory();
        }
      }
    );

    onMounted(() => {
      updateColumns();
      window.addEventListener("resize", updateColumns);
      const handleScroll = () => {
        const el = document.scrollingElement || document.documentElement;
        const nearBottom = el.scrollHeight - (el.scrollTop + el.clientHeight) < 400;
        if (nearBottom) {
          fillMore();
        }
      };
      window.addEventListener("scroll", handleScroll, { passive: true });
      handleScrollRef = handleScroll;
      observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            fillMore();
            // 若仍不足以滚动，继续预填充
            setTimeout(() => prefillUntilScrollable(), 0);
          }
        });
      }, { root: null, rootMargin: "0px 0px 400px 0px", threshold: 0 });
      if (loadMoreTrigger.value) observer.observe(loadMoreTrigger.value);
      // 首屏不足时预填充
      nextTick(() => prefillUntilScrollable());
    });

    onBeforeUnmount(() => {
      window.removeEventListener("resize", updateColumns);
      if (handleScrollRef) window.removeEventListener("scroll", handleScrollRef);
      if (observer && loadMoreTrigger.value) observer.unobserve(loadMoreTrigger.value);
      observer = null;
      handleScrollRef = null;
    });
    return {
      activeName,
      songStyle,
      allPlayList,
      visibleList,
      gridColumns,
      handleChangeView,
      loadMoreTrigger,
    };
  },
});
</script>

<style scoped>
.load-more-trigger {
  height: 1px;
}
</style>
