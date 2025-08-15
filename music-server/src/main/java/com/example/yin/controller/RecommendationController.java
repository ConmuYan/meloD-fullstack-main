package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取每日推荐（支持游客和用户）
     */
    @GetMapping("/daily")
    public R getDailyRecommendations(@RequestParam(required = false) Integer userId) {
        try {
            List<Song> recommendations = recommendationService.getUserDailyRecommendations(userId);
            return R.success("获取成功", recommendations);
        } catch (Exception e) {
            return R.error("获取失败");
        }
    }
    
    /**
     * 获取用户每日推荐（兼容原接口）
     */
    @GetMapping("/daily/{userId}")
    public R getUserDailyRecommendations(@PathVariable Integer userId) {
        try {
            return R.success("获取成功", recommendationService.getUserDailyRecommendations(userId));
        } catch (Exception e) {
            return R.error("获取失败");
        }
    }
    
    /**
     * 获取游客推荐
     */
    @GetMapping("/guest")
    public R getGuestRecommendations() {
        try {
            return R.success("获取成功", recommendationService.getGuestRecommendations());
        } catch (Exception e) {
            return R.error("获取失败");
        }
    }
    
    /**
     * 获取推荐主题（用于轮播图）
     */
    @GetMapping("/themes")
    public R getRecommendationThemes() {
        try {
            return R.success("获取成功", recommendationService.getRecommendationThemes());
        } catch (Exception e) {
            return R.error("获取失败");
        }
    }
    
    /**
     * 手动生成用户推荐
     */
    @PostMapping("/generate/{userId}")
    public R generateUserRecommendations(@PathVariable Integer userId) {
        try {
            return R.success("生成成功", recommendationService.generateUserRecommendations(userId));
        } catch (Exception e) {
            return R.error("生成失败");
        }
    }

    /**
     * 为用户生成推荐歌单
     */
    @PostMapping("/playlists/generate/{userId}")
    public R generateUserRecommendationPlaylists(@PathVariable Integer userId) {
        try {
            return R.success("生成成功", recommendationService.generateUserRecommendationPlaylists(userId));
        } catch (Exception e) {
            return R.error("生成失败");
        }
    }

    /**
     * 获取用户的推荐歌单
     */
    @GetMapping("/playlists/{userId}")
    public R getUserRecommendationPlaylists(@PathVariable Integer userId) {
        try {
            return R.success("获取成功", recommendationService.generateUserRecommendationPlaylists(userId));
        } catch (Exception e) {
            return R.error("获取失败");
        }
    }
}