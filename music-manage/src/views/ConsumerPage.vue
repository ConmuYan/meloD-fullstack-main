<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll">批量删除</el-button>
      <el-input v-model="searchWord" placeholder="筛选用户"></el-input>
    </div>

    <el-table height="550px" border size="small" :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="用户头像" width="102" align="center">
        <template v-slot="scope">
          <img :src="attachImageUrl(scope.row.avator)" style="width: 80px" />
        </template>
      </el-table-column>
      <el-table-column label="用户名" prop="username" width="80" align="center"></el-table-column>
      <el-table-column label="性别" width="50" align="center">
        <template v-slot="scope">
          <div>{{ changeSex(scope.row.sex) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="手机号码" prop="phoneNum" width="120" align="center"></el-table-column>
      <el-table-column label="邮箱" prop="email" width="120" align="center"></el-table-column>
      <el-table-column label="生日" width="120" align="center">
        <template v-slot="scope">
          <div>{{ getBirth(scope.row.birth) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="签名" prop="introduction"></el-table-column>
      <el-table-column label="地区" prop="location" width="70" align="center"></el-table-column>
      <el-table-column label="收藏" width="90" align="center">
        <template v-slot="scope">
          <el-button @click="goCollectPage(scope.row.id)">收藏</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center">
        <template v-slot="scope">
          <el-button type="danger" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column label="修改" width="90" align="center">
        
    <template v-slot="scope">
      <el-upload
        :action="'http://localhost:8888/user/avatar/update?id=' + scope.row.id" 
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload">
        <el-button>更新头像</el-button>
      </el-upload>
   </template>

        
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="tableData.length"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>

  <!-- 删除提示框 -->
  <yin-user-del-dialog
    :delVisible="delVisible"
    :username="currentUserName"
    :isOnline="currentUserOnlineStatus"
    @confirm="confirm"
    @forceDelete="forceConfirm"
    @cancelRow="delVisible = $event"
  ></yin-user-del-dialog>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, reactive, computed } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName } from "@/enums";
import YinUserDelDialog from "@/components/dialog/YinUserDelDialog.vue";
import { getBirth } from "@/utils";

export default defineComponent({
  components: {
    YinUserDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { changeSex, routerManager } = mixin();

    const tableData = ref([]); // 记录歌曲，用于显示
    const tempDate = ref([]); // 记录歌曲，用于搜索时能临时记录一份歌曲列表
    const pageSize = ref(5); // 页数
    const currentPage = ref(1); // 当前页

    // 计算当前表格中的数据
    const data = computed(() => {
      return tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
    });

    const searchWord = ref(""); // 记录输入框输入的内容
    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = [];
        for (let item of tempDate.value) {
          if (item.username.includes(searchWord.value)) {
            tableData.value.push(item);
          }
        }
      }
    });

    getData();

    // 获取用户信息
    async function getData() {
      tableData.value = [];
      tempDate.value = [];
      const result = (await HttpManager.getAllUser()) as ResponseBody;
      tableData.value = result.data;
      tempDate.value = result.data;
      currentPage.value = 1;
    }
    // 获取当前页
    function handleCurrentChange(val) {
      currentPage.value = val;
    }

    /**
     * 路由
     */
    function goCollectPage(id) {
      const breadcrumbList = reactive([
        {
          path: RouterName.Consumer,
          name: "用户管理",
        },
        {
          path: "",
          name: "收藏信息",
        },
      ]);
      proxy.$store.commit("setBreadcrumbList", breadcrumbList);
      routerManager(RouterName.Collect, { path: RouterName.Collect, query: { id } });
    }

    /**
     * 删除
     */
    const deleteId = ref(-1); // 记录当前要删除的行
    const currentUserName = ref(''); // 当前要删除的用户名
    const currentUserOnlineStatus = ref(false); // 当前用户在线状态
    const multipleSelection = ref([]); // 记录当前要删除的列表
    const delVisible = ref(false); // 显示删除框

    async function confirm() {
      const result = (await HttpManager.deleteUser(deleteId.value)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.success) getData();
      delVisible.value = false;
    }
    
    async function forceConfirm() {
      const result = (await HttpManager.forceDeleteUser(deleteId.value)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.success) getData();
      delVisible.value = false;
    }
    
    async function deleteRow(id) {
      deleteId.value = id;
      
      // 获取用户信息
      const userResult = (await HttpManager.getUserOfId(id)) as ResponseBody;
      if (userResult.success && userResult.data && userResult.data[0]) {
        currentUserName.value = userResult.data[0].username;
        
        // 检查用户在线状态
        const onlineResult = (await HttpManager.checkUserOnlineStatus(id)) as ResponseBody;
        if (onlineResult.success) {
          currentUserOnlineStatus.value = onlineResult.data;
        } else {
          currentUserOnlineStatus.value = false;
        }
        
        delVisible.value = true;
      } else {
        (proxy as any).$message({
          message: '获取用户信息失败',
          type: 'error',
        });
      }
    }
    function handleSelectionChange(val) {
      multipleSelection.value = val;
    }
    function deleteAll() {
      for (let item of multipleSelection.value) {
        deleteRow(item.id);
        confirm();
      }
      multipleSelection.value = [];
    }

   //handleAvatarSuccess
   function handleAvatarSuccess(){
     location.reload();
   }




    return {
      searchWord,
      data,
      tableData,
      delVisible,
      currentUserName,
      currentUserOnlineStatus,
      pageSize,
      currentPage,
      deleteAll,
      handleSelectionChange,
      handleCurrentChange,
      changeSex,
      getBirth,
      deleteRow,
      handleAvatarSuccess,
      confirm,
      forceConfirm,
      goCollectPage,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped></style>
