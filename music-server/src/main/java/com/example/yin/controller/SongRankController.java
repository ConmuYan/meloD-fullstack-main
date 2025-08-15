package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.SongRankRequest;
import com.example.yin.service.SongRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class SongRankController {

    @Autowired
    private SongRankService songRankService;

    // 提交歌曲评分
    @PostMapping("/songRank/add")
    public R addRank(@RequestBody SongRankRequest songRankAddRequest, HttpSession session) {
        // 验证用户登录状态
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return R.error("请先登录后再进行评分");
        }
        return songRankService.addRank(songRankAddRequest);
    }

    // 获取指定歌曲的评分
    @GetMapping("/songRank")
    public R rankOfSongId(@RequestParam Long songId) {
        return songRankService.rankOfSongId(songId);
    }

    // 获取指定歌曲的详细评分统计信息
    @GetMapping("/songRank/statistics")
    public R getSongRankStatistics(@RequestParam Long songId) {
        return songRankService.getSongRankStatistics(songId);
    }

    // 获取指定用户的歌曲评分
    @GetMapping("/songRank/user")
    public R getUserRank(@RequestParam(required = false) Long consumerId, @RequestParam Long songId, HttpSession session) {
        // 如果没有指定consumerId，则从session中获取当前登录用户
        if (consumerId == null) {
            String username = (String) session.getAttribute("username");
            if (username == null) {
                return R.error("请先登录");
            }
            // 这里需要根据username获取consumerId，暂时返回错误提示
            return R.error("请提供用户ID参数");
        }
        R userRank = songRankService.getUserRank(consumerId, songId);
        return R.success("成功", userRank);
    }

}