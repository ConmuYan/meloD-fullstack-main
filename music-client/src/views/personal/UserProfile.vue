<template>
  <div class="personal">
    <div class="personal-info">
      <div class="personal-img">
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
        </div>
      </div>
    </div>
    <div class="personal-body">
      <!-- 创建歌单展示区域 -->
      <div class="collected-section" v-if="createdPlaylists.length > 0">
        <h3 class="section-title">TA的创建歌单</h3>
        <div class="collected-content">
          <play-list :playList="createdPlaylists" path="song-sheet-detail"></play-list>
        </div>
      </div>

      <!-- 收藏歌单展示区域 -->
      <div class="collected-section" v-if="collectedPlaylists.length > 0">
        <h3 class="section-title">TA的收藏歌单</h3>
        <div class="collected-content">
          <play-list :playList="collectedPlaylists" path="song-sheet-detail"></play-list>
        </div>
      </div>

      <!-- 收藏歌曲展示区域 -->
      <div class="collected-section">
        <h3 class="section-title">TA喜欢的歌曲</h3>
        <div class="collected-content">
          <song-list
              :songList="collectSongList"
              :show="false"
              v-if="collectSongList.length > 0"
          ></song-list>
          <div v-else class="empty-message">暂无喜欢的歌曲</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import SongList from "@/components/SongList.vue";
import PlayList from "@/components/PlayList.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    SongList,
    PlayList,
  },
  setup() {
    const route = useRoute();
    const userId = computed(() => route.params.id as string);

    const personalInfo = ref({
      username: "",
      userSex: "",
      birth: "",
      location: "",
      introduction: "",
      phoneNum: "",
      email: "",
    });

    const collectSongList = ref([]); // 收藏的歌曲
    const collectedPlaylists = ref([]); // 收藏的歌单
    const createdPlaylists = ref([]); // 创建的歌单
    const userPic = ref("");

    // 获取用户信息
    async function getUserInfo(id: string) {
      try {
        const result = (await HttpManager.getUserOfId(id)) as any;
        if (result.success && result.data && result.data.length > 0) {
          const userData = result.data[0];
          personalInfo.value = {
            username: userData.username || '',
            userSex: userData.sex || '',
            birth: userData.birth || '',
            introduction: userData.introduction || '',
            location: userData.location || '',
            phoneNum: userData.phoneNum || '',
            email: userData.email || '',
          };
          userPic.value = userData.avator || '';
        } else {
          console.error('获取用户信息失败:', result);
        }
      } catch (error) {
        console.error('获取用户信息异常:', error);
      }
    }

    // 获取收藏的歌曲
    async function getCollection(id: string) {
      try {
        const result = (await HttpManager.getSongCollectionOfUser(id)) as any;
        if (result.success) {
          collectSongList.value = [];
          for (let item of result.data) {
            if (item.songId) {
              const songResult = (await HttpManager.getSongOfId(item.songId)) as any;
              if (songResult.success && songResult.data.length > 0) {
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
    async function getCollectedPlaylists(id: string) {
      try {
        const result = (await HttpManager.getSongListCollectionOfUser(id, {type : 1})) as any;
        if (result.success && result.data) {
          collectedPlaylists.value = [];
          for (let item of result.data) {
            if (item.songListId) {
              const songListResult = (await HttpManager.getSongListOfId(item.songListId)) as any;
              if (songListResult.success && songListResult.data.length > 0) {
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

    async function getCreatedPlaylists(id) {
      if (!id) return;
      try {
        // 使用与收藏歌单相同的逻辑，但添加type=2 参数
        const result = (await HttpManager.getSongListCollectionOfUser(id, {type : 2})) as ResponseBody;
        if (result.success && result.data) {
          createdPlaylists.value = [];
          // 获取歌单详情
          for (let item of result.data) {
            if (item.songListId) {
              const songListResult = (await HttpManager.getSongListOfId(item.songListId)) as ResponseBody;
              if (songListResult.success && songListResult.data) {
                const songList = songListResult.data[0];
                createdPlaylists.value.push(songList);
              }
            }
          }
        }
      } catch (error) {
        console.error('获取创建歌单失败:', error);
      }
    }

    // 初始化加载数据
    async function loadData() {
      const id = userId.value;
      if (!id) return;

      await Promise.all([
        getUserInfo(id),
        getCollection(id),
        getCollectedPlaylists(id),
        getCreatedPlaylists(id)
      ]);
    }

    onMounted(() => {
      loadData();
    });

    return {
      personalInfo,
      collectSongList,
      collectedPlaylists,
      createdPlaylists,
      userPic,
      attachImageUrl: HttpManager.attachImageUrl,
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
    overflow: hidden;
    box-sizing: content-box;

    .el-image {
      width: 100%;
      height: 100%;

      ::v-deep .el-image__inner {
        object-fit: cover;
        object-position: center center;
      }
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
      position: relative;
      overflow: hidden;
      backdrop-filter: blur(10px);

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

        &.contact-row {
          flex-direction: column;
          gap: 16px;
        }
      }
    }
  }
}

.personal-body {
  padding: 0 50px;
}

.collected-section {
  margin-top: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  & + & {
    margin-top: 25px;
  }
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e0e0e0;
  display: flex;
  align-items: center;

  &::before {
    content: "";
    display: inline-block;
    width: 4px;
    height: 18px;
    background: $color-blue-active;
    border-radius: 2px;
    margin-right: 10px;
  }
}

.empty-message {
  width: 100%;
  text-align: center;
  color: #999;
  font-size: 16px;
  padding: 40px 0;
  font-style: italic;
}

@media screen and (max-width: $sm) {
  .personal-info {
    margin: 0 20px 60px 20px;
    padding: 30px 20px;

    .personal-msg {
      .username {
        font-size: 28px;
      }
    }
  }

  .personal-body {
    padding: 0 20px;
  }

  .collected-section {
    padding: 15px;
  }
}

@media screen and (max-width: 480px) {
  .personal-info {
    margin: 0 15px 50px 15px;
    padding: 25px 15px;

    .personal-msg {
      margin-top: 80px;

      .username {
        font-size: 24px;
      }
    }

    .user-info-cards {
      .info-card {
        padding: 15px;

        .card-icon {
          width: 36px;
          height: 36px;
        }
      }
    }
  }
}
</style>