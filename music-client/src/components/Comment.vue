<template>
  <div class="comment">
    <h2 class="comment-title">
      <span>评论</span>
      <span class="comment-desc">共 {{ commentList.length }} 条评论</span>
    </h2>
    <el-input class="comment-input" type="textarea" placeholder="期待您的精彩评论..." :rows="2" v-model="textarea" />
    <el-button class="sub-btn" type="primary" @click="submitComment()">发表评论</el-button>
  </div>
  <ul class="popular">
    <li v-for="(item, index) in commentList" :key="index">
      <!-- 添加头像点击事件 -->
      <el-image class="popular-img" fit="contain" :src="attachImageUrl(item.avator)"
                @click="goUserProfile(item.userId)" />
<!--      <span>{{attachImageUrl(item.avator)}}</span>-->
      <div class="popular-msg">
        <ul>
          <li class="name">{{ item.username }}</li>
          <li class="time">{{ formatDate(item.createTime) }}</li>
          <li class="content">{{ item.content }}</li>
        </ul>
      </div>
      <!--这特么是直接拿到了评论的id-->
      <div class="comment-ctr">
        <div class="support-btn" @click="setSupport(item.id, item.up, userId)">
          <yin-icon :icon="iconList.Support" :style="{ color: item.isSupported ? '#ff0000' : '#000' }"></yin-icon>
          <span>{{ item.up }}</span>
        </div>
<!--      <div ref="up" class="comment-ctr" @click="setSupport(item.id, item.up, userId)">-->
<!--        <div><yin-icon :icon="iconList.Support" ></yin-icon> {{ item.up }}</div>-->
        <el-icon class="delete-btn" v-if="item.userId === userId" @click="deleteComment(item.id, index)"><delete /></el-icon>
      </div>
    </li>
  </ul>
</template>

<script lang="ts" setup>

import { defineProps, getCurrentInstance, ref, toRefs, computed, watch, reactive, onMounted } from "vue";
import { useStore } from "vuex";
import { Delete } from "@element-plus/icons-vue";

import YinIcon from "@/components/layouts/YinIcon.vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { Icon } from "@/enums";
import { formatDate } from "@/utils";
import { useRouter } from "vue-router";

const { proxy } = getCurrentInstance();
const store = useStore();
const { checkStatus } = mixin();
const router = useRouter();
const currentUserId = computed(() => store.getters.userId);

const props = defineProps({
  playId: Number || String, // 歌曲ID 或 歌单ID
  type: Number, // 歌单 1 / 歌曲 0
});

const { playId, type } = toRefs(props);
const textarea = ref(""); // 存放输入内容
const commentList = ref([]); // 存放评论内容
const iconList = reactive({
  Support: Icon.Support,
});

const userId = computed(() => store.getters.userId);
const songId = computed(() => store.getters.songId);

watch(songId, () => {
  getComment(songId.value);
});

onMounted(() => {
  getComment(playId.value);
});

// 获取所有评论
async function getComment(id) {
  try {
    const result = (await HttpManager.getAllComment(type.value, id)) as ResponseBody;
    commentList.value = result.data;
    for (let index = 0; index < commentList.value.length; index++) {
      // 获取评论用户的昵称和头像
      const resultUser = (await HttpManager.getUserOfId(commentList.value[index].userId)) as ResponseBody;
      commentList.value[index].avator = resultUser.data[0].avator;
      commentList.value[index].username = resultUser.data[0].username;

      // 添加点赞状态字段
      const supportResult = (await HttpManager.testAlreadySupport({
        commentId: commentList.value[index].id,
        userId: userId.value
      })) as ResponseBody;
      commentList.value[index].isSupported = supportResult.data
    }
  } catch (error) {
    console.error('[获取所有评论失败]===>', error);
  }
}

async function submitComment() {
  // 1. 检查用户是否登录
  if (!checkStatus()) return;

  // 2. 检查评论内容是否为空
  const content = textarea.value.trim();
  if (!content) {
    (proxy as any).$message({
      message: "评论内容不能为空",
      type: "warning",
    });
    return;
  }

  // 3. 准备评论数据
  let songListId = null;
  let songId = null;
  let nowType = null;

  if (type.value === 1) {
    nowType = 1;
    songListId = `${playId.value}`;
  } else if (type.value === 0) {
    nowType = 0;
    songId = `${playId.value}`;
  }

  // 4. 提交评论
  try {
    const result = (await HttpManager.setComment({
      userId: userId.value,
      content,
      songId,
      songListId,
      nowType
    })) as ResponseBody;

    (proxy as any).$message({
      message: result.message,
      type: result.type,
    });

    if (result.success) {
      textarea.value = "";
      await getComment(playId.value);
    }
  } catch (error) {
    console.error("提交评论失败:", error);
    (proxy as any).$message({
      message: "提交评论失败，请重试",
      type: "error",
    });
  }
}
async function deleteComment(id, index) {
  // 检查用户是否登录
  if (!checkStatus()) return;

  //添加确认对话框
  try {
    await (proxy as any).$confirm('确定删除这条评论吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
  } catch (cancel) {
    return; // 用户取消删除
  }
  // 调用API删除评论
  try {
    const result = (await HttpManager.deleteComment(id)) as ResponseBody;
    (proxy as any).$message({
      message: result.message,
      type: result.type,
    });
    if (result.success) {
      commentList.value.splice(index, 1);
    }
  } catch (error) {
    console.error("删除评论失败:", error);
    (proxy as any).$message({
      message: "删除评论失败，请重试",
      type: "error",
    });
  }
}

// 点赞  还得再查一下
async function setSupport(id, up, userId) {
  if (!checkStatus()) return;

  let result = null;
  let operatorR = null;
  const commentId = id;
  //当然可以这么左 直接在判断的时候 进行点赞或者取消
  const r = (await HttpManager.testAlreadySupport({ commentId, userId })) as ResponseBody;
  (proxy as any).$message({
    message: r.message,
    type: r.type,
    date: r.data,
  });

  if (r.data) {
    up = up - 1;
    operatorR = (await HttpManager.deleteUserSupport({ commentId, userId })) as ResponseBody;
    result = (await HttpManager.setSupport({ id, up })) as ResponseBody;
  } else {
    up = up + 1;
    operatorR = (await HttpManager.insertUserSupport({ commentId, userId })) as ResponseBody;
    result = (await HttpManager.setSupport({ id, up })) as ResponseBody;
  }
  if (result.success && operatorR.success) {
    // proxy.$refs.up[index].children[0].style.color = "#2796dd";
    await getComment(playId.value);
  }
}
// 添加跳转到用户主页的函数
function goUserProfile(userId: number) {
  if (userId === currentUserId.value) {
    // 跳转到个人主页
    router.push({ name: "personal" });
  } else {
    // 跳转到其他用户主页
    router.push({ name: "UserProfile", params: { id: userId } });
  }
}

const attachImageUrl = HttpManager.attachImageUrl;
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

/*评论*/
.comment {
  position: relative;
  margin-bottom: 60px;

  .comment-title {
    height: 50px;
    line-height: 50px;

    .comment-desc {
      font-size: 14px;
      font-weight: 400;
      color: $color-grey;
      margin-left: 10px;
    }
  }

  .comment-input {
    display: flex;
    margin-bottom: 20px;
  }

  .sub-btn {
    position: absolute;
    right: 0;
  }
}

/*热门评论*/
.popular {
  width: 100%;
  > li {
    border-bottom: solid 1px rgba(0, 0, 0, 0.1);
    padding: 15px 0;
    display: flex;
    .popular-img {
      width: 50px;
      height: 50px; /* 添加高度 */
      cursor: pointer;
      transition: transform 0.3s, opacity 0.3s;
      border-radius: 50%; /* 圆形 */
      overflow: hidden; /* 隐藏溢出的部分 */
      border: 2px solid #fff; /* 白色边框 */
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
      &:hover {
        transform: scale(1.05);
        opacity: 0.9;
      }
      /* 确保图片填满容器 */
      ::v-deep .el-image__inner {
        width: 100%;
        height: 100%;
        object-fit: cover; /* 确保完全覆盖容器 */
        object-position: center; /* 居中裁剪 */
      }
    }

    .popular-msg {
      padding: 0 20px;
      flex: 1;
      li {
        width: 100%;
      }
      .time {
        font-size: 0.6rem;
        color: rgba(0, 0, 0, 0.5);
      }
      .name {
        color: rgba(0, 0, 0, 0.5);
      }
      .content {
        font-size: 1rem;
      }
    }

    .comment-ctr {
      display: flex;
      align-items: center;
      width: auto;
      justify-content: flex-end;

      .support-btn {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 5px 8px;
        margin-right: 10px;
        border-radius: 4px;
        transition: all 0.3s;
        .yin-icon {
          margin-right: 5px;
          transition: transform 0.3s;
        }
        span {
          transition: color 0.3s;
        }
        &:hover {
          background-color: #f5f5f5;
          .yin-icon {
            transform: scale(1.1);
          }
          span {
            color: #67757f;
          }
        }
      }

      .delete-btn {
        cursor: pointer;
        padding: 5px;
        margin-left: 10px;
        color: #c0c4cc;
        transition: all 0.3s ease;
        border-radius: 4px;
        &:hover {
          background-color: #f5f5f5;
          .yin-icon {
            transform: scale(1.1);
          }
          span {
            color: #67757f;
          }
        }
      }
    }
  }
}

.icon {
  @include icon(1em);
}
</style>
