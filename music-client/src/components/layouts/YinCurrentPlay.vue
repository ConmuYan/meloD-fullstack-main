<template>
  <transition name="aside-fade">
    <div class="yin-current-play" v-if="showAside">
      <h2 class="title">当前播放</h2>
      <div class="control">共 {{ (currentPlayList && currentPlayList.length) || 0 }} 首</div>
      <ul class="menus">
        <li
          v-for="(item, index) in currentPlayList"
          :class="{ 'is-play': songId === item.id }"
          :key="index"
          @click="playMusic({
            id: item.id,
            url: item.url,
            pic: item.pic,
            index: index,
            name: item.name,
            lyric: item.lyric,
            currentSongList: currentPlayList,
          })">
          <div class="song-item">
            <div class="song-cover">
              <el-image 
                :src="attachImageUrl(item.pic)" 
                class="cover-img" 
                fit="cover"
                :lazy="true"
              >
                <template #error>
                  <div class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="song-info">
              <div class="song-name">{{ getSongTitle(item.name) }}</div>
              <div class="singer-name">{{ getSingerName(item.name) }}</div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </transition>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, onMounted } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { getSongTitle, getSingerName, playMusic } = mixin();

    const songId = computed(() => store.getters.songId); // 音乐 ID
    const currentPlayList = computed(() => store.getters.currentPlayList); // 当前播放
    const showAside = computed(() => store.getters.showAside); // 是否显示侧边栏

    onMounted(() => {
      document.addEventListener('click', (event) => {
        // 只有当点击的不是当前播放列表区域和播放栏的列表按钮时，才关闭侧边栏
        const target = event.target as HTMLElement;
        const currentPlayElement = document.querySelector('.yin-current-play');
        const playBarListButton = document.querySelector('.play-bar .song-edit .yin-icon:last-child');
        
        // 如果点击的是当前播放列表内部或播放栏的列表按钮，不关闭侧边栏
        if (currentPlayElement && (currentPlayElement.contains(target) || target === playBarListButton)) {
          return;
        }
        
        proxy.$store.commit('setShowAside', false);
      }, true)
    })

    return {
      songId,
      currentPlayList,
      showAside,
      getSongTitle,
      getSingerName,
      playMusic,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/yin-current-play.scss";
</style>
