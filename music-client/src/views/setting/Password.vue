<template>
  <div class="password-change">
    <div class="form-header">
      <h2>更改密码</h2>
      <p class="form-subtitle">为了您的账户安全，请定期更换密码</p>
    </div>
    
    <div class="form-container">
      <div class="form-card">
        <div class="card-header">
          <svg class="card-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="12" cy="16" r="1" fill="currentColor"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h3>密码设置</h3>
        </div>
        <div class="card-content">
          <el-form ref="passwordForm" :model="form" :rules="rules" class="password-form">
            <el-form-item label="当前密码" prop="oldPassword" class="form-item">
              <el-input 
                type="password" 
                v-model="form.oldPassword" 
                placeholder="请输入当前密码"
                class="custom-input"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword" class="form-item">
              <el-input 
                type="password" 
                v-model="form.newPassword" 
                placeholder="请输入新密码"
                class="custom-input"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword" class="form-item">
              <el-input 
                type="password" 
                v-model="form.confirmPassword" 
                placeholder="请再次输入新密码"
                class="custom-input"
                show-password
              />
            </el-form-item>
            
            <div class="password-tips">
              <div class="tips-header">
                <svg class="tips-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                  <path d="M12 16v-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 8h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>密码安全提示</span>
              </div>
              <ul class="tips-list">
                <li>密码长度至少8位字符</li>
                <li>建议包含大小写字母、数字和特殊字符</li>
                <li>不要使用过于简单或常见的密码</li>
                <li>定期更换密码以保障账户安全</li>
              </ul>
            </div>
            
            <div class="form-actions">
              <el-button @click="clearData()" class="reset-btn">重置</el-button>
              <el-button type="primary" @click="confirm()" class="confirm-btn">确认修改</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, reactive } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { validatePassword, validateUpdatePassword } from "@/enums";

export default defineComponent({
  setup() {
    const store = useStore();
    const { proxy } = getCurrentInstance();
    const { goBack } = mixin();

    const form = reactive({
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
    const userId = computed(() => store.getters.userId);
    const userName = computed(() => store.getters.username);

    const validateCheck = (rule: any, value: any, callback: any) => {
      if (value === "") {
        callback(new Error("确认密码不能为空"));
      } else if (value !== form.newPassword) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const rules = reactive({
      oldPassword: [{ validator: validatePassword, trigger: "blur" }],
      newPassword: [{ validator: validateUpdatePassword, trigger: "blur" }],
      confirmPassword: [{ validator: validateCheck, trigger: "blur" }],
    });

    async function clearData() {
      form.oldPassword = "";
      form.newPassword = "";
      form.confirmPassword = "";
    }

    async function confirm() {
      // 使用Promise包装表单验证，确保验证完成后再继续
      const isValid = await new Promise((resolve) => {
        (proxy.$refs["passwordForm"] as any).validate((valid) => {
          resolve(valid);
        });
      });
      
      if (!isValid) {
        (proxy as any).$message({
          message: "请检查表单信息是否填写正确",
          type: "error",
        });
        return;
      }

      const id = userId.value;
      const username = userName.value;
      const oldPassword = form.oldPassword;
      const password = form.newPassword;

      const result = (await HttpManager.updateUserPassword({id,username,oldPassword,password})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.success) goBack();
    }

    return {
      form,
      clearData,
      confirm,
      rules,
    };
  },
});
</script>

<style scoped>
.password-change {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #f8fafc;
  min-height: 100vh;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 0 20px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 8px 0;
  letter-spacing: -0.025em;
}

.form-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.3s ease;
}

.form-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 24px 16px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-icon {
  width: 24px;
  height: 24px;
  color: white;
  flex-shrink: 0;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: white;
}

.card-content {
  padding: 32px 24px 24px 24px;
}

.password-form {
  margin: 0;
}

.form-item {
  margin-bottom: 24px;
}

.form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
  width: 120px;
  text-align: left;
  display: flex;
  align-items: center;
  height: 48px;
}

.form-item :deep(.el-form-item__content) {
  margin-left: 120px;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  box-shadow: none;
  transition: all 0.2s ease;
  padding: 12px 16px;
  background: #ffffff;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #d1d5db;
  box-shadow: none;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.custom-input :deep(.el-input__inner) {
  font-size: 14px;
  color: #374151;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.password-tips {
  background: #f0f9ff;
  border: 1px solid #e0f2fe;
  border-radius: 12px;
  padding: 20px;
  margin: 24px 0;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.tips-icon {
  width: 20px;
  height: 20px;
  color: #0284c7;
  flex-shrink: 0;
}

.tips-header span {
  font-weight: 600;
  color: #0284c7;
  font-size: 14px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #475569;
  font-size: 14px;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 4px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.reset-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: 1px solid #d1d5db;
  background: white;
  color: #374151;
}

.reset-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
  transform: translateY(-1px);
}

.confirm-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  color: white;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .password-change {
    padding: 16px;
  }
  
  .form-header {
    margin-bottom: 24px;
    padding: 0 8px;
  }
  
  .form-header h2 {
    font-size: 24px;
  }
  
  .form-subtitle {
    font-size: 14px;
  }
  
  .card-header {
    padding: 20px 16px 12px 16px;
  }
  
  .card-content {
    padding: 24px 16px 16px 16px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .reset-btn,
  .confirm-btn {
    width: 100%;
    justify-content: center;
  }
  
  .password-tips {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .password-change {
    padding: 12px;
  }
  
  .form-header h2 {
    font-size: 20px;
  }
  
  .card-header {
    padding: 16px 12px 8px 12px;
  }
  
  .card-content {
    padding: 20px 12px 12px 12px;
  }
}
</style>
