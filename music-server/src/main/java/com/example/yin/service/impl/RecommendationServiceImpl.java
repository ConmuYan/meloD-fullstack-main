package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.*;
import com.example.yin.model.domain.*;
import com.example.yin.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private SongMapper songMapper;
    
    @Autowired
    private CollectMapper collectMapper;
    
    @Autowired
    private SongListMapper songListMapper;
    
    @Autowired
    private ListSongMapper listSongMapper;
    
    @Autowired
    private SingerMapper singerMapper;
    
    // 移除 DailyRecommendationMapper 的依赖
    // @Autowired
    // private DailyRecommendationMapper dailyRecommendationMapper;
    
    @Override
    public List<Song> getUserDailyRecommendations(Integer userId) {
        // 直接动态生成，无需数据库存储
        List<Song> recommendations = new ArrayList<>();
        
        if (userId == null || userId <= 0) {
            // 游客推荐策略
            return getGuestRecommendations();
        }
        
        // 已登录用户推荐策略
        // 1. 基于用户收藏的歌手推荐（40%）
        List<Song> singerBasedSongs = getSongsBasedOnUserFavoriteSingers(userId, 8);
        recommendations.addAll(singerBasedSongs);
        
        // 2. 热门歌曲推荐（30%）
        List<Song> popularSongs = getPopularSongs(6);
        recommendations.addAll(popularSongs);
        
        // 3. 最新歌曲推荐（20%）
        List<Song> latestSongs = getLatestSongs(4);
        recommendations.addAll(latestSongs);
        
        // 4. 随机推荐（10%）
        List<Song> randomSongs = getRandomSongs(2);
        recommendations.addAll(randomSongs);
        
        // 去重并限制数量
        return recommendations.stream()
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }
    
    // 新增游客推荐方法
    public List<Song> getGuestRecommendations() {
        List<Song> recommendations = new ArrayList<>();
        
        // 游客策略：热门(50%) + 最新(30%) + 经典(20%)
        recommendations.addAll(getPopularSongs(10));
        recommendations.addAll(getLatestSongs(6));
        recommendations.addAll(getClassicSongs(4));
        
        return recommendations.stream()
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> generateUserRecommendations(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        List<Song> recommendations = getUserDailyRecommendations(userId);
        result.put("songs", recommendations);
        result.put("generateTime", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        result.put("count", recommendations.size());
        
        return result;
    }
    
    // 私有辅助方法
    private List<Song> getSongsBasedOnUserFavoriteSingers(Integer userId, int limit) {
        try {
            // 获取用户收藏的歌曲，分析喜欢的歌手
            List<Collect> userCollections = collectMapper.selectList(
                new QueryWrapper<Collect>().eq("user_id", userId).eq("type", 0)
            );
            
            if (userCollections.isEmpty()) {
                return getRandomSongs(limit);
            }
            
            // 获取收藏歌曲的歌手ID
            Set<Integer> singerIds = new HashSet<>();
            for (Collect collect : userCollections) {
                Song song = songMapper.selectById(collect.getSongId());
                if (song != null) {
                    singerIds.add(song.getSingerId());
                }
            }
            
            // 基于这些歌手推荐其他歌曲
            List<Song> recommendations = new ArrayList<>();
            for (Integer singerId : singerIds) {
                List<Song> singerSongs = songMapper.selectList(
                    new QueryWrapper<Song>().eq("singer_id", singerId).last("LIMIT 3")
                );
                recommendations.addAll(singerSongs);
                if (recommendations.size() >= limit) break;
            }
            
            return recommendations.stream().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            // 发生异常时返回随机歌曲
            return getRandomSongs(limit);
        }
    }
    
    private List<Song> getPopularSongs(int limit) {
        try {
            // 基于创建时间判断热门程度（实际项目中可以基于播放量、收藏量等）
            return songMapper.selectList(
                new QueryWrapper<Song>().orderByDesc("create_time").last("LIMIT " + limit)
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private List<Song> getLatestSongs(int limit) {
        try {
            return songMapper.selectList(
                new QueryWrapper<Song>().orderByDesc("create_time").last("LIMIT " + limit)
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private List<Song> getClassicSongs(int limit) {
        try {
            // 获取较早创建的歌曲作为经典
            return songMapper.selectList(
                new QueryWrapper<Song>().orderByAsc("create_time").last("LIMIT " + limit)
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private List<Song> getRandomSongs(int limit) {
        try {
            return songMapper.selectList(
                new QueryWrapper<Song>().last("ORDER BY RAND() LIMIT " + limit)
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 为用户生成三种推荐歌单
     * @param userId 用户ID
     * @return 三个推荐歌单的信息
     */
    @Override
    public List<Map<String, Object>> generateUserRecommendationPlaylists(Integer userId) {
        List<Map<String, Object>> playlists = new ArrayList<>();
        
        try {
            // 添加当前日期到歌单ID中，确保每天生成不同的推荐
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 1. 热门推荐歌单
            List<Song> hotSongs = getPopularSongs(15);
            Map<String, Object> hotPlaylist = new HashMap<>();
            hotPlaylist.put("id", "user-hot-" + userId + "-" + today);
            hotPlaylist.put("title", "热门推荐");
            hotPlaylist.put("description", "今日最热门的音乐精选");
            hotPlaylist.put("pic", hotSongs.isEmpty() ? "/img/songListPic/default-hot.jpg" : hotSongs.get(0).getPic());
            hotPlaylist.put("theme", "hot");
            hotPlaylist.put("songCount", hotSongs.size());
            hotPlaylist.put("isRecommendation", true);
            hotPlaylist.put("generateDate", today);
            hotPlaylist.put("songs", hotSongs);
            playlists.add(hotPlaylist);
            
            // 2. 个性推荐歌单
            List<Song> personalSongs = getSongsBasedOnUserFavoriteSingers(userId, 15);
            Map<String, Object> personalPlaylist = new HashMap<>();
            personalPlaylist.put("id", "user-personal-" + userId + "-" + today);
            personalPlaylist.put("title", "个性推荐");
            personalPlaylist.put("description", "基于你的喜好为你推荐");
            personalPlaylist.put("pic", personalSongs.isEmpty() ? "/img/songListPic/default-personal.jpg" : personalSongs.get(0).getPic());
            personalPlaylist.put("theme", "personal");
            personalPlaylist.put("songCount", personalSongs.size());
            personalPlaylist.put("isRecommendation", true);
            personalPlaylist.put("generateDate", today);
            personalPlaylist.put("songs", personalSongs);
            playlists.add(personalPlaylist);
            
            // 3. 新歌推荐歌单
            List<Song> newSongs = getLatestSongs(15);
            Map<String, Object> newPlaylist = new HashMap<>();
            newPlaylist.put("id", "user-new-" + userId + "-" + today);
            newPlaylist.put("title", "新歌推荐");
            newPlaylist.put("description", "最新发布的优质音乐");
            newPlaylist.put("pic", newSongs.isEmpty() ? "/img/songListPic/default-new.jpg" : newSongs.get(0).getPic());
            newPlaylist.put("theme", "new");
            newPlaylist.put("songCount", newSongs.size());
            newPlaylist.put("isRecommendation", true);
            newPlaylist.put("generateDate", today);
            newPlaylist.put("songs", newSongs);
            playlists.add(newPlaylist);
            
        } catch (Exception e) {
            // 异常时返回空列表，前端会使用默认数据
            System.err.println("生成推荐歌单失败: " + e.getMessage());
        }
        
        return playlists;
    }

    @Override
    public List<Map<String, Object>> getGuestRecommendationPlaylists() {
        List<Map<String, Object>> playlists = new ArrayList<>();
        
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 1. 热门推荐歌单（游客版）
            List<Song> hotSongs = getPopularSongs(15);
            Map<String, Object> hotPlaylist = new HashMap<>();
            hotPlaylist.put("id", "guest-hot-" + today);
            hotPlaylist.put("title", "热门推荐");
            hotPlaylist.put("description", "今日最热门的音乐精选");
            hotPlaylist.put("pic", hotSongs.isEmpty() ? "/img/songListPic/default-hot.jpg" : hotSongs.get(0).getPic());
            hotPlaylist.put("theme", "hot");
            hotPlaylist.put("songCount", hotSongs.size());
            hotPlaylist.put("isRecommendation", true);
            hotPlaylist.put("generateDate", today);
            hotPlaylist.put("songs", hotSongs);
            playlists.add(hotPlaylist);
            
            // 2. 新歌推荐歌单（游客版）
            List<Song> newSongs = getLatestSongs(15);
            Map<String, Object> newPlaylist = new HashMap<>();
            newPlaylist.put("id", "guest-new-" + today);
            newPlaylist.put("title", "新歌推荐");
            newPlaylist.put("description", "最新发布的优质音乐");
            newPlaylist.put("pic", newSongs.isEmpty() ? "/img/songListPic/default-new.jpg" : newSongs.get(0).getPic());
            newPlaylist.put("theme", "new");
            newPlaylist.put("songCount", newSongs.size());
            newPlaylist.put("isRecommendation", true);
            newPlaylist.put("generateDate", today);
            newPlaylist.put("songs", newSongs);
            playlists.add(newPlaylist);
            
            // 3. 经典推荐歌单（游客版）
            List<Song> classicSongs = getClassicSongs(15);
            Map<String, Object> classicPlaylist = new HashMap<>();
            classicPlaylist.put("id", "guest-classic-" + today);
            classicPlaylist.put("title", "经典推荐");
            classicPlaylist.put("description", "经典不朽的音乐作品");
            classicPlaylist.put("pic", classicSongs.isEmpty() ? "/img/songListPic/default-classic.jpg" : classicSongs.get(0).getPic());
            classicPlaylist.put("theme", "classic");
            classicPlaylist.put("songCount", classicSongs.size());
            classicPlaylist.put("isRecommendation", true);
            classicPlaylist.put("generateDate", today);
            classicPlaylist.put("songs", classicSongs);
            playlists.add(classicPlaylist);
            
        } catch (Exception e) {
            // 异常时返回空列表
            System.err.println("获取游客推荐歌单失败: " + e.getMessage());
        }
        
        return playlists;
    }

    @Override
    public Map<String, Object> getRecommendationThemes() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> themes = new ArrayList<>();
        
        try {
            // 主题1：今日热门
            List<Song> hotSongs = getPopularSongs(15);
            Map<String, Object> hotTheme = new HashMap<>();
            hotTheme.put("id", "theme-hot");
            hotTheme.put("title", "今日热门");
            hotTheme.put("description", "最受欢迎的音乐推荐");
            hotTheme.put("pic", hotSongs.isEmpty() ? "/img/songListPic/theme-hot.jpg" : hotSongs.get(0).getPic());
            hotTheme.put("theme", "hot");
            hotTheme.put("songCount", hotSongs.size());
            hotTheme.put("isRecommendation", true);
            hotTheme.put("songs", hotSongs);
            themes.add(hotTheme);
            
            // 主题2：新歌推荐
            List<Song> newSongs = getLatestSongs(15);
            Map<String, Object> newTheme = new HashMap<>();
            newTheme.put("id", "theme-new");
            newTheme.put("title", "新歌推荐");
            newTheme.put("description", "最新发布的精选音乐");
            newTheme.put("pic", newSongs.isEmpty() ? "/img/songListPic/theme-new.jpg" : newSongs.get(0).getPic());
            newTheme.put("theme", "new");
            newTheme.put("songCount", newSongs.size());
            newTheme.put("isRecommendation", true);
            newTheme.put("songs", newSongs);
            themes.add(newTheme);
            
            // 主题3：经典回顾
            List<Song> classicSongs = getClassicSongs(15);
            Map<String, Object> classicTheme = new HashMap<>();
            classicTheme.put("id", "theme-classic");
            classicTheme.put("title", "经典回顾");
            classicTheme.put("description", "永不过时的经典音乐");
            classicTheme.put("pic", classicSongs.isEmpty() ? "/img/songListPic/theme-classic.jpg" : classicSongs.get(0).getPic());
            classicTheme.put("theme", "classic");
            classicTheme.put("songCount", classicSongs.size());
            classicTheme.put("isRecommendation", true);
            classicTheme.put("songs", classicSongs);
            themes.add(classicTheme);
            
        } catch (Exception e) {
            System.err.println("获取推荐主题失败: " + e.getMessage());
        }
        
        result.put("themes", themes);
        return result;
    }
}