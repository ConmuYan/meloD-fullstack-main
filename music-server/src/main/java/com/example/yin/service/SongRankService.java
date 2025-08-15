package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.SongRank;
import com.example.yin.model.request.SongRankRequest;

public interface SongRankService extends IService<SongRank> {

    R addRank(SongRankRequest songRankAddRequest);

    R rankOfSongId(Long songId);

    R getUserRank(Long consumerId, Long songId);

    R getSongRankStatistics(Long songId);

}