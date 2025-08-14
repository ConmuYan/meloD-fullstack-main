<template>
  <div class="setting">
    <div class="setting-header">
      <h1>设置</h1>
      <p class="setting-subtitle">管理您的个人信息和账户设置</p>
    </div>
    <div class="setting-container">
      <el-tabs tab-position="left" class="setting-tabs">
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <svg class="tab-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 12C14.7614 12 17 9.76142 17 7C17 4.23858 14.7614 2 12 2C9.23858 2 7 4.23858 7 7C7 9.76142 9.23858 12 12 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20.5899 22C20.5899 18.13 16.7399 15 11.9999 15C7.25991 15 3.40991 18.13 3.40991 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>个人资料</span>
            </div>
          </template>
          <div class="tab-content">
            <Personal-data></Personal-data>
          </div>
        </el-tab-pane>
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <svg class="tab-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="16" r="1" fill="currentColor"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>更改密码</span>
            </div>
          </template>
          <div class="tab-content">
            <Password></Password>
          </div>
        </el-tab-pane>
        <el-tab-pane class="content">
          <template #label>
            <div class="tab-label">
              <svg class="tab-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 15a3 3 0 0 0 0-6 3 3 0 0 0 0 6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1 1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>账号和安全</span>
            </div>
          </template>
          <div class="tab-content">
            <div class="security-section">
              <div class="security-card">
                <div class="security-header">
                  <svg class="security-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M21 12c-1 0-3-1-3-3s2-3 3-3 3 1 3 3-2 3-3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M3 12c1 0 3-1 3-3s-2-3-3-3-3 1-3 3 2 3 3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <h3>危险操作</h3>
                </div>
                <p class="security-description">注销账号将永久删除您的所有数据，此操作不可恢复。</p>
                <el-button type="danger" :icon="Delete" @click="cancelAccount" class="danger-btn">注销账号</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, reactive } from "vue";
import { Delete } from "@element-plus/icons-vue";
import PersonalData from "./PersonalData.vue";
import Password from "./Password.vue";
import { HttpManager } from "@/api";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { RouterName } from "@/enums";

export default defineComponent({
  components: {
    PersonalData,
    Password,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { routerManager } = mixin();

    const userId = computed(() => store.getters.userId);

    async function cancelAccount() {
      const result = (await HttpManager.deleteUser(userId.value)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      routerManager(RouterName.SignIn, { path: RouterName.SignIn });
      proxy.$store.commit("setToken", false);
    }

    return {
      Delete,
      cancelAccount,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.setting {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 80vh;
}

.setting-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px 0;
  background: linear-gradient(135deg,rgb(17, 128, 115) 0%,rgb(193, 125, 219) 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);

  h1 {
    font-size: 2.5rem;
    font-weight: 600;
    margin: 0 0 10px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .setting-subtitle {
    font-size: 1.1rem;
    opacity: 0.9;
    margin: 0;
    font-weight: 300;
  }
}

.setting-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.setting-tabs {
  ::v-deep .el-tabs__header {
    background: #f8fafc;
    margin: 0;
    border-right: 1px solid #e2e8f0;
  }

  ::v-deep .el-tabs__nav-wrap {
    padding: 20px 0;
  }

  ::v-deep .el-tabs__item {
    padding: 0;
    margin-bottom: 8px;
    border: none;
    background: transparent;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(102, 126, 234, 0.1);
    }

    &.is-active {
      background: linear-gradient(135deg,rgba(31, 102, 177, 0.78) 0%,rgb(104, 96, 224) 100%);
      color: white;
      border-radius: 0 25px 25px 0;
      margin-right: -1px;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

      .tab-icon {
        color: white;
      }
    }
  }

  ::v-deep .el-tabs__content {
    padding: 0;
  }

  ::v-deep .el-tab-pane {
    padding: 0;
  }

  ::v-deep .el-tabs__item {
    justify-content: flex-start;
    text-align: left;
    padding: 0;
  }

  ::v-deep .el-tabs__nav {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }
}

.tab-label {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 16px 24px;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  text-align: left;
  width: 100%;

  .tab-icon {
    width: 20px;
    height: 20px;
    margin-right: 12px;
    color: #64748b;
    transition: color 0.3s ease;
    flex-shrink: 0;
  }

  span {
    white-space: nowrap;
    text-align: left;
    flex: 1;
  }
}

.tab-content {
  padding: 40px;
  min-height: 500px;
}

.security-section {
  max-width: 600px;
  margin: 0 auto;
}

.security-card {
  background: #fef2f2;
  border: 2px solid #fecaca;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s ease;

  &:hover {
    border-color: #f87171;
    box-shadow: 0 8px 25px rgba(248, 113, 113, 0.15);
  }

  .security-header {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;

    .security-icon {
      width: 24px;
      height: 24px;
      color: #dc2626;
      margin-right: 12px;
    }

    h3 {
      color: #dc2626;
      font-size: 1.25rem;
      font-weight: 600;
      margin: 0;
    }
  }

  .security-description {
    color: #7f1d1d;
    font-size: 1rem;
    line-height: 1.6;
    margin-bottom: 25px;
  }

  .danger-btn {
    padding: 12px 30px;
    font-size: 1rem;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(220, 38, 38, 0.3);
    }
  }
}

@media screen and (max-width: 768px) {
  .setting {
    padding: 15px;
  }

  .setting-header {
    padding: 20px 15px;
    margin-bottom: 20px;

    h1 {
      font-size: 2rem;
    }

    .setting-subtitle {
      font-size: 1rem;
    }
  }

  .setting-tabs {
    ::v-deep .el-tabs__header {
      width: 100% !important;
    }

    ::v-deep .el-tabs {
      .el-tabs__header {
        position: relative;
      }

      .el-tabs__content {
        margin-left: 0 !important;
      }
    }
  }

  .tab-content {
    padding: 20px;
  }

  .tab-label {
    padding: 12px 16px;
    font-size: 0.9rem;

    .tab-icon {
      width: 18px;
      height: 18px;
      margin-right: 8px;
    }
  }
}

@media screen and (max-width: 480px) {
  .setting-header {
    h1 {
      font-size: 1.75rem;
    }
  }

  .tab-content {
    padding: 15px;
  }

  .security-card {
    padding: 20px;
  }
}
</style>
