import { getBaseURL, get, post, deletes } from "./request";

const HttpManager = {
  // 获取图片信息
  attachImageUrl: (url) => {
    if (!url) return "https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png";
    const baseUrl = getBaseURL();
    const cleanUrl = url.startsWith('/') ? url.substring(1) : url;
    return `${baseUrl}/${cleanUrl}`;
  },
  // =======================> 用户 API 完成
  // 登录
  signIn: ({username,password}) => post(`user/login/status`, {username,password}),
  signInByemail: ({email,password})=>post(`user/email/status`, {email,password}),
  // 注册
  SignUp: ({username,password,sex,phoneNum,email,birth,introduction,location}) => post(`user/add`, {username,password,sex,phoneNum,email,birth,introduction,location}),
  // 删除用户
  deleteUser: (id) => get(`user/delete?id=${id}`),
  // 更新用户信息
  updateUserMsg: ({id,username,sex,phoneNum,email,birth,introduction,location}) => post(`user/update`, {id,username,sex,phoneNum,email,birth,introduction,location}),
  updateUserPassword: ({id,username,oldPassword,password}) => post(`user/updatePassword`, {id,username,oldPassword,password}),
  // 返回指定ID的用户
  getUserOfId: (id) => get(`user/detail?id=${id}`),
  // 更新用户头像
  uploadUrl: (userId) => `${getBaseURL()}/user/avatar/update?id=${userId}`,

  // =======================> 歌单 API 完成
  // 获取全部歌单
  getSongList: () => get("songList"),
  // 获取指定ID的歌单详情
  getSongListOfId: (id) => get(`songList/detail?id=${id}`),
  // 获取歌单类型
  getSongListOfStyle: (style) => get(`songList/style/detail?style=${style}`),
  // 返回标题包含文字的歌单
  getSongListOfLikeTitle: (keywords) => get(`songList/likeTitle/detail?title=${keywords}`),
  // 返回歌单里指定歌单ID的歌曲
  getListSongOfSongId: (songListId) => get(`listSong/detail?songListId=${songListId}`),

  // =======================> 歌手 API  完成
  // 返回所有歌手
  getAllSinger: () => get("singer"),
  // 通过性别对歌手分类
  getSingerOfSex: (sex) => get(`singer/sex/detail?sex=${sex}`),
  // 根据歌手名字查询歌手
  getSingerOfName: (name) => get(`singer/name/detail?name=${name}`),

  // =======================> 收藏 API 完成
  // 返回的指定用户ID的收藏列表
  getCollectionOfUser: (userId) => get(`collection/detail?userId=${userId}`),
  // 添加收藏的歌曲 type: 0 代表歌曲， 1 代表歌单
  setCollection: ({userId,type,songId,songListId}) => post(`collection/add`,{userId,type,songId,songListId}),

  deleteCollection: (userId, songId) => deletes(`collection/delete?userId=${userId}&&songId=${songId}`),

  isCollection: ({userId, songId}) => post(`collection/status`, {userId, songId}),

  // 歌单收藏相关API
  getSongListCollectionOfUser: (userId) => get(`collection/songList/detail?userId=${userId}`),
  getSongCollectionOfUser: (userId) => get(`collection/song/detail?userId=${userId}`),
  isSongListCollection: ({userId, songListId}) => post(`collection/songList/status`, {userId, songListId}),
  deleteSongListCollection: (userId, songListId) => deletes(`collection/songList/delete?userId=${userId}&songListId=${songListId}`),
  
  // "我喜欢"歌单相关API
  createMyFavoriteSongList: (userId) => post(`songList/myFavorite/create?userId=${userId}`),
  getMyFavoriteSongList: (userId) => get(`songList/myFavorite/detail?userId=${userId}`),
  
  // 歌单歌曲管理API
  addSongToSongList: ({songListId, songId}) => post(`listSong/add`, {songListId, songId}),
  removeSongFromSongList: (songId) => get(`listSong/delete?songId=${songId}`),
  clearSongList: (songListId) => get(`listSong/clear?songListId=${songListId}`),
  
  // "我喜欢"歌单专用方法
  addSongToMyFavorite: ({songListId, songId}) => post(`listSong/add`, {songListId, songId}),
  clearMyFavoriteSongList: (songListId) => get(`listSong/clear?songListId=${songListId}`),

  // =======================> 评分 API 完成
  // 提交评分
  setRank: ({songListId,consumerId,score}) => post(`rankList/add`, {songListId,consumerId,score}),
  // 获取指定歌单的评分
  getRankOfSongListId: (songListId) => get(`rankList?songListId=${songListId}`),
  // 获取指定用户的歌单评分
  getUserRank: (consumerId, songListId) => get(`/rankList/user?consumerId=${consumerId}&songListId=${songListId}`),

  // =======================> 评论 API 完成
  // 添加评论
  setComment: ({userId,content,songId,songListId,nowType}) => post(`comment/add`, {userId,content,songId,songListId,nowType}),
  // 删除评论
  deleteComment: (id) => get(`comment/delete?id=${id}`),
  // 点赞
  setSupport: ({id,up}) => post(`comment/like`, {id,up}),
  // 返回所有评论
  getAllComment: (type, id) => {
    let url = "";
    if (type === 1) {
      url = `comment/songList/detail?songListId=${id}`;
    } else if (type === 0) {
      url = `comment/song/detail?songId=${id}`;
    }
    return get(url);
  },

  // =======================> 歌曲 API
  // 返回指定歌曲ID的歌曲
  getSongOfId: (id) => get(`song/detail?id=${id}`),
  // 返回指定歌手ID的歌曲
  getSongOfSingerId: (id) => get(`song/singer/detail?singerId=${id}`),
  // 返回指定歌手名的歌曲
  getSongOfSingerName: (keywords) => get(`song/singerName/detail?name=${keywords}`),
  // 获取所有歌曲
  getAllSongs: () => get(`song`),
  // 下载音乐
  downloadMusic: (url) => get(url, { responseType: "blob" }),

  //获取所有的海报
  getBannerList: () => get("banner/getAllBanner"),
  
  // =======================> 每日推荐 API
  // 获取用户每日推荐
  getUserDailyRecommendations: (userId) => {
    if (userId && userId > 0) {
      return get(`recommendation/daily/${userId}`);
    } else {
      return get('recommendation/daily');
    }
  },
  // 获取游客推荐
  getGuestRecommendations: () => get('recommendation/guest'),
  // 手动刷新用户推荐
  generateUserRecommendations: (userId) => post(`recommendation/generate/${userId}`, {}),
  // 获取推荐主题歌单（用于轮播图）
  getRecommendationThemes: () => get('recommendation/themes'),
  // 生成用户推荐歌单
  generateUserRecommendationPlaylists: (userId) => post(`recommendation/playlists/generate/${userId}`, {}),
  // 获取用户推荐歌单
  getUserRecommendationPlaylists: (userId) => get(`recommendation/playlists/${userId}`),
  // 获取游客推荐歌单
  getGuestRecommendationPlaylists: () => get('recommendation/playlists/guest'),
  // 管理员批量生成推荐
  generateAllUserRecommendations: () => post('recommendation/generate/all', {}),
};

export { HttpManager };
