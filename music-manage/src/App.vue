<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { getCurrentInstance, onMounted, onUnmounted } from "vue";
import { HttpManager } from "@/api";

const { proxy } = getCurrentInstance();

if (sessionStorage.getItem("dataStore")) {
  proxy.$store.replaceState(Object.assign({}, proxy.$store.state, JSON.parse(sessionStorage.getItem("dataStore"))));
}

window.addEventListener("beforeunload", () => {
  sessionStorage.setItem("dataStore", JSON.stringify(proxy.$store.state));
});

// 心跳机制
let heartbeatTimer: number | null = null;
const HEARTBEAT_INTERVAL = 1 * 5 * 1000; // 1分钟发送一次心跳

// 发送心跳
function sendHeartbeat() {
  // 只有在用户已登录的情况下才发送心跳
  if (proxy.$store.getters.isLogin) {
    HttpManager.userHeartbeat().then((result: any) => {
      if (!result.success) {
        console.warn('心跳发送失败:', result.message);
      }
    }).catch((error) => {
      console.warn('心跳发送异常:', error);
    });
  }
}

// 启动心跳定时器
function startHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer);
  }
  heartbeatTimer = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL);
}

// 停止心跳定时器
function stopHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer);
    heartbeatTimer = null;
  }
}

onMounted(() => {
  // 页面加载时启动心跳
  startHeartbeat();
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
      // 页面隐藏时停止心跳
      stopHeartbeat();
    } else {
      // 页面显示时重新启动心跳
      startHeartbeat();
      // 立即发送一次心跳
      sendHeartbeat();
    }
  });
});

onUnmounted(() => {
  stopHeartbeat();
});
</script>
