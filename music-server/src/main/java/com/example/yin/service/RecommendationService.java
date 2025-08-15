package com.example.yin.service;

import com.example.yin.model.domain.Song;
import java.util.List;
import java.util.Map;

public interface RecommendationService {
    
    /**
     * 获取用户每日推荐歌曲
     * @param userId 用户ID，null表示游客
     * @return 推荐歌曲列表
     */
    List<Song> getUserDailyRecommendations(Integer userId);
    
    /**
     * 获取推荐主题歌单（用于轮播图展示）
     * @return 三个主题的推荐歌单
     */
    Map<String, Object> getRecommendationThemes();
    
    /**
     * 生成用户个性化推荐
     * @param userId 用户ID
     * @return 推荐结果
     */
    Map<String, Object> generateUserRecommendations(Integer userId);
    
    /**
     * 为用户生成推荐歌单
     * @param userId 用户ID
     * @return 推荐歌单列表
     */
    List<Map<String, Object>> generateUserRecommendationPlaylists(Integer userId);
    
    /**
     * 获取游客推荐
     * @return 游客推荐歌曲列表
     */
    List<Song> getGuestRecommendations();
    
    /**
     * 获取游客推荐歌单
     * @return 游客推荐歌单列表
     */
    List<Map<String, Object>> getGuestRecommendationPlaylists();
    
    /**
     * 获取用户的推荐歌单（已生成的）
     * @param userId 用户ID
     * @return 用户推荐歌单列表
     */
    List<Map<String, Object>> getUserRecommendationPlaylists(Integer userId);
}