package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/13 13:16
 **/
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取所有轮播图（兼容旧接口）
     * @return 轮播图列表
     */
    @GetMapping("/getAllBanner")
    public R getAllBanner(){
        return R.success("成功获取轮播图", bannerService.getAllBanner());
    }

    /**
     * 获取所有启用的轮播图，按排序顺序
     * @return 轮播图列表
     */
    @GetMapping("/getActiveBanner")
    public R getActiveBanner(){
        return R.success("成功获取启用的轮播图", bannerService.getAllActiveBanner());
    }

    /**
     * 根据类别获取轮播图
     * @param category 歌单类别
     * @return 轮播图列表
     */
    @GetMapping("/getBannerByCategory")
    public R getBannerByCategory(@RequestParam String category){
        return R.success("成功获取" + category + "类别的轮播图", bannerService.getBannerByCategory(category));
    }
}
