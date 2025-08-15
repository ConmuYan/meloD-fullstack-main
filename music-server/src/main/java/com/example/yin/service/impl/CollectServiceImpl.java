package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.CollectMapper;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.request.CollectRequest;
import com.example.yin.service.CollectService;
import com.example.yin.service.SongListService;
import com.example.yin.service.ListSongService;
import com.example.yin.model.domain.SongList;
import com.example.yin.model.request.ListSongRequest;
import io.lettuce.core.StrAlgoArgs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
    @Autowired
    private CollectMapper collectMapper;
    
    @Autowired
    private SongListService songListService;
    
    @Autowired
    private ListSongService listSongService;

    @Override
    public R addCollection(CollectRequest addCollectRequest) {
        //作者用type来判断收藏的是歌还是歌单
        Collect collect = new Collect();
        BeanUtils.copyProperties(addCollectRequest, collect);
        
        // 如果是收藏歌曲（type=0），需要同时添加到"我喜欢"歌单
        if (addCollectRequest.getType() != null && addCollectRequest.getType() == 0) {
            // 确保用户有"我喜欢"歌单
            R myFavoriteResult = songListService.getMyFavoriteSongList(addCollectRequest.getUserId());
            SongList myFavorite = null;
            
            if (!myFavoriteResult.getSuccess()) {
                // 如果"我喜欢"歌单不存在，创建一个
                R createResult = songListService.createMyFavoriteSongList(addCollectRequest.getUserId());
                if (createResult.getSuccess()) {
                    myFavorite = (SongList) createResult.getData();
                } else {
                    return R.error("创建我喜欢歌单失败");
                }
            } else {
                myFavorite = (SongList) myFavoriteResult.getData();
            }
            
            // 将歌曲添加到"我喜欢"歌单
            if (myFavorite != null) {
                ListSongRequest listSongRequest = new ListSongRequest();
                listSongRequest.setSongId(addCollectRequest.getSongId());
                listSongRequest.setSongListId(myFavorite.getId());
                listSongService.addListSong(listSongRequest);
            }
        }
        
        if (collectMapper.insert(collect) > 0) {
            return R.success("收藏成功", true);
        } else {
            return R.error("收藏失败");
        }
    }

    @Override
    public R existSongId(CollectRequest isCollectRequest) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",isCollectRequest.getUserId());
        queryWrapper.eq("song_id",isCollectRequest.getSongId());
        if (collectMapper.selectCount(queryWrapper) > 0) {
            return R.success("已收藏", true);
        } else {
            return R.success("未收藏", false);
        }
    }

    @Override
    public R deleteCollect(Integer userId, Integer songId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("song_id",songId);
        
        // 同时从"我喜欢"歌单中移除歌曲
        R myFavoriteResult = songListService.getMyFavoriteSongList(userId);
        if (myFavoriteResult.getSuccess()) {
            SongList myFavorite = (SongList) myFavoriteResult.getData();
            if (myFavorite != null) {
                // 从"我喜欢"歌单中删除歌曲
                listSongService.deleteListSongFromList(songId, myFavorite.getId());
            }
        }
        
        if (collectMapper.delete(queryWrapper) > 0) {
            return R.success("取消收藏", false);
        } else {
            return R.error("取消收藏失败");
        }
    }

    @Override
    public R collectionOfUser(Integer userId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        return R.success("用户收藏", collectMapper.selectList(queryWrapper));
    }
    
    @Override
    public R existSongListId(CollectRequest isCollectRequest) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", isCollectRequest.getUserId());
        queryWrapper.eq("song_list_id", isCollectRequest.getSongListId());
        queryWrapper.eq("type", 1); // type=1 表示歌单
        if (collectMapper.selectCount(queryWrapper) > 0) {
            return R.success("已收藏", true);
        } else {
            return R.success("未收藏", false);
        }
    }

    @Override
    public R deleteSongListCollect(Integer userId, Integer songListId, Byte type) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("song_list_id", songListId);
        queryWrapper.eq("type", type);
        if (collectMapper.delete(queryWrapper) > 0) {
            return R.success("取消收藏", false);
        } else {
            return R.error("取消收藏失败");
        }
    }
    
    @Override
    public R songListCollectionOfUser(Integer userId, Byte type) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", type); // type=1 表示歌单,type=2表示自己创建的歌单
        queryWrapper.orderByDesc("create_time"); // 按收藏时间倒序排列
        return R.success("用户收藏的歌单", collectMapper.selectList(queryWrapper));
    }
    
    @Override
    public R songCollectionOfUser(Integer userId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", 0); // type=0 表示歌曲
        queryWrapper.orderByDesc("create_time"); // 按收藏时间倒序排列
        return R.success("用户收藏的歌曲", collectMapper.selectList(queryWrapper));
    }
}
