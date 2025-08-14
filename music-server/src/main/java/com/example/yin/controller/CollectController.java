package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.CollectRequest;
import com.example.yin.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectController {

    @Autowired
    private CollectService collectService;


    // 添加收藏的歌曲
    //前台界面逻辑
    @PostMapping("/collection/add")
    public R addCollection(@RequestBody CollectRequest addCollectRequest) {
        return collectService.addCollection(addCollectRequest);
    }

    //TODO  这些其实有点偏简单的逻辑  所以就一点 所以放在外面  拿到里面
    // 取消收藏的歌曲
    @DeleteMapping("/collection/delete")
    public R deleteCollection(@RequestParam Integer userId, @RequestParam Integer songId) {
        return collectService.deleteCollect(userId, songId);
    }

    // 是否收藏歌曲
    @PostMapping("/collection/status")
    public R isCollection(@RequestBody CollectRequest isCollectRequest) {
        return collectService.existSongId(isCollectRequest);

    }

    // 返回的指定用户 ID 收藏的列表
    @GetMapping("/collection/detail")
    public R collectionOfUser(@RequestParam Integer userId) {
        return collectService.collectionOfUser(userId);
    }
    
    // 新增歌单收藏相关接口
    
    // 是否收藏歌单
    @PostMapping("/collection/songList/status")
    public R isSongListCollection(@RequestBody CollectRequest isCollectRequest) {
        return collectService.existSongListId(isCollectRequest);
    }
    
    // 取消收藏歌单
    @DeleteMapping("/collection/songList/delete")
    public R deleteSongListCollection(@RequestParam Integer userId, @RequestParam Integer songListId) {
        return collectService.deleteSongListCollect(userId, songListId);
    }
    
    // 返回指定用户收藏的歌单列表
    @GetMapping("/collection/songList/detail")
    public R songListCollectionOfUser(@RequestParam Integer userId, @RequestParam Byte type) {
        return collectService.songListCollectionOfUser(userId, type);
    }
    
    // 返回指定用户收藏的歌曲列表（区分歌曲和歌单）
    @GetMapping("/collection/song/detail")
    public R songCollectionOfUser(@RequestParam Integer userId) {
        return collectService.songCollectionOfUser(userId);
    }
}
