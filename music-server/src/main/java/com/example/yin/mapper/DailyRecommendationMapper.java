package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.DailyRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DailyRecommendationMapper extends BaseMapper<DailyRecommendation> {
    
    List<DailyRecommendation> getUserDailyRecommendations(@Param("userId") Integer userId);
    
    void deleteOldRecommendations(@Param("userId") Integer userId);
}