import { createStore } from "vuex";
import configure from "./configure";
import user from "./user";
import song from "./song";

export default createStore({
  modules: {
    configure,
    user,
    song,
  },
  state: {
    currentRecommendationPlaylist: null,
  },
  mutations: {
    setCurrentRecommendationPlaylist(state, playlist) {
      state.currentRecommendationPlaylist = playlist;
    },
  },
});
