package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.model.domain.Banner;

import java.util.List;

/**
* @author asus
* @description 针对表【banner】的数据库操作Service
* @createDate 2022-06-13 13:13:42
*/
public interface BannerService extends IService<Banner> {

    /**
     * 获取所有轮播图（兼容旧接口）
     * @return 轮播图列表
     */
    List<Banner> getAllBanner();

    /**
     * 获取所有启用的轮播图，按排序顺序
     * @return 轮播图列表
     */
    List<Banner> getAllActiveBanner();

    /**
     * 根据类别获取轮播图
     * @param category 歌单类别
     * @return 轮播图列表
     */
    List<Banner> getBannerByCategory(String category);

}
