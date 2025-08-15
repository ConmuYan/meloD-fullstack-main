package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.SongRank;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRankMapper extends BaseMapper<SongRank> {

    /**
     * 查询歌曲总分
     * @param songId
     * @return
     */
    int selectScoreSum(Long songId);

    /**
     * 查询指定用户对歌曲的评分
     * @param consumerId
     * @param songId
     * @return
     */
    Integer selectUserRank(@Param("consumer_id") Long consumerId, @Param("song_id") Long songId);
}