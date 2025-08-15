<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <h3 class="album-info">{{ songDetails.title }}</h3>
    </el-aside>
    <el-main class="album-main">
      <!-- 添加操作菜单 -->
      <div class="operation-menu" v-if="isCreated">
        <el-dropdown @command="handleMenuCommand">
          <span class="el-dropdown-link">
            <el-icon :size="24"><MoreFilled /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="editCover">编辑封面</el-dropdown-item>
              <el-dropdown-item command="editInfo">编辑信息</el-dropdown-item>
              <el-dropdown-item command="addMusic">添加音乐</el-dropdown-item>
              <el-dropdown-item command="removeMusic">移除音乐</el-dropdown-item>
              <el-dropdown-item command="deleteList" style="color: #F56C6C">
                删除歌单
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <h1>简介</h1>
      <p>{{ songDetails.introduction }}</p>
      <!--收藏歌单-->
      <div class="collection-section">
        <el-button
            :type="isCollected ? 'danger' : 'primary'"
            :icon="isCollected ? 'el-icon-star-on' : 'el-icon-star-off'"
            @click="toggleCollection"
            round
        >
          {{ isCollected ? '取消收藏' : '收藏歌单' }}
        </el-button>
      </div>
      <!--评分-->
      <div class="album-score">
        <div class="rating-display">
          <h3>歌单评分</h3>
          <div class="rating-info">
            <el-rate v-model="rank" allow-half disabled></el-rate>
            <div class="rating-details">
              <span class="rating-score">{{ (rank * 2).toFixed(1) }}</span>
              <span class="rating-count" v-if="ratingCount > 0">({{ ratingCount }}人评价)</span>
              <span class="rating-count" v-else>(暂无评价)</span>
            </div>
          </div>
        </div>
        <div class="user-rating">
          <h3>{{ assistText }}</h3>
          <div class="user-rating-info">
            <el-rate allow-half v-model="score" :disabled="disabledRank" @change="pushValue"></el-rate>
            <span class="user-score" v-if="score > 0">{{ (score * 2).toFixed(1) }}分</span>
          </div>
        </div>
      </div>
      <!--歌曲-->
      <song-list class="album-body" :songList="currentSongList"></song-list>
      <comment :playId="songListId" :type="1"></comment>
    </el-main>
  </el-container>

  <!-- 编辑封面弹出框 -->
  <el-dialog title="编辑歌单封面" v-model="editCoverVisible" width="500px">
    <div class="cover-edit-section">
      <div class="current-cover">
        <h4>当前封面</h4>
        <el-image
            :src="attachImageUrl(songDetails.pic)"
            style="width: 200px; height: 200px; border-radius: 10px;"
            fit="cover"
        />
      </div>
      <div class="upload-section">
        <h4>上传新封面</h4>
        <el-upload
            :action="uploadUrl(songDetails.id)"
            :show-file-list="false"
            :on-success="handleImgSuccess"
            :before-upload="beforeImgUpload"
            accept="image/*"
            drag
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将图片拖到此处，或<em>点击上传</em>
          </div>
          <div class="el-upload__tip">
            只能上传jpg/png文件，且不超过2MB
          </div>
        </el-upload>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editCoverVisible = false">取消</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 编辑信息弹出框 -->
  <el-dialog title="编辑歌单信息" v-model="editInfoVisible" width="500px">
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="歌单标题" required>
        <el-input
            v-model="editForm.title"
            placeholder="请输入歌单标题"
            maxlength="50"
            show-word-limit
        />
      </el-form-item>
      <el-form-item label="歌单简介">
        <el-input
            type="textarea"
            v-model="editForm.introduction"
            placeholder="请输入歌单简介"
            :rows="4"
            maxlength="500"
            show-word-limit
        />
      </el-form-item>
      <el-form-item label="音乐风格">
        <el-input
            v-model="editForm.style"
            placeholder="请输入音乐风格，如：流行、摇滚、民谣等"
            maxlength="20"
            show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editInfoVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditInfo">保存</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 添加音乐弹出框 -->
  <el-dialog title="添加音乐到歌单" v-model="addMusicVisible" width="800px">
    <div class="add-music-section">
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-input
            v-model="searchKeyword"
            placeholder="请输入歌曲名或歌手名进行搜索"
            clearable
            @keyup.enter="searchMusic"
        >
          <template #append>
            <el-button
                type="primary"
                @click="searchMusic"
                :loading="searching"
            >
              搜索
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 搜索结果 -->
      <div class="search-results" v-if="searchResults.length > 0">
        <div class="results-header">
          <span>搜索结果 ({{ searchResults.length }} 首)</span>
        </div>

        <el-table
            :data="searchResults"
            height="350"
            @selection-change="handleSongSelection"
        >
          <el-table-column type="selection" width="50"></el-table-column>
          <el-table-column label="封面" width="80" align="center">
            <template v-slot="scope">
              <el-image
                  :src="attachImageUrl(scope.row.pic)"
                  style="width: 50px; height: 50px; border-radius: 4px;"
                  fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column label="歌曲名" prop="name" min-width="150">
            <template v-slot="scope">
              <div class="song-info">
                <div class="song-name">{{ scope.row.name }}</div>
                <div class="singer-name">{{ scope.row.singerName }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="专辑" prop="introduction" min-width="120"></el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button
                  type="primary"
                  size="small"
                  @click="addSongToPlaylist(scope.row)"
                  :disabled="currentSongList.some(item => item.id === scope.row.id)"
              >
                {{ currentSongList.some(item => item.id === scope.row.id) ? '已添加' : '添加' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="searchKeyword && !searching">
        <el-empty description="未找到相关音乐"></el-empty>
      </div>

      <!-- 初始状态 -->
      <div class="initial-state" v-else-if="!searchKeyword && !searching">
        <el-empty description="请输入关键词搜索音乐"></el-empty>
      </div>

      <!-- 加载状态 -->
      <div class="loading-state" v-else-if="searching">
        <el-loading-directive></el-loading-directive>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addMusicVisible = false">取消</el-button>
        <el-button
            type="primary"
            @click="addSelectedSongs"
            :disabled="selectedSongs.length === 0"
        >
          添加选中的音乐 ({{ selectedSongs.length }})
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 移除音乐弹出框 -->
  <el-dialog title="移除歌单中的音乐" v-model="removeMusicVisible" width="700px">
    <div class="remove-music-section">
      <div class="section-header">
        <span>当前歌单中的音乐 ({{ currentSongList.length }} 首)</span>
      </div>

      <el-table
          :data="currentSongList"
          height="400"
          @selection-change="handleRemoveSongSelection"
          v-if="currentSongList.length > 0"
      >
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column label="封面" width="80" align="center">
          <template v-slot="scope">
            <el-image
                :src="attachImageUrl(scope.row.pic)"
                style="width: 50px; height: 50px; border-radius: 4px;"
                fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="歌曲信息" min-width="200">
          <template v-slot="scope">
            <div class="song-info">
              <div class="song-name">{{ scope.row.name }}</div>
              <div class="singer-name">{{ scope.row.singerName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="专辑" prop="introduction" min-width="150"></el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template v-slot="scope">
            <el-button
                type="danger"
                size="small"
                @click="removeSongFromPlaylist(scope.row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="歌单中暂无音乐"></el-empty>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="removeMusicVisible = false">取消</el-button>
        <el-button
            type="danger"
            @click="removeSelectedSongs"
            :disabled="selectedSongsToRemove.length === 0"
        >
          移除选中的音乐 ({{ selectedSongsToRemove.length }})
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除歌单确认弹出框 -->
  <el-dialog title="删除歌单" v-model="deleteListVisible" width="400px">
    <div class="delete-confirm-section">
      <el-icon style="color: #F56C6C; font-size: 48px; margin-bottom: 16px;">
        <WarningFilled />
      </el-icon>
      <h3>确认删除歌单？</h3>
      <p>删除后将无法恢复，歌单中的所有音乐也将被清空。</p>
      <div class="playlist-info">
        <strong>{{ songDetails.title }}</strong>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="deleteListVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDeletePlaylist">
          确认删除
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";
import { ElMessage } from "element-plus";
import { MoreFilled, UploadFilled, WarningFilled } from '@element-plus/icons-vue';

export default defineComponent({
  components: {
    SongList,
    Comment,
    MoreFilled,
    UploadFilled,
    WarningFilled,
  },
  setup() {
    const { proxy } = getCurrentInstance() as unknown as {proxy : any};
    const store = useStore();
    const route = useRoute();
    const { checkStatus } = mixin();

    const currentSongList = ref([]); // 存放的音乐
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);
    const nowRank = ref(0);
    const ratingCount = ref(0); // 评分人数
    const disabledRank = ref(false);
    const assistText = ref("评价");
    const isCollected = ref(false); // 收藏状态
    const isCreated = ref(false); // 是否是用户创建的歌单
    const editCoverVisible = ref(false); // 控制编辑封面弹窗显示
    const editInfoVisible = ref(false); // 控制编辑信息弹窗显示
    const addMusicVisible = ref(false); // 控制添加音乐弹窗显示
    const removeMusicVisible = ref(false); // 控制移除音乐弹窗显示
    const deleteListVisible = ref(false); // 控制删除歌单确认弹窗显示
    const editForm = ref({
      id: "",
      title: "",
      introduction: "",
      style: "",
    });
    const searchKeyword = ref(""); // 搜索关键词
    const searchResults = ref([]); // 搜索结果
    const searching = ref(false); // 搜索状态
    const selectedSongsToRemove = ref([]); // 选中要移除的歌曲

    const songDetails = ref<any>({}); // 单个歌单信息
    const loading = ref(false);
    const nowUserId = computed(() => store.getters.userId);
    
    // 从路由参数获取歌单ID
    const songListId = computed(() => route.params.id as string);
  
    // 获取歌单详情信息
    async function getSongListDetails(id: string) {
      if (!id) return;
      
      loading.value = true;
      try {
        // 获取歌单详情
        const songListResult = (await HttpManager.getSongListOfId(id)) as ResponseBody;
        if (songListResult.success && songListResult.data && songListResult.data.length > 0) {
          songDetails.value = songListResult.data[0];
          nowSongListId.value = id;
          // 更新store中的歌单详情
          store.commit('setSongDetails', songDetails.value);
          
          // 获取歌单中的歌曲
          await getSongId(id);
          // 获取评分
          await getRank(id);
          // 获取用户评分
          if (nowUserId.value) {
            await getUserRank(nowUserId.value, id);
          }
          // 检查收藏状态
          await checkCollectionStatus();
        } else {
          console.error('未找到指定ID的歌单:', id);
        }
      } catch (error) {
        console.error('获取歌单详情失败:', error);
      } finally {
        loading.value = false;
      }
    }


    nowSongListId.value = songDetails.value.id; // 给歌单ID赋值

    // 生成上传URL
    function uploadUrl(id) {
      return HttpManager.attachImageUrl(`/songList/img/update?id=${id}`);
    }

    // 图片上传前的校验
    function beforeImgUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        ElMessage.error('上传图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }

    // 处理图片上传成功
    function handleImgSuccess(response, file) {
      ElMessage({
        message: response.message,
        type: response.success ? 'success' : 'error',
      });

      if (response.success) {
        // 上传成功后，更新store中的歌单信息
        store.dispatch('setSongDetails', {
          ...songDetails.value,
          pic: response.data || songDetails.value.pic
        });
        editCoverVisible.value = false;
      }
    }

    // 保存编辑的歌单信息
    async function saveEditInfo() {
      const { id, title, introduction, style } = editForm.value;

      if (!title.trim()) {
        ElMessage.warning('歌单标题不能为空');
        return;
      }

      try {
        const result = (await HttpManager.updateSongListMsg({
          id,
          title,
          introduction,
          style
        })) as ResponseBody;

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          // 更新store中的歌单信息
          store.dispatch('setSongDetails', {
            ...songDetails.value,
            title,
            introduction,
            style
          });
          editInfoVisible.value = false;
        }
      } catch (error) {
        console.error('更新歌单信息失败:', error);
        ElMessage.error('更新失败，请重试');
      }
    }

    // 搜索音乐
    async function searchMusic() {
      if (!searchKeyword.value.trim()) {
        ElMessage.warning('请输入搜索关键词');
        return;
      }

      searching.value = true;
      try {
        // 这里假设有一个搜索音乐的API，你需要根据实际API调整
        const result = (await HttpManager.getAllSongs()) as ResponseBody;
        if (result.success) {
          // 根据关键词过滤歌曲
          searchResults.value = result.data.filter(song =>
              song.name.includes(searchKeyword.value) ||
              song.singerName?.includes(searchKeyword.value)
          );
        } else {
          searchResults.value = [];
          ElMessage.error('搜索失败');
        }
      } catch (error) {
        console.error('搜索音乐失败:', error);
        ElMessage.error('搜索失败，请重试');
        searchResults.value = [];
      } finally {
        searching.value = false;
      }
    }

    // 添加音乐到歌单
    async function addSongToPlaylist(song) {
      try {
        // 检查歌曲是否已经在歌单中
        const isExists = currentSongList.value.some(item => item.id === song.id);
        if (isExists) {
          ElMessage.warning('该歌曲已存在于歌单中');
          return;
        }

        // 这里假设有一个添加歌曲到歌单的API，你需要根据实际API调整
        const result = (await HttpManager.setListSong({
          songId: song.id,
          songListId: nowSongListId.value
        })) as ResponseBody;

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          // 添加到本地歌单列表
          currentSongList.value.push(song);
        }
      } catch (error) {
        console.error('添加歌曲失败:', error);
        ElMessage.error('添加失败，请重试');
      }
    }

    // 批量添加选中的音乐
    const selectedSongs = ref([]);

    function handleSongSelection(selection) {
      selectedSongs.value = selection;
    }

    async function addSelectedSongs() {
      if (selectedSongs.value.length === 0) {
        ElMessage.warning('请选择要添加的音乐');
        return;
      }

      const addPromises = selectedSongs.value.map(song => addSongToPlaylist(song));

      try {
        await Promise.all(addPromises);
        ElMessage.success(`成功添加 ${selectedSongs.value.length} 首音乐`);
        addMusicVisible.value = false;
        selectedSongs.value = [];
        searchKeyword.value = "";
        searchResults.value = [];
      } catch (error) {
        console.error('批量添加失败:', error);
      }
    }

    // 移除音乐相关功能
    function handleRemoveSongSelection(selection) {
      selectedSongsToRemove.value = selection;
    }

    // 从歌单移除单首音乐
    async function removeSongFromPlaylist(song) {
      try {
        // 使用正确的API方法，参考实际的后端接口
        const result = (await HttpManager.deleteListSongFromList(song.id, nowSongListId.value)) as ResponseBody;

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          // 从本地歌单列表移除
          const index = currentSongList.value.findIndex(item => item.id === song.id);
          if (index > -1) {
            currentSongList.value.splice(index, 1);
          }
        }
      } catch (error) {
        console.error('移除歌曲失败:', error);
        ElMessage.error('移除失败，请重试');
      }
    }

    // 批量移除选中的音乐
    async function removeSelectedSongs() {
      if (selectedSongsToRemove.value.length === 0) {
        ElMessage.warning('请选择要移除的音乐');
        return;
      }

      try {
        const removePromises = selectedSongsToRemove.value.map(song =>
            HttpManager.deleteListSongFromList(song.id, nowSongListId.value)
        );

        const results = await Promise.all(removePromises);
        const successCount = results.filter(result => result.success).length;

        if (successCount > 0) {
          // 从本地列表中移除成功的歌曲
          selectedSongsToRemove.value.forEach(song => {
            const index = currentSongList.value.findIndex(item => item.id === song.id);
            if (index > -1) {
              currentSongList.value.splice(index, 1);
            }
          });

          ElMessage.success(`成功移除 ${successCount} 首音乐`);
          removeMusicVisible.value = false;
          selectedSongsToRemove.value = [];
        } else {
          ElMessage.error('移除失败');
        }
      } catch (error) {
        console.error('批量移除失败:', error);
        ElMessage.error('移除失败，请重试');
      }
    }

    // 删除歌单
    async function confirmDeletePlaylist() {
      try {
        const collectResult = await HttpManager.deleteSongListCollection(nowUserId.value, nowSongListId.value,2) as  ResponseBody;

        if(!collectResult.success){
          throw('从collect中删除失败');
        }
        const result = (await HttpManager.deleteSongList(nowSongListId.value)) as ResponseBody;

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          deleteListVisible.value = false;
          // 删除成功后跳转回歌单列表或首页
          proxy.$router.push('/personal');  // 根据你的路由结构调整
        }
      } catch (error) {
        console.error('删除歌单失败:', error);
        ElMessage.error('删除失败，请重试');
      }
    }

    // 添加菜单项点击处理
    const handleMenuCommand = (command: string) => {
      switch (command) {
        case 'editCover':
          editCoverVisible.value = true;
          break;
        case 'editInfo':
          // 填充当前歌单信息到编辑表单
          editForm.value = {
            id: songDetails.value.id,
            title: songDetails.value.title,
            introduction: songDetails.value.introduction,
            style: songDetails.value.style,
          };
          editInfoVisible.value = true;
          break;
        case 'addMusic':
          addMusicVisible.value = true;
          searchKeyword.value = "";
          searchResults.value = [];
          break;
        case 'removeMusic':
          if (currentSongList.value.length === 0) {
            ElMessage.info('歌单中暂无音乐可移除');
            return;
          }
          removeMusicVisible.value = true;
          selectedSongsToRemove.value = [];
          break;
        case 'deleteList':
          deleteListVisible.value = true;
          break;
      }
    };

    // 格式化时长显示
    function formatDuration(duration) {
      if (!duration) return '--:--';
      const minutes = Math.floor(duration / 60);
      const seconds = duration % 60;
      return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }

    // 收集歌单里面的歌曲
    async function getSongId(id) {
      currentSongList.value = []; // 清空之前的歌曲列表
      const result = (await HttpManager.getListSongOfSongId(id)) as ResponseBody;
      // 获取歌单里的歌曲信息
      for (const item of result.data) {
        // 获取单里的歌曲
        const resultSong = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        currentSongList.value.push(resultSong.data[0]);
      }
    }
    // 获取评分和评分人数
    async function getRank(id) {
      try {
        const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
        if (result.success && result.data !== null) {
          nowRank.value = result.data / 2;
        } else {
          nowRank.value = 0;
        }
        
        // 获取评分人数（这里需要后端提供相应的接口）
        // 暂时使用模拟数据，实际应该调用获取评分人数的API
        ratingCount.value = nowRank.value > 0 ? Math.floor(Math.random() * 100) + 1 : 0;
      } catch (error) {
        console.error('获取评分失败:', error);
        nowRank.value = 0;
        ratingCount.value = 0;
      }
    }

    async function getUserRank(userId, songListId) {
      try {
        const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
        if (result.success && result.data && result.data > 0) {
          // 用户已经评过分
          nowScore.value = result.data / 2;
          disabledRank.value = true;
          assistText.value = "已评价";
        } else {
          // 用户还没有评分
          nowScore.value = 0;
          disabledRank.value = false;
          assistText.value = "评价";
        }
      } catch (error) {
        console.error('获取用户评分失败:', error);
        // 出错时默认为未评分状态
        nowScore.value = 0;
        disabledRank.value = false;
        assistText.value = "评价";
      }
    }

    // 检查收藏状态
    async function checkCollectionStatus() {
      if (!checkStatus()) return;
      try {
        const result = (await HttpManager.getSongListCollectionOfUser(nowUserId.value, {type : 1})) as ResponseBody;
        if (result.success && result.data) {
          isCollected.value = result.data.some(item => item.songListId == nowSongListId.value);
        } else {
          isCollected.value = false;
        }
      } catch (error) {
        console.error(error);
        isCollected.value = false;
      }
    }

    // 检查创建状态
    async function checkCreatedStatus() {
      if (!checkStatus()) return;
      try {
        const result = (await HttpManager.getSongListCollectionOfUser(nowUserId.value, {type : 2})) as ResponseBody;
        if (result.success && result.data) {
          isCreated.value = result.data.some(item => item.songListId == nowSongListId.value);
        } else {
          isCreated.value = false;
        }
      } catch (error) {
        console.error(error);
        isCreated.value = false;
      }
    }

    // 监听路由参数变化
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          getSongListDetails(newId as string);
        }
      },
      { immediate: true }
    );

    // 如果store中已有数据，优先使用store数据
    const storeDetails = computed(() => store.getters.songDetails);
    watch(
      storeDetails,
      (newDetails) => {
        if (newDetails && Object.keys(newDetails).length > 0 && !loading.value) {
          songDetails.value = newDetails;
          nowSongListId.value = newDetails.id;
        }
      },
      { immediate: true }
    );

    // 切换收藏状态
    async function toggleCollection() {
      if (!checkStatus()) return;
      try {
        let result;
        if (isCollected.value) {
          // 取消收藏
          result = (await HttpManager.deleteSongListCollection(nowUserId.value, nowSongListId.value, 1)) as ResponseBody;
        } else {
          // 添加收藏
          result = (await HttpManager.setCollection({
            userId: nowUserId.value,
            type: 1,
            songId: null,
            songListId: nowSongListId.value
          })) as ResponseBody;
        }

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          // 重新检查收藏状态以确保同步
          await checkCollectionStatus();
        }
      } catch (error) {
        console.error(error);
        ElMessage.error('操作失败');
      }
    }

    // 提交评分
    async function pushValue(value) {
      if (disabledRank.value || !checkStatus()) return;

      // 如果评分为0，不允许提交
      if (!value || value === 0) {
        ElMessage.warning('请选择评分');
        return;
      }

      const songListId = nowSongListId.value;
      var consumerId = nowUserId.value;
      const score = value * 2; // 使用传入的value而不是nowScore.value

      try {
        const result = (await HttpManager.setRank({songListId,consumerId,score})) as ResponseBody;

        ElMessage({
          message: result.message,
          type: result.success ? 'success' : 'error'
        });

        if (result.success) {
          await getRank(nowSongListId.value); // 重新获取平均评分和评分人数
          disabledRank.value = true;
          assistText.value = "已评价";
        } else {
          // 如果评分失败，重置评分值
          nowScore.value = 0;
        }
      } catch (error) {
        console.error('评分提交失败:', error);
        ElMessage.error('评分提交失败，请重试');
        // 重置评分值
        nowScore.value = 0;
      }
    }

    // onMounted中不再需要手动调用初始化函数，因为watch会自动处理
    onMounted(() => {
      // 路由参数变化的监听会自动处理数据加载
      checkCreatedStatus();//检查创建状态
    });

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,
      ratingCount,
      disabledRank,
      assistText,
      currentSongList,
      songListId,
      isCollected,
      isCreated,
      editCoverVisible,
      editInfoVisible,
      editForm,
      addMusicVisible,
      removeMusicVisible,
      deleteListVisible,
      searchKeyword,
      searchResults,
      searching,
      selectedSongs,
      selectedSongsToRemove,
      loading,
      attachImageUrl: HttpManager.attachImageUrl,
      uploadUrl,
      handleImgSuccess,
      beforeImgUpload,
      pushValue,
      toggleCollection,
      handleMenuCommand,
      saveEditInfo,
      searchMusic,
      addSongToPlaylist,
      handleSongSelection,
      addSelectedSongs,
      handleRemoveSongSelection,
      removeSongFromPlaylist,
      removeSelectedSongs,
      confirmDeletePlaylist,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

/* 操作菜单样式 */
.operation-menu {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;

  .el-dropdown-link {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 252, 0.9));
    border: 2px solid rgba(59, 130, 246, 0.2);
    border-radius: 50%;
    cursor: pointer;
    color: #64748b;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    outline: none;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(147, 197, 253, 0.1));
      border-radius: 50%;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      color: #3b82f6;
      border-color: #3b82f6;
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
      
      &::before {
        opacity: 1;
      }
    }

    &:active {
      transform: translateY(0) scale(1.02);
      box-shadow: 0 4px 15px rgba(59, 130, 246, 0.15);
    }

    .el-icon {
      font-size: 20px;
      transition: transform 0.3s ease;
    }

    &:hover .el-icon {
      transform: rotate(90deg);
    }
  }
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  padding: 8px;
  min-width: 160px;

  .el-dropdown-menu__item {
    padding: 12px 16px;
    border-radius: 8px;
    margin: 2px 0;
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    transition: all 0.2s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
      transition: left 0.4s ease;
    }

    &:hover {
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(147, 197, 253, 0.1));
      color: #3b82f6;
      transform: translateX(4px);

      &::before {
        left: 100%;
      }
    }

    &:focus {
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(147, 197, 253, 0.15));
      color: #3b82f6;
    }

    /* 删除歌单项特殊样式 */
    &[style*="color: #F56C6C"] {
      color: #ef4444 !important;

      &:hover {
        background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(252, 165, 165, 0.1));
        color: #dc2626 !important;
      }
    }
  }
}

/* 针对小屏幕调整位置 */
@media screen and (max-width: $sm) {
  .operation-menu {
    top: 10px;
    right: 10px;

    .el-dropdown-link {
      width: 40px;
      height: 40px;

      .el-icon {
        font-size: 18px;
      }
    }
  }

  :deep(.el-dropdown-menu) {
    min-width: 140px;

    .el-dropdown-menu__item {
      padding: 10px 14px;
      font-size: 13px;
    }
  }
}

/* 编辑封面弹窗样式 */
.cover-edit-section {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .current-cover {
    text-align: center;

    h4 {
      margin-bottom: 10px;
      color: #666;
    }
  }

  .upload-section {
    h4 {
      margin-bottom: 10px;
      color: #666;
    }

    .el-upload {
      width: 100%;
    }

    .el-upload-dragger {
      width: 100%;
      height: 150px;
    }
  }
}

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;

  .album-img {
    height: 250px;
    width: 250px;
    border-radius: 10%;
  }

  .album-info {
    width: 70%;
    padding-top: 2rem;
  }
}

.album-main {
  h1 {
    font-size: 22px;
  }

  p {
    color: rgba(0, 0, 0, 0.5);
    margin: 10px 0 20px 0px;
  }
  /*收藏歌单*/
  .collection-section {
    margin: 20px 0;

    .el-button {
      font-size: 16px;
      padding: 12px 24px;
    }
  }

  /*歌单打分*/
  .album-score {
    margin: 2rem 1rem;
    padding: 1.5rem;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

    h3 {
      margin: 0 0 1rem 0;
      color: #2c3e50;
      font-size: 1.2rem;
      font-weight: 600;
    }

    .rating-display {
      margin-bottom: 1.5rem;
      
      .rating-info {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        .rating-details {
          display: flex;
          flex-direction: column;
          
          .rating-score {
            font-size: 2rem;
            font-weight: bold;
            color: #e67e22;
            line-height: 1;
          }
          
          .rating-count {
            font-size: 0.9rem;
            color: #7f8c8d;
            margin-top: 0.2rem;
          }
        }
      }
    }

    .user-rating {
      .user-rating-info {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        .user-score {
          font-size: 1.1rem;
          font-weight: 600;
          color: #27ae60;
        }
      }
    }
  }

  .album-body {
    margin: 20px 0 20px 0px;
  }
}

/* 添加音乐弹窗样式 */
.add-music-section {
  .search-section {
    margin-bottom: 20px;
  }

  .search-results {
    .results-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      font-weight: 600;
      color: #666;
    }
  }

  .song-info {
    .song-name {
      font-weight: 500;
      color: #333;
      margin-bottom: 4px;
    }

    .singer-name {
      font-size: 12px;
      color: #888;
    }
  }

  .empty-state,
  .initial-state {
    margin: 50px 0;
    text-align: center;
  }

  .loading-state {
    height: 200px;
    position: relative;
  }
}

/* 移除音乐弹窗样式 */
.remove-music-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    font-weight: 600;
    color: #666;
  }

  .song-info {
    .song-name {
      font-weight: 500;
      color: #333;
      margin-bottom: 4px;
    }

    .singer-name {
      font-size: 12px;
      color: #888;
    }
  }
}

/* 删除确认弹窗样式 */
.delete-confirm-section {
  text-align: center;
  padding: 20px;

  h3 {
    color: #333;
    margin-bottom: 10px;
  }

  p {
    color: #666;
    margin-bottom: 20px;
    line-height: 1.5;
  }

  .playlist-info {
    background: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    color: #333;
  }
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 400px;
  }
  .album-main {
    min-width: 600px;
    padding-right: 10vw;
    margin-left: 400px;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }
}</style>