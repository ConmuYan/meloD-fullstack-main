package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.RankListRequest;
import com.example.yin.service.RankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class RankListController {

    @Autowired
    private RankListService rankListService;


    // 提交评分
    @PostMapping("/rankList/add")
    public R addRank(@RequestBody RankListRequest rankListAddRequest, HttpSession session) {
        // 验证用户登录状态
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return R.error("请先登录后再进行评分");
        }
        return rankListService.addRank(rankListAddRequest);
    }

    // 获取指定歌单的评分
    @GetMapping("/rankList")
    public R rankOfSongListId(@RequestParam Long songListId) {
        return rankListService.rankOfSongListId(songListId);
    }

    // 获取指定用户的歌单评分
    @GetMapping("/rankList/user")
    public R getUserRank(@RequestParam(required = false) Long consumerId, @RequestParam Long songListId, HttpSession session) {
        // 如果没有指定consumerId，则从session中获取当前登录用户
        if (consumerId == null) {
            String username = (String) session.getAttribute("username");
            if (username == null) {
                return R.error("请先登录");
            }
            // 这里需要根据username获取consumerId，暂时返回错误提示
            return R.error("请提供用户ID参数");
        }
        R userRank = rankListService.getUserRank(consumerId, songListId);
        return R.success("成功",userRank);
    }


}
