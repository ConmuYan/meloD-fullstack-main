package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.request.CollectRequest;

public interface CollectService extends IService<Collect> {

    R addCollection(CollectRequest addCollectRequest);

    R existSongId(CollectRequest isCollectRequest);

    R deleteCollect(Integer userId,Integer songId);

    R collectionOfUser(Integer userId);
    
    // 新增歌单收藏相关方法
    R existSongListId(CollectRequest isCollectRequest);
    
    R deleteSongListCollect(Integer userId, Integer songListId);
    
    R songListCollectionOfUser(Integer userId);
    
    R songCollectionOfUser(Integer userId);
}
