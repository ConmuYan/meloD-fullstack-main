<template>
  <fullscreen-decoration></fullscreen-decoration>
  <yin-login-logo></yin-login-logo>
  <div class="sign enhanced" style="opacity: 1; visibility: visible;">
    <div class="sign-head">
      <span>邮箱登录</span>
    </div>
    <el-form ref="signInForm" status-icon :model="registerForm" :rules="EmailSignInRules">
      <el-form-item prop="email">
        <enhanced-input placeholder="邮箱" v-model="registerForm.email"></enhanced-input>
      </el-form-item>
      <el-form-item prop="password">
        <enhanced-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn"></enhanced-input>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button type="primary" @click="handleLoginIn">登录</el-button>
         <el-button type="primary" @click="handleLoginCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance } from "vue";
import mixin from "@/mixins/mixin";
import YinLoginLogo from "@/components/layouts/YinLoginLogo.vue";
import EnhancedInput from "@/components/common/EnhancedInput.vue";
import FullscreenDecoration from "@/components/common/FullscreenDecoration.vue";
import { HttpManager } from "@/api";
import { NavName, RouterName, EmailSignInRules } from "@/enums";

export default defineComponent({
  components: {
    YinLoginLogo,
    EnhancedInput,
    FullscreenDecoration,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();

    // 登录
    const registerForm = reactive({
      email: "",
      password: "",
    });

    async function handleLoginCancel() {
       routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    async function handleLoginIn() {
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      try {
        const email = registerForm.email;
        const password = registerForm.password;
        const result = (await HttpManager.signInByemail({email,password})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          proxy.$store.commit("setUserId", result.data[0].id);
          proxy.$store.commit("setUsername", result.data[0].username);
          proxy.$store.commit("setUserPic", result.data[0].avator);
          proxy.$store.commit("setToken", true);
          
          // 立即同步store状态到localStorage，确保路由守卫能正确识别登录状态
          localStorage.setItem("dataStore", JSON.stringify(proxy.$store.state));
          
          changeIndex(NavName.Home);
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
      }
    }

    return {
      registerForm,
      EmailSignInRules,
      handleLoginIn,
      handleLoginCancel,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
</style>
