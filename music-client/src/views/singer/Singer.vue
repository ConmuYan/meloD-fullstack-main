<template>
  <div class="play-list-container">
    <yin-nav :styleList="singerStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <play-list
      :playList="visibleList"
      path="singer-detail"
      :masonry="false"
      :gridColumns="gridColumns"
    ></play-list>
    <div ref="loadMoreTrigger" class="load-more-trigger"></div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { singerStyle } from "@/enums";
import { HttpManager } from "@/api";

// 声明ResponseBody类型
interface ResponseBody {
  success: boolean;
  message: string;
  data: any;
  type?: string;
}

// data
const activeName = ref("全部歌手");
const batchSize = ref(20); // 每次追加的数量
const allPlayList = ref<any[]>([]); // 全量数据
const visibleList = ref<any[]>([]); // 瀑布流已显示的数据

// 固定网格列数：基于窗口宽度
const gridColumns = ref(6);
function updateColumns() {
  const w = window.innerWidth;
  if (w >= 1200) gridColumns.value = 6;        // 桌面端固定8列
  else if (w >= 992) gridColumns.value = 6;     // 大平板/小桌面
  else if (w >= 768) gridColumns.value = 4;     // 平板
  else gridColumns.value = 2;                   // 手机
}

// 获取所有歌手
async function getAllSinger() {
  const result = (await HttpManager.getAllSinger()) as ResponseBody;
  allPlayList.value = result.data || [];
  resetAndFill();
}

getAllSinger();

// 监听筛选变化
function handleChangeView(item) {
  activeName.value = item.name;
  allPlayList.value = [];
  visibleList.value = [];
  if (item.name === "全部歌手") {
    getAllSinger();
  } else {
    getSingerSex(item.type);
  }
}

// 通过性别对歌手分类
async function getSingerSex(sex) {
  const result = (await HttpManager.getSingerOfSex(sex)) as ResponseBody;
  allPlayList.value = result.data || [];
  resetAndFill();
}

// 无限滚动：IntersectionObserver + 滚动兜底
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
        // 如果仍不足以产生滚动，继续预填充
        setTimeout(() => prefillUntilScrollable(), 0);
      }
    });
  }, { root: null, rootMargin: "0px 0px 400px 0px", threshold: 0 });
  if (loadMoreTrigger.value) observer.observe(loadMoreTrigger.value);
  // 首屏不足时预填充
  nextTick(() => prefillUntilScrollable());
  // 保存到实例上以便卸载时移除（使用闭包即可）
  (onBeforeUnmount as any).scrollHandler = handleScroll;
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", updateColumns);
  if (handleScrollRef) window.removeEventListener("scroll", handleScrollRef);
  if (observer && loadMoreTrigger.value) observer.unobserve(loadMoreTrigger.value);
  observer = null;
  handleScrollRef = null;
});
</script>

<style scoped>
.load-more-trigger {
  height: 1px;
}
</style>
