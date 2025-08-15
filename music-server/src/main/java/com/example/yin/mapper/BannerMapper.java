package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Banner;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author asus
* @description 针对表【banner】的数据库操作Mapper
* @createDate 2022-06-13 13:13:42
* @Entity generator.domain.Banner
*/
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 查询所有启用的轮播图，按排序顺序
     * @return 轮播图列表
     */
    List<Banner> selectAllActive();

    /**
     * 根据类别查询轮播图
     * @param category 歌单类别
     * @return 轮播图列表
     */
    List<Banner> selectByCategory(@Param("category") String category);

    /**
     * 插入轮播图
     * @param banner 轮播图对象
     * @return 影响行数
     */
    int insert(Banner banner);

    /**
     * 根据主键更新轮播图
     * @param banner 轮播图对象
     * @return 影响行数
     */
    int updateByPrimaryKey(Banner banner);

    /**
     * 根据主键删除轮播图
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Integer id);

}
