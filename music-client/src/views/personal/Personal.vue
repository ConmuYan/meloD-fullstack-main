<template>
  <div class="personal">
    <div class="personal-info">
      <div class="personal-img" @click="dialogTableVisible = true">
        <el-image fit="cover" :src="attachImageUrl(userPic)" />
      </div>
      <div class="personal-msg">
        <div class="username">{{ personalInfo.username }}</div>
        <div class="user-subtitle">{{ personalInfo.introduction || '这个人很懒，什么也没留下' }}</div>
        
        <!-- 用户信息卡片 -->
        <div class="user-info-cards">
          <div class="info-card">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </div>
            <div class="card-content">
              <div class="card-label">性别</div>
              <div class="card-value">
                {{
                  personalInfo.userSex != null
                      ? (personalInfo.userSex == '1'
                          ? '男'
                          : personalInfo.userSex == '0'
                              ? '女'
                              : '保密')
                       : '未设置'
                 }}
              </div>
            </div>
          </div>
          
          <div class="info-card">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
              </svg>
            </div>
            <div class="card-content">
              <div class="card-label">生日</div>
              <div class="card-value">
                {{
                  personalInfo.birth
                      ? new Date(personalInfo.birth).toLocaleDateString('zh-CN')
                      : '未设置'
                }}
              </div>
            </div>
          </div>
          
          <div class="info-card">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
              </svg>
            </div>
            <div class="card-content">
              <div class="card-label">地区</div>
              <div class="card-value">{{ personalInfo.location || '未设置' }}</div>
            </div>
          </div>
          
          <div class="info-card contact-row">
            <div class="contact-item">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
                </svg>
              </div>
              <div class="card-content">
                <div class="card-label">手机</div>
                <div class="card-value">{{ personalInfo.phoneNum || '未设置' }}</div>
              </div>
            </div>
            
            <div class="contact-item">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                </svg>
              </div>
              <div class="card-content">
                <div class="card-label">邮箱</div>
                <div class="card-value">{{ personalInfo.email || '未设置' }}</div>
              </div>
            </div>
          </div>
          
          <div class="info-card full-width">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h4l4 4 4-4h4c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 9h-2V9h2v2zm0-4h-2V5h2v2z"/>
              </svg>
            </div>
            <div class="card-content">
              <div class="card-label">个人签名</div>
              <div class="card-value signature">{{ personalInfo.introduction || '这个人很懒，什么也没留下' }}</div>
            </div>
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
import { defineComponent, nextTick, ref, computed, watch, reactive, onActivated, getCurrentInstance } from "vue";
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
    const { proxy } = getCurrentInstance() as any;

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
      phoneNum: "",
      email: "",
    });
    
    // 添加加载状态管理，防止重复请求
    const isLoadingUserInfo = ref(false);
    const isLoadingCollection = ref(false);
    const isLoadingPlaylists = ref(false);
    const hasInitialized = ref(false); // 标记是否已经初始化过
    const userId = computed(() => store.getters.userId);
    const userPic = computed(() => store.getters.userPic);
    watch(userPic, () => {
      dialogTableVisible.value = false;
    });

    function goPage() {
      routerManager(RouterName.Setting, { path: RouterName.Setting });
    }
    async function getUserInfo(id) {
      if (isLoadingUserInfo.value) {
        console.log('用户信息正在加载中，跳过重复请求');
        return;
      }
      
      isLoadingUserInfo.value = true;
      try {
        const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
        if (result.success && result.data && result.data.length > 0) {
          const userData = result.data[0];
          personalInfo.username = userData.username || '';
          personalInfo.userSex = userData.sex;
          personalInfo.birth = userData.birth;
          personalInfo.introduction = userData.introduction || '';
          personalInfo.location = userData.location || '';
          personalInfo.phoneNum = userData.phoneNum || '';
          personalInfo.email = userData.email || '';
        } else {
          console.error('获取用户信息失败:', result);
          ElMessage.error('获取用户信息失败');
        }
      } catch (error) {
        console.error('获取用户信息异常:', error);
        ElMessage.error('获取用户信息异常');
      } finally {
        isLoadingUserInfo.value = false;
      }
    }
    // 获取收藏的歌曲（从"我喜欢"歌单或直接收藏的歌曲）
    async function getCollection(id) {
      if (isLoadingCollection.value) {
        console.log('收藏歌曲正在加载中，跳过重复请求');
        return;
      }
      
      isLoadingCollection.value = true;
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
      } finally {
        isLoadingCollection.value = false;
      }
    }
    
    // 获取收藏的歌单
    async function getCollectedPlaylists(id) {
      if (isLoadingPlaylists.value) {
        console.log('收藏歌单正在加载中，跳过重复请求');
        return;
      }
      
      isLoadingPlaylists.value = true;
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
      } finally {
        isLoadingPlaylists.value = false;
      }
    }

    async function changeData() {
      // 强制刷新数据，重置加载状态
      isLoadingCollection.value = false;
      isLoadingPlaylists.value = false;
      
      if (userId.value) {
        await Promise.all([
          getCollection(userId.value),
          getCollectedPlaylists(userId.value)
        ]);
      }
    }

    // 统一的数据加载函数
    async function loadAllData(id) {
      if (hasInitialized.value) {
        console.log('数据已初始化，跳过重复加载');
        return;
      }
      
      if (!id) {
        console.warn('用户未登录，跳过数据加载');
        return;
      }
      
      try {
        hasInitialized.value = true;
        await Promise.all([
          getUserInfo(id),
          getCollection(id),
          getCollectedPlaylists(id)
        ]);
      } catch (error: any) {
        console.error('加载个人页面数据失败:', error);
        hasInitialized.value = false; // 加载失败时重置标记
        // 如果是认证相关错误，清除用户状态并跳转到登录页
        if (error?.response?.status === 400 || error?.response?.status === 401) {
          proxy.$store.commit('setToken', false);
          proxy.$store.commit('clearUserInfo');
          localStorage.removeItem('dataStore');
          proxy.$router.replace('/sign-in');
        }
      }
    }

    nextTick(async () => {
      await loadAllData(userId.value);
    });

    // 当组件被激活时，只在必要时重新加载数据
    onActivated(async () => {
      // 只有在数据未初始化时才加载
      if (userId.value && !hasInitialized.value) {
        await loadAllData(userId.value);
      }
    });

    // 监听用户ID变化
    watch(userId, async (newUserId, oldUserId) => {
      // 只有当用户ID真正发生变化时才重新加载数据
      if (newUserId && newUserId !== oldUserId) {
        hasInitialized.value = false; // 重置初始化标记
        await loadAllData(newUserId);
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
      margin-bottom: 15px;
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

    .user-subtitle {
      font-size: 16px;
      font-weight: 500;
      color: #7f8c8d;
      margin-bottom: 35px;
      font-style: italic;
      line-height: 1.5;
    }
  }
  
  /* 新的卡片式用户信息样式 */
  .user-info-cards {
    margin-top: 30px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
    max-width: 900px;
    margin-left: auto;
    margin-right: auto;
    padding: 0 20px;

    .info-card {
      display: flex;
      align-items: center;
      padding: 24px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 252, 0.9));
      border-radius: 16px;
      border: 1px solid rgba(226, 232, 240, 0.8);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      backdrop-filter: blur(10px);
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), rgba(147, 197, 253, 0.05));
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 40px rgba(59, 130, 246, 0.15);
        border-color: rgba(59, 130, 246, 0.2);
        
        &::before {
          opacity: 1;
        }
        
        .card-icon {
          transform: scale(1.1);
          color: #3b82f6;
        }
      }
      
      &.full-width {
        grid-column: 1 / -1;
        
        .card-content {
          .card-value.signature {
            font-style: italic;
            color: #6b7280;
            line-height: 1.6;
            text-align: left;
          }
        }
      }
      
      &.contact-row {
        grid-column: 1 / -1;
        display: flex;
        gap: 20px;
        
        .contact-item {
          flex: 1;
          display: flex;
          align-items: center;
          
          .card-icon {
            margin-right: 16px;
          }
        }
      }
    }
    
    .card-icon {
      width: 48px;
      height: 48px;
      margin-right: 20px;
      color: #64748b;
      transition: all 0.3s ease;
      flex-shrink: 0;
      
      svg {
        width: 100%;
        height: 100%;
      }
    }
    
    .card-content {
      flex: 1;
      min-width: 0;
      
      .card-label {
        font-size: 14px;
        font-weight: 600;
        color: #64748b;
        margin-bottom: 8px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
      
      .card-value {
        font-size: 16px;
        font-weight: 500;
        color: #1e293b;
        line-height: 1.4;
        word-break: break-word;
      }
    }
    
    /* 响应式设计 */
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      padding: 0 15px;
      gap: 16px;
      
      .info-card {
        padding: 20px;
        
        .card-icon {
          width: 40px;
          height: 40px;
          margin-right: 16px;
        }
        
        .card-content {
          .card-label {
            font-size: 13px;
          }
          
          .card-value {
            font-size: 15px;
          }
        }
        
        &.contact-row {
          flex-direction: column;
          gap: 16px;
          
          .contact-item {
            .card-icon {
              margin-right: 12px;
            }
          }
        }
      }
    }
    
    @media (max-width: 480px) {
      .info-card {
        padding: 16px;
        
        .card-icon {
          width: 36px;
          height: 36px;
          margin-right: 12px;
        }
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
