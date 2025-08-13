import { getCurrentInstance, computed } from "vue";
import { useStore } from "vuex";
import { LocationQueryRaw, useRouter } from "vue-router";
import { RouterName } from "@/enums";
import { HttpManager } from "@/api";
import axios from 'axios'
import { ComponentInternalInstance } from "vue";

interface routerOptions {
  path?: string;
  query?: LocationQueryRaw;
}

export default function () {
  const { proxy } = getCurrentInstance() as unknown as { proxy: any };
  const router = useRouter();

  const store = useStore();
  const token = computed(() => store.getters.token);

  function getUserSex(sex) {
    if (sex === 0) {
      return "女";
    } else if (sex === 1) {
      return "男";
    }
  }

  // 获取歌曲名
  function getSongTitle(str) {
    return str.split("-")[1];
  }

  // 获取歌手名
  function getSingerName(str) {
    return str.split("-")[0];
  }

  // 判断登录状态
  function checkStatus(status?: boolean) {
    if (!token.value) {
      if (status !== false)
        proxy.$message({
          message: "请先登录",
          type: "warning",
        });
      return false;
    }
    return true;
  }

  // 播放
  function playMusic({ id, url, pic, index, name, lyric, currentSongList, autoPlay = true }) {
    const songTitle = getSongTitle(name);
    const singerName = getSingerName(name);
    proxy.$store.dispatch("playMusic", {
      id,
      url,
      pic,
      index,
      songTitle,
      singerName,
      lyric,
      currentSongList,
      autoPlay
    });
  }

  function getFileName(path) {
    const parts = path.split('/');
    return parts[parts.length - 1];
  }

  // 下载
  async function downloadMusic({ songUrl, songName }) {
    if (!songUrl) {
      proxy.$message({
        message: "下载链接为空！",
        type: "error",
      });
      console.error("下载链接为空！");
      return;
    }
    const fileName = getFileName(songUrl);
    const downUrl="/download/"+fileName

    const response = await axios.get(downUrl, {
      responseType: 'blob', // 指定响应类型为二进制数据
    });
    
    // 创建一个Blob URL来下载文件
    const blob = new Blob([response.data], { type: 'application/octet-stream' });
    const url = window.URL.createObjectURL(blob);

    // 创建一个隐藏的<a>标签来下载文件
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();

    // 释放URL对象
    window.URL.revokeObjectURL(url);
    document.body.removeChild(link);
  }

  // 导航索引
  function changeIndex(value) {
    proxy.$store.commit("setActiveNavName", value);
  }
  
  // 路由管理
  function routerManager(routerName: string | number, options: routerOptions) {
    switch (routerName) {
      case RouterName.Search:
        router.push({ path: options.path, query: options.query });
        break;
      case RouterName.Home:
      case RouterName.SongSheet:
      case RouterName.SongSheetDetail:
      case RouterName.Singer:
      case RouterName.SingerDetail:
      case RouterName.Personal:
      case RouterName.PersonalData:
      case RouterName.Setting:
      case RouterName.SignIn:
      case RouterName.SignUp:
      case RouterName.SignOut:
      case RouterName.Lyric:
      case RouterName.Error:
      default:
        router.push({ path: options.path });
        break;
    }
  }

  function goBack(step = -1) {
    router.go(step);
  }

  return {
    getUserSex,
    getSongTitle,
    getSingerName,
    changeIndex,
    checkStatus,
    playMusic,
    routerManager,
    goBack,
    downloadMusic,
  };
}

export function routerManager(path: string, flag: boolean) {
  const router = useRouter();
  if (flag) {
    router.push({ path, query: { from: 'song' } });
  } else {
    router.push({ path, query: { from: 'songList' } });
  }
}

export function goBack() {
  const router = useRouter();
  router.go(-1);
}
