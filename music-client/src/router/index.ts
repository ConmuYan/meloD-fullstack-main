import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Personal from "@/views/personal/Personal.vue";
import OtherUser from "@/views/personal/UserProfile.vue";
const routes: Array<RouteRecordRaw> = [
  {
    path: "/:pathMatch(.*)*",
    redirect: "/404",
  },
  {
    path: "/404",
    component: () => import("@/views/error/404.vue"),
  },
  {
    path: "/",
    name: "yin-container",
    component: () => import("@/views/YinContainer.vue"),
    children: [
      {
        path: "/",
        name: "home",
        component: () => import("@/views/Home.vue"),
      },
      {
        path: "/sign-in",
        name: "sign-in",
        component: () => import("@/views/SignIn.vue"),
      },
      {
        path: "/sign-up",
        name: "sign-up",
        component: () => import("@/views/SignUp.vue"),
      },
      {
        path: "/personal",
        name: "personal",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/views/personal/Personal.vue"),
      },
      // 添加用户主页路由
      {
        path: '/user/:id',
        name: 'UserProfile',
        component: () => import("@/views/personal/UserProfile.vue"),
        props: true,
      },
      {
        path: "/song-sheet",
        name: "song-sheet",
        component: () => import("@/views/song-sheet/SongSheet.vue"),
      },
      {
        path: "/song-sheet-detail/:id",
        name: "song-sheet-detail",
        component: () => import("@/views/song-sheet/SongSheetDetail.vue"),
      },
      {
        path: "/singer",
        name: "singer",
        component: () => import("@/views/singer/Singer.vue"),
      },
      {
        path: "/singer-detail/:id",
        name: "singer-detail",
        component: () => import("@/views/singer/SingerDetail.vue"),
      },
      {
        path: "/lyric/:id",
        name: "lyric",
        component: () => import("@/views/Lyric.vue"),
      },
      {
        path: "/search",
        name: "search",
        component: () => import("@/views/search/Search.vue"),
      },
      {
        path: "/personal-data",
        name: "personal-data",
        component: () => import("@/views/setting/PersonalData.vue"),
      },
      {
        path: "/FPassword",
        name: "FPassword",
        component: ()=> import("@/views/FPassword.vue"),
      },
      {
        path: "/loginByemail",
        name: "loginByemail",
        component: ()=> import("@/views/loginByemail.vue"),
      },
      {
        path: "/setting",
        name: "setting",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/views/setting/Setting.vue"),
        children: [
          {
            path: "/setting/PersonalData",
            name: "personalData",
            meta: {
              requireAuth: true,
            },
            component: () => import("@/views/setting/PersonalData.vue"),
          }
        ]
      },
      {
        path: '/recommendation-playlist',
        name: 'RecommendationPlaylist',
        component: () => import('@/views/RecommendationPlaylist.vue'),
        meta: {
          title: '推荐歌单'
        }
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要登录权限
  if (to.matched.some(record => record.meta.requireAuth)) {
    // 从localStorage获取store状态
    const dataStore = localStorage.getItem('dataStore');
    let isLoggedIn = false;
    
    if (dataStore) {
      try {
        const storeState = JSON.parse(dataStore);
        isLoggedIn = storeState.configure?.token && storeState.user?.userId;
      } catch (error) {
        console.error('解析localStorage数据失败:', error);
        localStorage.removeItem('dataStore');
      }
    }
    
    if (!isLoggedIn) {
      // 未登录，跳转到登录页
      next({
        path: '/sign-in',
        query: { redirect: to.fullPath }
      });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
