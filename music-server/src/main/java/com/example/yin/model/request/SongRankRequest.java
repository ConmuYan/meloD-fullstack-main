package com.example.yin.model.request;

import lombok.Data;

/**
 * 歌曲评分请求对象
 * @Author AI Assistant
 * @Time : 2024/12/19
 **/
@Data
public class SongRankRequest {
    private Long id;

    private Long songId;

    private Long consumerId;

    private Integer score;
}