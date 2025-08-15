package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "song_rank")
@Data
public class SongRank implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long songId;

    private Long consumerId;

    private Integer score;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}