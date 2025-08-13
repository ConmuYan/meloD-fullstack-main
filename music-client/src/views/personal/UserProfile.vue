<template>
  <div class="user-profile">
    <div class="user-info">
      <div class="user-img">
        <el-image fit="cover" :src="attachImageUrl(userData.avator)"/>
      </div>
      <div class="user-msg">
        <div class="username">{{ userData.username }}</div>
        <div class="user-info-details">
          <div class="info-item">
            <span class="label">性别：</span>
            {{
              userData.userSex != null
                  ? (userData.userSex == '1'
                      ? '男'
                      : userData.userSex == '0'
                          ? '女'
                          : '保密')
                  : '未设置'
            }}
          </div>
          <div class="info-item">
            <span class="label">生日：</span>
            <span>
              {{
                userData.birth
                    ? new Date(userData.birth).toLocaleDateString('zh-CN')
                    : '未设置'
              }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">地区：</span>
            <span>{{ userData.location || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">签名：</span>
            <span>{{ userData.introduction || '这个人很懒，什么也没留下' }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="user-body">
      <song-list :songList="collectSongList" :show="true"></song-list>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import { Plus } from "@element-plus/icons-vue";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";
import { formatDate } from "@/utils";

export default defineComponent({
  name: "UserProfile",
  components: {
    SongList
  },
  setup() {
    const store = useStore();
    const route = useRoute();

    const userData = ref({
      username: "",
      userSex: "",
      birth: "",
      location: "",
      introduction: "",
      avator: ""
    });

    const collectSongList = ref([]);
    const userId = computed(() => store.getters.userId);
    const targetUserId = ref(0);

    // 判断是否是当前用户
    const isCurrentUser = computed(() => {
      return userId.value !== 0 &&
          targetUserId.value !== 0 &&
          targetUserId.value === userId.value;
    });

    // 获取用户信息
    async function getUserInfo(id: number) {
      try {
        const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
        if (result.success && result.data && result.data[0]) {
          Object.assign(userData.value, result.data[0]);
        } else {
          console.error("用户不存在");
          userData.value.username = "用户不存在";
        }
      } catch (error) {
        console.error("获取用户信息失败:", error);
      }
    }

    // 获取收藏的歌曲
    async function getCollection(userId: number) {
      collectSongList.value = [];
      try {
        const result = (await HttpManager.getCollectionOfUser(userId)) as ResponseBody;
        const collectIDList = result.data || [];

        for (const item of collectIDList) {
          if (!item.songId) {
            console.error(`歌曲${item}异常`);
            continue;
          }

          const songResult = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
          if (songResult.success && songResult.data && songResult.data[0]) {
            collectSongList.value.push(songResult.data[0]);
          }
        }
      } catch (error) {
        console.error("获取收藏列表失败:", error);
      }
    }

    // 初始化
    onMounted(() => {
      if (route.params.id) {
        const userIdNum = Number(route.params.id);
        if (!isNaN(userIdNum)) {
          targetUserId.value = userIdNum;
          getUserInfo(userIdNum);
          getCollection(userIdNum);
        }
      }
    });

    return {
      Plus,
      userData,
      collectSongList,
      isCurrentUser,
      attachImageUrl: HttpManager.attachImageUrl,
      formatDate
    };
  }
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.user-profile {
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

.user-info {
  position: relative;
  margin-bottom: 80px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  margin: 0 50px 80px 50px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  
  .user-img {
    height: 180px;
    width: 180px;
    border-radius: 50%;
    border: 4px solid $color-white;
    position: absolute;
    top: -90px;
    left: 50%;
    transform: translateX(-50%);
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
  
  .user-msg {
    text-align: center;
    margin-top: 100px;
    padding: 0 20px;

    .username {
      font-size: 36px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 30px;
      letter-spacing: 1px;
    }
  }
  
  /* 优化的用户信息详情样式 */
  .user-info-details {
    margin-top: 20px;
    font-size: 16px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    max-width: 600px;
    margin-left: auto;
    margin-right: auto;

    .info-item {
      display: flex;
      align-items: center;
      padding: 15px 20px;
      background: rgba(52, 152, 219, 0.05);
      border-radius: 12px;
      border-left: 4px solid #3498db;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(52, 152, 219, 0.1);
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(52, 152, 219, 0.2);
      }

      .label {
        color: #34495e;
        min-width: 60px;
        font-weight: 600;
        margin-right: 10px;
        font-size: 14px;
      }

      span {
        word-break: break-word;
        color: #2c3e50;
        font-weight: 500;
        flex: 1;
      }
    }
  }
  
  .follow-btn {
    position: absolute;
    top: 20px;
    right: 20px;
    background: linear-gradient(135deg, #e74c3c, #c0392b);
    border: none;
    color: white;
    padding: 12px 24px;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
    
    &:hover {
      background: linear-gradient(135deg, #c0392b, #a93226);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
    }
  }
}

@media screen and (min-width: $sm) {
  .user-body {
    padding: 0px 100px;
  }
}

@media screen and (max-width: $sm) {
  .user-info {
    margin: 0 20px 60px 20px;
    padding: 30px 20px;
    
    .user-info-details {
      grid-template-columns: 1fr;
      gap: 15px;
    }
    
    .user-msg .username {
      font-size: 28px;
    }
  }
  
  .follow-btn {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .user-info {
    .user-info-details {
      .info-item {
        flex-direction: column;
        align-items: flex-start;
        text-align: left;
        
        .label {
          margin-bottom: 5px;
          min-width: auto;
        }
      }
    }
  }
}
</style>