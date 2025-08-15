package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.SongListMapper;
import com.example.yin.model.domain.SongList;
import com.example.yin.model.request.SongListRequest;
import com.example.yin.service.SongListService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    @Autowired
    private SongListMapper songListMapper;
    @Value("${minio.bucket-name}")
    String bucketName;

    @Override
    public R updateSongListMsg(SongListRequest updateSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(updateSongListRequest, songList);
        if (songListMapper.updateById(songList) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R deleteSongList(Integer id) {
        if (songListMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allSongList() {
        return R.success(null, songListMapper.selectList(null));
    }

    @Override
    public R songListOfId(Integer id) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return R.success(null, songListMapper.selectList(queryWrapper));
    }

    @Override
    public List<SongList> findAllSong() {
        List<SongList> songLists = songListMapper.selectList(null);
        return songLists;
    }


    @Override
    public R likeTitle(String title) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",title);
        return R.success(null, songListMapper.selectList(queryWrapper));
    }

    @Override
    public R likeStyle(String style) {
        QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("style",style);
        return R.success(null, songListMapper.selectList(queryWrapper));
    }

    @Override
    public R addSongList(SongListRequest addSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(addSongListRequest, songList);
        String pic = "/img/songListPic/123.jpg";
        songList.setPic(pic);
        if (songListMapper.insert(songList) > 0) {
            int newId = songList.getId();
            return R.success("添加成功", newId);
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R updateSongListImg(MultipartFile avatorFile, @RequestParam("id") int id) {
        String fileName =avatorFile.getOriginalFilename();
        String path="/"+bucketName+"/"+"songlist/";
        String imgPath = path + fileName;
        MinioUploadController.uploadSonglistImgFile(avatorFile);
        SongList songList = new SongList();
        songList.setId(id);
        songList.setPic(imgPath);
        if (songListMapper.updateById(songList) > 0) {
            return R.success("上传成功", imgPath);
         }
         return R.error("上传失败");
     }
     
     @Override
     public R createMyFavoriteSongList(Integer userId) {
         // 检查用户是否已经有"我喜欢"歌单
         QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
         queryWrapper.eq("title", "我喜欢");
         queryWrapper.eq("style", "我喜欢");
         // 这里需要添加用户ID字段来区分不同用户的"我喜欢"歌单
         // 暂时使用introduction字段存储用户ID
         queryWrapper.eq("introduction", "user_" + userId);
         
         if (songListMapper.selectCount(queryWrapper) > 0) {
             return R.success("我喜欢歌单已存在");
         }
         
         SongList myFavorite = new SongList();
         myFavorite.setTitle("我喜欢");
         myFavorite.setStyle("我喜欢");
         myFavorite.setIntroduction("user_" + userId); // 用于标识用户
         myFavorite.setPic("/img/songListPic/default.jpg"); // 默认封面
         
         if (songListMapper.insert(myFavorite) > 0) {
             return R.success("创建我喜欢歌单成功", myFavorite);
         } else {
             return R.error("创建我喜欢歌单失败");
         }
     }
     
     @Override
     public R getMyFavoriteSongList(Integer userId) {
         QueryWrapper<SongList> queryWrapper = new QueryWrapper<>();
         queryWrapper.eq("title", "我喜欢");
         queryWrapper.eq("style", "我喜欢");
         queryWrapper.eq("introduction", "user_" + userId);
         
         SongList myFavorite = songListMapper.selectOne(queryWrapper);
         if (myFavorite != null) {
             return R.success("获取我喜欢歌单成功", myFavorite);
         } else {
             return R.error("我喜欢歌单不存在");
         }
     }
}
