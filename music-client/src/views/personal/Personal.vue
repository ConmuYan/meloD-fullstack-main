<template>
  <div class="personal">
    <div class="personal-info">
      <div class="personal-img" @click="dialogTableVisible = true">
        <el-image fit="cover" :src="attachImageUrl(userPic)" />
      </div>
      <div class="personal-msg">
        <div class="username">{{ personalInfo.username }}</div>
<!--        <div class="introduction">{{ personalInfo.introduction }}</div>-->
        <!-- 新增的用户信息展示区域 -->
        <div class="user-info-details">
          <div class="info-item">
            <span class="label">性别：</span>
            <span>
              {{
                personalInfo.userSex != null
                    ? (personalInfo.userSex == '1'
                        ? '男'
                        : personalInfo.userSex == '0'
                            ? '女'
                            : '保密')
                    : '未设置'
              }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">生日：</span>
            <span>
              {{
                personalInfo.birth
                    ? new Date(personalInfo.birth).toLocaleDateString('zh-CN')
                    : '未设置'
              }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">地区：</span>
            <span>{{ personalInfo.location || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">签名：</span>
            <span>{{ personalInfo.introduction || '这个人很懒，什么也没留下' }}</span>
          </div>
        </div>
      </div>
      <el-button class="edit-info" round :icon="Edit" @click="goPage()">修改个人信息</el-button>
    </div>
    <div class="personal-body">
      <!-- 收藏歌单展示区域 -->
      <div class="collected-playlists" v-if="collectedPlaylists.length > 0">
        <h3 class="section-title">我的收藏歌单</h3>
        <play-list :playList="collectedPlaylists" path="song-sheet-detail"></play-list>
      </div>
      
      <!-- 收藏歌曲展示区域 -->
      <div class="collected-songs">
        <h3 class="section-title">我喜欢的歌曲</h3>
        <song-list :songList="collectSongList" :show="true" @changeData="changeData"></song-list>
      </div>
    </div>
    <el-dialog v-model="dialogTableVisible" title="修改头像">
      <upload></upload>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, nextTick, ref, computed, watch, reactive, onActivated } from "vue";
import { useStore } from "vuex";
import { Edit } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import SongList from "@/components/SongList.vue";
import PlayList from "@/components/PlayList.vue";
import Upload from "../setting/Upload.vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName } from "@/enums";

export default defineComponent({
  components: {
    SongList,
    PlayList,
    Upload,
  },
  setup() {
    const store = useStore();

    const { routerManager } = mixin();

    const dialogTableVisible = ref(false);
    const collectSongList = ref([]); // 收藏的歌曲
    const collectedPlaylists = ref([]); // 收藏的歌单
    const personalInfo = reactive({
      username: "",
      userSex: "",
      birth: "",
      location: "",
      introduction: "",
    });
    const userId = computed(() => store.getters.userId);
    const userPic = computed(() => store.getters.userPic);
    watch(userPic, () => {
      dialogTableVisible.value = false;
    });

    function goPage() {
      routerManager(RouterName.Setting, { path: RouterName.Setting });
    }
    async function getUserInfo(id) {
      try {
        const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
        if (result.success && result.data && result.data.length > 0) {
          const userData = result.data[0];
          personalInfo.username = userData.username || '';
          personalInfo.userSex = userData.sex;
          personalInfo.birth = userData.birth;
          personalInfo.introduction = userData.introduction || '';
          personalInfo.location = userData.location || '';
        } else {
          console.error('获取用户信息失败:', result);
          ElMessage.error('获取用户信息失败');
        }
      } catch (error) {
        console.error('获取用户信息异常:', error);
        ElMessage.error('获取用户信息异常');
      }
    }
    // 获取收藏的歌曲（从"我喜欢"歌单或直接收藏的歌曲）
    async function getCollection(id) {
      try {
        // 首先尝试创建"我喜欢"歌单
        await HttpManager.createMyFavoriteSongList(id);
        
        // 获取收藏的歌曲
        const result = (await HttpManager.getSongCollectionOfUser(id)) as ResponseBody;
        if (result.success) {
          collectSongList.value = [];
          for (let item of result.data) {
            if (item.songId) {
              const songResult = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
              if (songResult.success) {
                collectSongList.value.push(songResult.data[0]);
              }
            }
          }
        }
      } catch (error) {
        console.error('获取收藏歌曲失败:', error);
      }
    }
    
    // 获取收藏的歌单
    async function getCollectedPlaylists(id) {
      try {
        const result = (await HttpManager.getSongListCollectionOfUser(id)) as ResponseBody;
        if (result.success && result.data) {
          collectedPlaylists.value = [];
          
          // 获取收藏的歌单详情
          for (let item of result.data) {
            if (item.songListId) {
              const songListResult = (await HttpManager.getSongListOfId(item.songListId)) as ResponseBody;
              if (songListResult.success && songListResult.data) {
                const songList = songListResult.data[0];
                collectedPlaylists.value.push(songList);
              }
            }
          }
        }
      } catch (error) {
        console.error('获取收藏歌单失败:', error);
      }
    }

    async function changeData() {
      await getCollection(userId.value);
      await getCollectedPlaylists(userId.value);
    }

    nextTick(async () => {
      await getUserInfo(userId.value);
      await getCollection(userId.value);
      await getCollectedPlaylists(userId.value);
    });

    // 当组件被激活时重新加载数据
    onActivated(async () => {
      await getCollectedPlaylists(userId.value);
    });

    // 监听用户ID变化
    watch(userId, async (newUserId) => {
      if (newUserId) {
        await getUserInfo(newUserId);
        await getCollection(newUserId);
        await getCollectedPlaylists(newUserId);
      }
    });

    return {
      Edit,
      userPic,
      dialogTableVisible,
      collectSongList,
      collectedPlaylists,
      personalInfo,
      attachImageUrl: HttpManager.attachImageUrl,
      goPage,
      changeData,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.personal {
  padding-top: $header-height + 150px;
  min-height: 100vh;

  &::before {
    content: "";
    background: linear-gradient(135deg, $color-blue-shallow 0%, rgba(135, 206, 250, 0.8) 100%);
    position: absolute;
    top: 0;
    width: 100%;
    height: $header-height + 150px;
    z-index: -1;
  }
}

.personal-info {
  position: relative;
  margin-bottom: 80px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  margin: 0 50px 80px 50px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  
  .personal-img {
    height: 180px;
    width: 180px;
    border-radius: 50%;
    border: 4px solid $color-white;
    position: absolute;
    top: -90px;
    left: 50%;
    transform: translateX(-50%);
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

    /* 新增关键样式 - 确保图像被正确裁剪 */
    overflow: hidden;
    box-sizing: content-box;
    
    &:hover {
      transform: translateX(-50%) scale(1.05);
      box-shadow: 0 12px 35px rgba(0, 0, 0, 0.2);
    }
  }
  
  /* 确保图片元素填满容器 */
  .el-image {
    width: 100%;
    height: 100%;

    /* 深层次定制图片 */
    ::v-deep .el-image__inner {
      object-fit: cover;
      object-position: center center;
    }
  }
  
  .personal-msg {
    text-align: center;
    margin-top: 100px;
    padding: 0 30px;

    .username {
      font-size: 38px;
      font-weight: 800;
      color: #2c3e50;
      margin-bottom: 35px;
      letter-spacing: 1.5px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 50%;
        transform: translateX(-50%);
        width: 60px;
        height: 3px;
        background: linear-gradient(135deg, #3498db, #2980b9);
        border-radius: 2px;
      }
    }

    .introduction {
      font-size: 18px;
      font-weight: 500;
      color: #7f8c8d;
      margin-bottom: 20px;
    }
  }
  
  /* 优化的用户信息详情样式 */
  .user-info-details {
    margin-top: 30px;
    font-size: 16px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
    padding: 0 20px;

    .info-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 20px 28px;
        background: linear-gradient(135deg, rgba(52, 152, 219, 0.06), rgba(255, 255, 255, 0.9));
        border-radius: 16px;
        border: 1px solid rgba(52, 152, 219, 0.12);
        border-left: 4px solid #3498db;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        min-height: 60px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(52, 152, 219, 0.08), transparent);
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover {
        background: linear-gradient(135deg, rgba(52, 152, 219, 0.1), rgba(255, 255, 255, 0.95));
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(52, 152, 219, 0.15);
        border-color: rgba(52, 152, 219, 0.25);
        
        &::before {
          opacity: 1;
        }
      }

      .label {
          color: #2c3e50;
          font-weight: 600;
          font-size: 15px;
          letter-spacing: 0.3px;
          position: relative;
          z-index: 1;
          white-space: nowrap;
          min-width: 60px;
          text-align: left;
          flex-shrink: 0;
        }

        span:not(.label) {
          color: #34495e;
          font-weight: 500;
          line-height: 1.5;
          text-align: right;
          position: relative;
          z-index: 1;
          flex: 1;
          margin-left: 16px;
          word-break: break-word;
        }
    }
    
    /* 特殊处理签名项，让它占据整行 */
    .info-item:last-child {
      grid-column: 1 / -1;
      
      span:not(.label) {
        font-style: italic;
        color: #7f8c8d;
        text-align: left;
        margin-left: 16px;
      }
    }
  }
  
  .edit-info {
    position: absolute;
    top: 20px;
    right: 20px;
    background: linear-gradient(135deg, #3498db, #2980b9);
    border: none;
    color: white;
    padding: 12px 24px;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(52, 152, 219, 0.3);
    
    &:hover {
      background: linear-gradient(135deg, #2980b9, #1f5f8b);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
    }
  }
}

@media screen and (min-width: $sm) {
  .personal-body {
    padding: 0px 100px;
  }
}

@media screen and (max-width: $sm) {
  .personal-info {
    margin: 0 20px 60px 20px;
    padding: 30px 20px;
    
    .user-info-details {
      grid-template-columns: 1fr;
      gap: 18px;
      padding: 0 10px;
      
      .info-item {
          padding: 16px 20px;
          justify-content: flex-start;
          
          .label {
            font-size: 14px;
            margin-right: 10px;
            min-width: 65px;
            text-align: left;
          }
          
          span {
            text-align: center;
            flex: 1;
            display: flex;
            justify-content: center;
          }
        }
      
      /* 在小屏幕上签名项也保持单列布局 */
      .info-item:last-child {
        grid-column: 1;
      }
    }
    
    .personal-msg .username {
      font-size: 28px;
    }
  }
  
  .edit-info {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .personal-info {
    .user-info-details {
      margin-top: 25px;
      
      .info-item {
          flex-direction: column;
          align-items: center;
          padding: 15px 18px;
          
          .label {
            margin-bottom: 8px;
            margin-right: 0;
            font-size: 14px;
            color: #3498db;
            text-align: center;
            width: 100%;
          }
          
          span {
            font-size: 15px;
            line-height: 1.5;
            text-align: center;
            width: 100%;
          }
        }
    }
    
    .personal-msg .username {
      font-size: 24px;
      margin-bottom: 20px;
    }
  }
}

/* 超小屏幕优化 */
@media screen and (max-width: 480px) {
  .personal-info {
    margin: 0 15px 50px 15px;
    padding: 25px 15px;
    
    .user-info-details {
      gap: 15px;
      padding: 0 5px;
      
      .info-item {
          padding: 12px 15px;
          border-radius: 12px;
          
          .label {
            font-size: 13px;
            margin-bottom: 6px;
            margin-right: 0;
            text-align: center;
            width: 100%;
          }
          
          span {
            font-size: 14px;
            text-align: center;
            width: 100%;
          }
        }
    }
  }
}
  .collected-section {
    margin-top: 30px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 15px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  .section-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e0e0e0;
  }

  .collected-content {
    min-height: 200px;
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
  }

  .empty-message {
    width: 100%;
    text-align: center;
    color: #999;
    font-size: 16px;
    padding: 40px 0;
  }
</style>
