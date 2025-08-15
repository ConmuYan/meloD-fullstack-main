<template>
  <div>
    <!-- 删除提示框 -->
    <el-dialog title="删除用户" v-model="centerDialogVisible" width="450px" center>
      <div class="del-dialog-cnt" align="center">
        <p>确定要删除用户 "{{ username }}" 吗？</p>
        
        <!-- 在线用户警告 -->
        <div v-if="isOnline" class="warning-msg">
          <el-alert
            title="用户在线，请联系用户下线后再删除"
            type="warning"
            :closable="false"
            show-icon>
          </el-alert>
          <div class="force-warning" style="margin-top: 15px;">
            <el-alert
              title="警告：暴力删除将清除该用户的所有数据（评论、收藏、点赞等），且会强制踢出在线用户，此操作不可恢复！"
              type="error"
              :closable="false"
              show-icon>
            </el-alert>
          </div>
        </div>
        
        <!-- 离线用户选项 -->
        <div v-if="!isOnline" class="delete-options">
          <p>请选择删除方式：</p>
          <ul style="text-align: left; margin: 10px 0;">
            <li><strong>普通删除：</strong>保留用户数据（评论、收藏等），仅删除账户</li>
            <li><strong>暴力删除：</strong>彻底删除用户及所有相关数据</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelRow">取 消</el-button>
          
          <!-- 在线用户只显示暴力删除按钮 -->
          <template v-if="isOnline">
            <el-button 
              type="danger" 
              @click="handleForceDelete"
              :disabled="cooldownActive">
              {{ cooldownActive ? `暴力删除 (${cooldownSeconds}s)` : '暴力删除' }}
            </el-button>
          </template>
          
          <!-- 离线用户显示两个按钮 -->
          <template v-else>
            <el-button 
              type="primary" 
              @click="handleNormalDelete">
              删除
            </el-button>
            <el-button 
              type="danger" 
              @click="handleForceDelete"
              :disabled="cooldownActive">
              {{ cooldownActive ? `暴力删除 (${cooldownSeconds}s)` : '暴力删除' }}
            </el-button>
          </template>
        </span>
      </template>
    </el-dialog>

    <!-- 二次确认对话框 -->
    <el-dialog title="最终确认" v-model="secondConfirmVisible" width="350px" center>
      <div align="center">
        <p><strong>最后确认：您确定要暴力删除用户吗？</strong></p>
        <p>此操作将：</p>
        <ul style="text-align: left; margin: 10px 0;">
          <li>删除用户账户</li>
          <li>删除所有评论记录</li>
          <li>删除所有收藏记录</li>
          <li>删除所有点赞记录</li>
          <li>强制踢出在线用户</li>
        </ul>
        <p style="color: red;"><strong>此操作不可恢复！</strong></p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="secondConfirmVisible = false">取消</el-button>
          <el-button type="danger" @click="finalConfirm">确认执行暴力删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, watch, ref, onUnmounted } from "vue";

export default defineComponent({
  props: {
    delVisible: Boolean,
    username: String,
    isOnline: Boolean,
  },
  emits: ["cancelRow", "confirm", "forceDelete"],
  setup(props) {
    const { proxy } = getCurrentInstance();

    const { delVisible } = toRefs(props);
    const centerDialogVisible = ref(delVisible.value);
    const secondConfirmVisible = ref(false);
    const cooldownActive = ref(false);
    const cooldownSeconds = ref(5);
    let cooldownTimer: number | null = null;

    watch(delVisible, (value) => {
      centerDialogVisible.value = value;
      if (value) {
        // 重置状态
        cooldownActive.value = false;
        cooldownSeconds.value = 5;
        secondConfirmVisible.value = false;
        // 清除之前的计时器
        if (cooldownTimer) {
          clearInterval(cooldownTimer);
          cooldownTimer = null;
        }
      }
    });

    // 组件卸载时清除计时器
    onUnmounted(() => {
      if (cooldownTimer) {
        clearInterval(cooldownTimer);
      }
    });

    function cancelRow() {
      // 清除计时器
      if (cooldownTimer) {
        clearInterval(cooldownTimer);
        cooldownTimer = null;
      }
      proxy.$emit("cancelRow", false);
    }

    function handleNormalDelete() {
      // 普通删除，直接确认
      proxy.$emit("confirm", null);
    }

    function handleForceDelete() {
      if (cooldownActive.value) {
        return; // 冷静时间内不允许操作
      }
      
      // 开始5秒冷静时间
      cooldownActive.value = true;
      cooldownSeconds.value = 5;
      
      cooldownTimer = setInterval(() => {
        cooldownSeconds.value--;
        if (cooldownSeconds.value <= 0) {
          cooldownActive.value = false;
          if (cooldownTimer) {
            clearInterval(cooldownTimer);
            cooldownTimer = null;
          }
          // 冷静时间结束，显示二次确认对话框
          secondConfirmVisible.value = true;
        }
      }, 1000);
    }

    function finalConfirm() {
      secondConfirmVisible.value = false;
      proxy.$emit("forceDelete", null);
    }

    return {
      centerDialogVisible,
      secondConfirmVisible,
      cooldownActive,
      cooldownSeconds,
      cancelRow,
      handleNormalDelete,
      handleForceDelete,
      finalConfirm,
    };
  },
});
</script>

<style scoped>
.del-dialog-cnt {
  padding: 20px 0;
}

.warning-msg {
  margin: 15px 0;
}

.delete-options {
  margin: 15px 0;
  text-align: left;
}

.force-warning {
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>