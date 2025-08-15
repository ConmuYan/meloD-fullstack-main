<template>
  <el-container class="container">
    <el-header class="header">
      <yin-header></yin-header>
    </el-header>
    <el-main class="main">
      <router-view></router-view>
      <yin-current-play></yin-current-play>
      <announcement v-if="showAnnouncement" :show="true" @hide="hideAnnouncement" />
      <yin-play-bar></yin-play-bar>
      <yin-scroll-top></yin-scroll-top>
      <yin-audio></yin-audio>
    </el-main>
    <el-footer>
      <yin-footer></yin-footer>
    </el-footer>
  </el-container>
</template>

<script>
import { onMounted } from "vue";
import YinHeader from "@/components/layouts/YinHeader.vue";
import YinCurrentPlay from "@/components/layouts/YinCurrentPlay.vue";
import YinPlayBar from "@/components/layouts/YinPlayBar.vue";
import YinScrollTop from "@/components/layouts/YinScrollTop.vue";
import YinFooter from "@/components/layouts/YinFooter.vue";
import YinAudio from "@/components/layouts/YinAudio.vue";
import Announcement from '@/components/ui/Announcement.vue';

export default {
  components: {
    YinHeader,
    YinCurrentPlay,
    YinPlayBar,
    YinScrollTop,
    YinFooter,
    YinAudio,
    Announcement
  },
  data() {
    return {
      isVisible: true
    };
  },
  computed: {
    isLoggedIn() {
      return !!this.$store.getters.userId;
    },
    isOnRegisterPage() {
      return this.$route.path === '/sign-up';
    },
    isOnLoginPage() {
      return this.$route.path === '/sign-in';
    },
    isOnEmailLoginPage() {
      return this.$route.path === '/loginByemail';
    },
    isOnForgotPasswordPage() {
      return this.$route.path === '/FPassword';
    },
    showAnnouncement() {
      return !this.isLoggedIn && !this.isOnRegisterPage && !this.isOnLoginPage && !this.isOnEmailLoginPage && !this.isOnForgotPasswordPage && this.isVisible;
    }
  },
  async mounted() {
    if (localStorage.getItem("dataStore")) {
      this.$store.replaceState(
        Object.assign({}, this.$store.state, JSON.parse(localStorage.getItem("dataStore")))
      );
      this.$store.commit("setIsPlay", false);
      
      // 检查用户登录状态的有效性
      if (this.$store.getters.userId) {
        const isValidSession = await this.validateUserSession();
        if (!isValidSession) {
          // 清除无效的用户状态
          this.$store.commit("setToken", false);
          this.$store.commit("clearUserInfo");
          localStorage.removeItem("dataStore");
          
          // 如果当前在需要登录的页面，跳转到首页
          if (this.$route.meta?.requireAuth) {
            this.$router.replace('/');
          }
        }
      }
    } else {
      this.$store.dispatch("initRandomSong");
    }

    window.addEventListener("beforeunload", () => {
      localStorage.setItem("dataStore", JSON.stringify(this.$store.state));
    });
  },
  
  methods: {
    hideAnnouncement() {
      this.isVisible = false;
    },
    
    // 验证用户session的有效性
     async validateUserSession() {
       try {
         // 尝试获取用户信息来验证session是否有效
         const userId = this.$store.getters.userId;
         if (!userId) return false;
         
         // 导入HttpManager
         const { HttpManager } = await import('@/api');
         const result = await HttpManager.getUserOfId(userId);
         
         // 检查返回结果
         if (result.success && result.data && result.data.length > 0) {
           return true;
         } else {
           console.log('用户信息验证失败:', result.message);
           return false;
         }
       } catch (error) {
         // 如果请求失败（如401、400等），说明session无效
         console.log('Session验证失败:', error);
         // 检查是否是网络错误还是认证错误
         if (error.response && (error.response.status === 401 || error.response.status === 400)) {
           return false;
         }
         // 网络错误等其他情况，暂时认为session有效，避免误判
         return true;
       }
     },
    
    // 检查是否有有效的登录会话（保留原方法作为备用）
    hasValidSession() {
      // 这里可以通过检查cookie、发送验证请求等方式来验证会话有效性
      // 简单起见，我们检查是否有相关的认证信息
      return document.cookie.includes('JSESSIONID') || sessionStorage.getItem('userToken');
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.el-container {
  min-height: calc(100% - 60px);
}
.el-header {
  padding: 0;
}
.el-main {
  padding-left: 0;
  padding-right: 0;
  padding-top: 0px
}


</style>
