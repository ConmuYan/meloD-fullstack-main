<template>
  <yin-login-logo></yin-login-logo>
  <div class="sign enhanced">
    <div class="sign-head">
      <span>修改密码</span>
    </div>
    <el-form ref="signInForm" status-icon :model="registerForm" :rules="rules">
      <el-form-item prop="email">
        <el-input placeholder="邮箱" v-model="registerForm.email" type="email"></el-input>
        <div class="secondary-buttons">
          <el-button :loading="sending" :disabled="sending" @click="sendVerificationCode">发送验证码</el-button>
        </div>
      </el-form-item>
      <el-form-item prop="code">
        <el-input placeholder="验证码" v-model="registerForm.code"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="新密码" v-model="registerForm.password"></el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input type="password" placeholder="确认密码" v-model="registerForm.confirmPassword"></el-input>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button type="primary" :loading="submitting" :disabled="submitting" @click="handleSubmit">提交</el-button>
        <div class="secondary-buttons">
          <el-button @click="handleCancel">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
  
</template>


<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
</style>

<script lang="ts">
import { defineComponent, reactive, getCurrentInstance, ref } from "vue";
import axios from "axios";
import YinLoginLogo from "@/components/layouts/YinLoginLogo.vue";
import mixin from "@/mixins/mixin";
import { RouterName } from "@/enums";

export default defineComponent({
  components: { YinLoginLogo },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager } = mixin();

    const signInForm = ref();
    const sending = ref(false);
    const submitting = ref(false);
    const registerForm = reactive({
      email: "",
      code: "",
      password: "",
      confirmPassword: "",
    });

    const rules = {
      email: [
        { required: true, message: "邮箱不能为空", trigger: "blur" },
        { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] },
      ],
      code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
      password: [{ required: true, message: "请输入新密码", trigger: "blur" }],
      confirmPassword: [
        {
          validator: (_rule, value, callback) => {
            if (!value) return callback(new Error("请确认新密码"));
            if (value !== registerForm.password) return callback(new Error("两次输入的密码不一致"));
            return callback();
          },
          trigger: ["blur", "change"],
        },
      ],
    } as any;

    async function sendVerificationCode() {
      try {
        // 仅校验邮箱字段
        await new Promise((resolve) =>
          (signInForm.value as any).validateField("email", () => resolve(true))
        );
        if (!registerForm.email) return;
        sending.value = true;
        const response = await axios.get("http://localhost:8888/user/sendVerificationCode", {
          params: { email: registerForm.email },
        });
        const msg = response?.data?.message ?? "发送成功";
        const msgType = response?.data?.type ?? "success";
        (proxy as any).$message({ message: msg, type: msgType });
      } catch (error: any) {
        const backendMsg = error?.response?.data?.message;
        (proxy as any).$message({ message: backendMsg || "发送失败，请稍后重试", type: "error" });
        console.error(error);
      } finally {
        sending.value = false;
      }
    }

    async function handleSubmit() {
      let canRun = true;
      (signInForm.value as any).validate((valid: boolean) => {
        if (!valid) canRun = false;
      });
      if (!canRun) return;

      try {
        submitting.value = true;
        const data = { ...registerForm };
        const response = await axios.post("http://localhost:8888/user/resetPassword", data);
        const msg = response?.data?.message ?? "密码修改成功";
        const msgType = response?.data?.type ?? "success";
        (proxy as any).$message({ message: msg, type: msgType });
        routerManager(RouterName.SignIn, { path: RouterName.SignIn });
      } catch (error: any) {
        const backendMsg = error?.response?.data?.message;
        (proxy as any).$message({ message: backendMsg || "重置失败，请稍后重试", type: "error" });
        console.error(error);
      } finally {
        submitting.value = false;
      }
    }

    function handleCancel() {
      routerManager(RouterName.SignIn, { path: RouterName.SignIn });
    }

    return {
      signInForm,
      sending,
      submitting,
      registerForm,
      rules,
      sendVerificationCode,
      handleSubmit,
      handleCancel,
    };
  },
});
</script>
