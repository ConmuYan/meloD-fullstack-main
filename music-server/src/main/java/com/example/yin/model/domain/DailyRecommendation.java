package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("daily_recommendation")
public class DailyRecommendation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer songListId;
    private String theme;
    private BigDecimal score;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}