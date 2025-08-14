<template>
  <div class="personal-data">
    <div class="form-header">
      <h2>个人资料</h2>
      <p class="form-subtitle">更新您的个人信息</p>
    </div>
    
    <div class="form-container">
      <el-form ref="updateForm" :model="registerForm" :rules="SignUpRules" class="personal-form">
        <div class="form-grid">
          <!-- 基本信息卡片 -->
          <div class="form-card">
            <div class="card-header">
              <svg class="card-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 12C14.7614 12 17 9.76142 17 7C17 4.23858 14.7614 2 12 2C9.23858 2 7 4.23858 7 7C7 9.76142 9.23858 12 12 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20.5899 22C20.5899 18.13 16.7399 15 11.9999 15C7.25991 15 3.40991 18.13 3.40991 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <h3>基本信息</h3>
            </div>
            <div class="card-content">
              <el-form-item prop="username" label="用户名" class="form-item">
                <el-input v-model="registerForm.username" placeholder="请输入用户名" class="custom-input"></el-input>
              </el-form-item>
              <el-form-item label="性别" class="form-item">
                <el-radio-group v-model="registerForm.sex" class="gender-group">
                  <el-radio :label="0" class="gender-radio">女</el-radio>
                  <el-radio :label="1" class="gender-radio">男</el-radio>
                  <el-radio :label="2" class="gender-radio">保密</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item prop="birth" label="生日" class="form-item">
                <el-date-picker 
                  type="date" 
                  placeholder="选择生日" 
                  v-model="registerForm.birth" 
                  class="custom-date-picker"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>
            </div>
          </div>

          <!-- 联系信息卡片 -->
          <div class="form-card">
            <div class="card-header">
              <svg class="card-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <h3>联系方式</h3>
            </div>
            <div class="card-content">
              <el-form-item prop="phoneNum" label="手机号码" class="form-item">
                <el-input placeholder="请输入手机号码" v-model="registerForm.phoneNum" class="custom-input"></el-input>
              </el-form-item>
              <el-form-item prop="email" label="邮箱地址" class="form-item">
                <el-input v-model="registerForm.email" placeholder="请输入邮箱地址" class="custom-input"></el-input>
              </el-form-item>
              <el-form-item prop="location" label="所在地区" class="form-item">
                <el-select v-model="registerForm.location" placeholder="请选择地区" class="custom-select" style="width: 100%">
                  <el-option v-for="item in AREA" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </div>
        </div>

        <!-- 个人签名卡片 -->
        <div class="form-card signature-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3>个人签名</h3>
          </div>
          <div class="card-content">
            <el-form-item prop="introduction" class="form-item">
              <el-input 
                type="textarea" 
                placeholder="写下您的个人签名（最多50字）" 
                v-model="registerForm.introduction" 
                maxlength="50" 
                show-word-limit 
                :rows="4"
                class="custom-textarea"
              ></el-input>
            </el-form-item>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button @click="goBack(-1)" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="saveMsg()" class="save-btn">保存修改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, onMounted, getCurrentInstance, reactive} from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { AREA, SignUpRules } from "@/enums";
import { HttpManager } from "@/api";
import { getBirth } from "@/utils";

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { goBack } = mixin();

    // 注册
    const registerForm = reactive({
      username: "",
      sex: "",
      phoneNum: "",
      email: "",
      birth: new Date(),
      introduction: "",
      location: "",
      userPic: "",
    });
    const userId = computed(() => store.getters.userId);

    async function getUserInfo(id) {
      // 验证用户ID的有效性
      if (!id || id === '' || id === 0) {
        console.warn('用户ID无效，跳过获取用户信息');
        // 如果用户ID无效，跳转到登录页
        proxy.$router.replace('/sign-in');
        return;
      }
      
      try {
        const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
        if (result.success && result.data && result.data.length > 0) {
          registerForm.username = result.data[0].username;
          registerForm.sex = result.data[0].sex;
          registerForm.phoneNum = result.data[0].phoneNum;
          registerForm.email = result.data[0].email;
          registerForm.birth = result.data[0].birth;
          registerForm.introduction = result.data[0].introduction;
          registerForm.location = result.data[0].location;
          registerForm.userPic = result.data[0].avator;
        } else {
          console.error('获取用户信息失败：用户不存在');
          proxy.$message({
            message: '用户信息获取失败，请重新登录',
            type: 'error'
          });
          proxy.$router.replace('/sign-in');
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        proxy.$message({
          message: '获取用户信息失败，请重新登录',
          type: 'error'
        });
        proxy.$router.replace('/sign-in');
      }
    }

    async function saveMsg() {
      let canRun = true;
      (proxy.$refs["updateForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      const id = userId.value;
      const username = registerForm.username;
      const sex = registerForm.sex;
      const phoneNum = registerForm.phoneNum;
      const email = registerForm.email;
      const birth = registerForm.birth;
      const introduction = registerForm.introduction;
      const location = registerForm.location;
      const result = (await HttpManager.updateUserMsg({id,username,sex,phoneNum,email,birth,introduction,location})) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.success) {
        proxy.$store.commit("setUsername", registerForm.username);
        goBack(-1);
      }
    }

    onMounted(() => {
      // 延迟检查用户状态，确保store已经从localStorage恢复
      const checkUserAndLoad = () => {
        const currentUserId = userId.value;
        if (currentUserId && currentUserId !== '' && currentUserId !== 0) {
          getUserInfo(currentUserId);
        } else {
          // 如果用户ID仍然无效，再等待一段时间后重试
          setTimeout(() => {
            const retryUserId = userId.value;
            if (retryUserId && retryUserId !== '' && retryUserId !== 0) {
              getUserInfo(retryUserId);
            } else {
              console.warn('用户未登录或session已失效');
              proxy.$router.replace('/sign-in');
            }
          }, 100);
        }
      };
      
      // 立即检查一次，如果失败则延迟重试
      checkUserAndLoad();
    });

    return {
      AREA,
      registerForm,
      SignUpRules,
      saveMsg,
      goBack,
    };
  },
});
</script>

<style lang="scss" scoped>
.personal-data {
  max-width: 100%;
  margin: 0 auto;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 1.8rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }
  
  .form-subtitle {
    color: #6b7280;
    font-size: 1rem;
    margin: 0;
  }
}

.form-container {
  background: transparent;
}

.personal-form {
  ::v-deep .el-form-item__label {
    font-weight: 500;
    color: #374151;
    font-size: 0.95rem;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.form-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  overflow: hidden;
  
  &:hover {
    border-color: #d1d5db;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}

.signature-card {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 20px 24px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e5e7eb;
  
  .card-icon {
    width: 20px;
    height: 20px;
    color: #667eea;
    margin-right: 12px;
  }
  
  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0;
  }
}

.card-content {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  ::v-deep .el-form-item__label {
    line-height: 1.5;
    margin-bottom: 8px;
  }
}

.custom-input {
  ::v-deep .el-input__inner {
    border-radius: 6px;
    border: 1px solid #e5e7eb;
    padding: 12px 16px;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    background: #ffffff;
    box-shadow: none;
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    &:hover {
      border-color: #d1d5db;
    }
  }
}

.custom-select {
  ::v-deep .el-input__inner {
    border-radius: 6px;
    border: 1px solid #e5e7eb;
    padding: 12px 16px;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    background: #ffffff;
    box-shadow: none;
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    &:hover {
      border-color: #d1d5db;
    }
  }
}

.custom-date-picker {
  ::v-deep .el-input__inner {
    border-radius: 6px;
    border: 1px solid #e5e7eb;
    padding: 12px 16px;
    font-size: 0.95rem;
    transition: all 0.2s ease;
    background: #ffffff;
    box-shadow: none;
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    &:hover {
      border-color: #d1d5db;
    }
  }
}

.custom-textarea {
  ::v-deep .el-textarea__inner {
    border-radius: 6px;
    border: 1px solid #e5e7eb;
    padding: 12px 16px;
    font-size: 0.95rem;
    line-height: 1.6;
    resize: vertical;
    transition: all 0.2s ease;
    background: #ffffff;
    box-shadow: none;
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    &:hover {
      border-color: #d1d5db;
    }
  }
  
  ::v-deep .el-input__count {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 4px;
    padding: 2px 6px;
    font-size: 12px;
    color: #6b7280;
    border: 1px solid #e5e7eb;
  }
}

.gender-group {
  display: flex;
  gap: 20px;
  
  .gender-radio {
    ::v-deep .el-radio__label {
      font-size: 0.95rem;
      color: #374151;
    }
    
    ::v-deep .el-radio__input.is-checked .el-radio__inner {
      background-color: #667eea;
      border-color: #667eea;
    }
    
    ::v-deep .el-radio__inner:hover {
      border-color: #667eea;
    }
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.cancel-btn {
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  border: 1px solid #d1d5db;
  color: #6b7280;
  background: white;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #9ca3af;
    color: #374151;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.save-btn {
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  }
}

@media screen and (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .card-content {
    padding: 20px;
  }
  
  .form-header {
    margin-bottom: 20px;
    
    h2 {
      font-size: 1.5rem;
    }
  }
  
  .form-actions {
    flex-direction: column;
    align-items: center;
    
    .cancel-btn,
    .save-btn {
      width: 100%;
      max-width: 200px;
    }
  }
}

@media screen and (max-width: 480px) {
  .card-header {
    padding: 16px 20px 12px;
    
    h3 {
      font-size: 1rem;
    }
    
    .card-icon {
      width: 18px;
      height: 18px;
    }
  }
  
  .card-content {
    padding: 16px;
  }
  
  .gender-group {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
