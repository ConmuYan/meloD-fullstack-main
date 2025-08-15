package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.SongRankMapper;
import com.example.yin.model.domain.SongRank;
import com.example.yin.model.request.SongRankRequest;
import com.example.yin.service.SongRankService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 歌曲评分服务实现类
 * @author AI Assistant
 */
@Service
public class SongRankServiceImpl extends ServiceImpl<SongRankMapper, SongRank> implements SongRankService {

    @Autowired
    private SongRankMapper songRankMapper;

    @Override
    public R addRank(SongRankRequest songRankAddRequest) {
        SongRank songRank = new SongRank();
        BeanUtils.copyProperties(songRankAddRequest, songRank);
        if (songRankMapper.insert(songRank) > 0) {
            return R.success("评价成功");
        } else {
            return R.error("评价失败");
        }
    }

    @Override
    public R rankOfSongId(Long songId) {
        // 评分总人数如果为 0，则返回0；否则返回计算出的结果
        QueryWrapper<SongRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id", songId);
        Long rankNum = songRankMapper.selectCount(queryWrapper);
        
        if (rankNum <= 0) {
            return R.success(null, 0);
        }
        
        // 使用浮点数计算平均分，保留两位小数
        Integer totalScore = songRankMapper.selectScoreSum(songId);
        double averageScore = Math.round((double) totalScore / rankNum * 100.0) / 100.0;
        
        return R.success(null, averageScore);
    }

    @Override
    public R getUserRank(Long consumerId, Long songId) {
        Integer score = songRankMapper.selectUserRank(consumerId, songId);
        return R.success(null, score);
    }

    @Override
    public R getSongRankStatistics(Long songId) {
        QueryWrapper<SongRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id", songId);
        Long rankNum = songRankMapper.selectCount(queryWrapper);
        
        if (rankNum <= 0) {
            // 返回默认统计信息
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("averageScore", 0.0);
            statistics.put("totalRatings", 0);
            statistics.put("scoreDistribution", new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}); // 1-10分的分布
            return R.success("获取评分统计成功", statistics);
        }
        
        // 计算平均分
        Integer totalScore = songRankMapper.selectScoreSum(songId);
        double averageScore = Math.round((double) totalScore / rankNum * 100.0) / 100.0;
        
        // 获取评分分布
        int[] scoreDistribution = new int[10]; // 1-10分的分布
        for (int i = 1; i <= 10; i++) {
            QueryWrapper<SongRank> scoreQuery = new QueryWrapper<>();
            scoreQuery.eq("song_id", songId).eq("score", i);
            scoreDistribution[i-1] = Math.toIntExact(songRankMapper.selectCount(scoreQuery));
        }
        
        // 构建返回结果
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("averageScore", averageScore);
        statistics.put("totalRatings", rankNum);
        statistics.put("scoreDistribution", scoreDistribution);
        
        return R.success("获取评分统计成功", statistics);
    }
}