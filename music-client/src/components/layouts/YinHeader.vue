<template>
  <div class="yin-header">
    <!--图标-->
    <div class="header-logo" @click="goPage()">
      <img :src="logoImg" alt="logo" class="logo-img" />
      <!-- <span>{{ musicName }}</span> -->
    </div>
    <yin-header-nav class="yin-header-nav" :styleList="headerNavList" :activeName="activeNavName" @click="goPage"></yin-header-nav>
    <!--搜索框-->
    <div class="header-search">
      <el-input placeholder="搜索" :prefix-icon="Search" v-model="keywords" @keyup.enter="goSearch()" />
    </div>
    <!--设置-->
    <yin-header-nav v-if="!token" :styleList="signList" :activeName="activeNavName" @click="goPage"></yin-header-nav>
    <el-dropdown class="user-wrap" v-if="token" trigger="click">
      <el-image class="user" fit="contain" :src="attachImageUrl(userPic)" />
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="(item, index) in menuList" :key="index" @click.stop="goMenuList(item.path)">{{ item.name }}</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, reactive, watch } from "vue";
import { Search } from "@element-plus/icons-vue";
import { useStore } from "vuex";
import YinIcon from "./YinIcon.vue";
import YinHeaderNav from "./YinHeaderNav.vue";
import mixin from "@/mixins/mixin";
import { useRoute } from "vue-router";
import { HEADERNAVLIST, SIGNLIST, MENULIST, Icon, MUSICNAME, RouterName, NavName } from "@/enums";
import { HttpManager } from "@/api";
// eslint-disable-next-line @typescript-eslint/no-var-requires
const logoPath = require("@/assets/images/meloD.png"); // 使用require导入图片

export default defineComponent({
  components: {
    // YinIcon,
    YinHeaderNav,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { changeIndex, routerManager } = mixin();
    const route = useRoute();

    const logoImg = ref(logoPath);
    
    const musicName = ref(MUSICNAME);
    const headerNavList = ref(HEADERNAVLIST); // 左侧导航栏
    const signList = ref(SIGNLIST); // 右侧导航栏
    const menuList = ref(MENULIST); // 用户下拉菜单项
    const iconList = reactive({
      ERJI: Icon.ERJI,
    });
    const keywords = ref("");
    const activeNavName = computed(() => store.getters.activeNavName);
    const userPic = computed(() => store.getters.userPic);
    const token = computed(() => store.getters.token);

    // 参数可选：允许无参调用（点击 LOGO 回首页）
    function goPage(path?: string, name?: string) {
      if (!path && !name) {
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        changeIndex(name);
        routerManager(path, { path });
      }
    }

    function goMenuList(path) {
      if (path == RouterName.SignOut) {
        // 清除用户信息
        proxy.$store.commit("setToken", false);
        proxy.$store.commit("clearUserInfo");
        
        // 清除localStorage中的状态
        localStorage.removeItem("dataStore");
        
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
        
        // 刷新页面以确保announcement组件重新显示
        setTimeout(() => {
          window.location.reload();
        }, 100);
      } else {
        routerManager(path, { path });
      }
    }
    function goSearch() {
      if (keywords.value !== "") {
        // 记录来源页面（首页/歌单/歌手），用于搜索页默认选项卡
        const from = activeNavName.value;
        proxy.$store.commit("setSearchWord", keywords.value);
        // 进入搜索模式：清空顶部导航激活，取消黑色下划线
        proxy.$store.commit("setActiveNavName", "");
        routerManager(RouterName.Search, { path: RouterName.Search, query: { keywords: keywords.value, from } });
      } else {
        (proxy as any).$message({
          message: "搜索内容不能为空",
          type: "error",
        });
      }
    }

    // 路由切换到非搜索页时，自动清空搜索词与输入框，避免残留
    watch(
      () => route.name,
      (name) => {
        if (name !== RouterName.Search) {
          keywords.value = "";
          store.commit("setSearchWord", "");
        }
      },
      { immediate: false }
    );

    return {
      musicName,
      headerNavList,
      signList,
      logoImg,
      menuList,
      iconList,
      keywords,
      activeNavName,
      userPic,
      token,
      Search,
      goPage,
      goMenuList,
      goSearch,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style lang="scss" scoped>

@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

@media screen and (min-width: $sm) {
  .header-logo {
    margin: 0 1rem;
  }
}

@media screen and (max-width: $sm) {
  .header-logo {
    margin: 0 1rem;
    span {
      display: none;
    }
  }
  .header-search {
    display: none;
  }
}

.logo-img {
  height: 25px;  // 高度跟导航栏一致
  width: auto;             // 宽度自适应比例
  object-fit: contain;     // 保持比例不拉伸
  vertical-align: middle;  // 和文字垂直居中
}

.yin-header {
  position: fixed;
  width: 100%;
  height: $header-height;
  line-height: $header-height;
  padding: $header-padding;
  margin: $header-margin;
  background-color: $theme-header-color;
  box-shadow: $box-shadow;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  white-space: nowrap;
  flex-wrap: nowrap;
}

/* LOGO */
.header-logo {
  font-size: $font-size-logo;
  font-weight: bold;
  cursor: pointer;
  .icon {
    @include icon(1.9rem, $color-black);
    vertical-align: middle;
  }
  span {
    margin-left: 1rem;
  }
}

.yin-header-nav {
  flex: 1;
}

/*搜索输入框*/
.header-search {
  margin: 0 20px;
  width: 100%;
  display: flex;
  align-items: center;
  
  ::v-deep .el-input {
    max-width: $header-search-max-width;
    min-width: $header-search-min-width;
    
    .el-input__wrapper {
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 250, 252, 0.95));
      border: 1px solid rgba(52, 152, 219, 0.2);
      border-radius: 25px;
      box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      padding: 8px 20px;
      backdrop-filter: blur(10px);
      
      &:hover {
        border-color: rgba(52, 152, 219, 0.4);
        box-shadow: 0 4px 15px rgba(52, 152, 219, 0.2);
        transform: translateY(-1px);
      }
      
      &.is-focus {
        border-color: #3498db;
        box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1), 0 4px 20px rgba(52, 152, 219, 0.25);
        transform: translateY(-2px);
      }
    }
    
    .el-input__inner {
      color: #2c3e50;
      font-size: 15px;
      font-weight: 500;
      border: none;
      background: transparent;
      padding: 0;
      height: auto;
      line-height: 1.5;
      
      &::placeholder {
        color: #7f8c8d;
        font-weight: 400;
        transition: color 0.3s ease;
      }
      
      &:focus::placeholder {
        color: #bdc3c7;
      }
    }
    
    .el-input__prefix {
      color: #7f8c8d;
      font-size: 16px;
      transition: color 0.3s ease;
    }
    
    &:hover .el-input__prefix,
    &.is-focus .el-input__prefix {
      color: #3498db;
    }
  }
}

/*用户*/
.user-wrap {
  position: relative;
  display: flex;
  align-items: center;

  .user {
    width: $header-user-width;
    height: $header-user-width;
    border-radius: $header-user-radius;
    margin-right: $header-user-margin;
    cursor: pointer;
  }
}
</style>
