package com.example.yin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.mapper.BannerMapper;
import com.example.yin.model.domain.Banner;
import com.example.yin.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author asus
 * @description 针对表【banner】的数据库操作Service实现
 * @createDate 2022-06-13 13:13:42
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner>
        implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Cacheable(value = "banner", key = "'list'")
    @Override
    public List<Banner> getAllBanner() {
        System.out.println("没有走缓存");
        return bannerMapper.selectList(null);
    }

    @Cacheable(value = "banner", key = "'active_list'")
    @Override
    public List<Banner> getAllActiveBanner() {
        System.out.println("获取启用的轮播图，没有走缓存");
        return bannerMapper.selectAllActive();
    }

    @Cacheable(value = "banner", key = "'category_' + #category")
    @Override
    public List<Banner> getBannerByCategory(String category) {
        System.out.println("根据类别获取轮播图：" + category + "，没有走缓存");
        return bannerMapper.selectByCategory(category);
    }
}
