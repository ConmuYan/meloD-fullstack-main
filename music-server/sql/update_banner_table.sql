-- 更新Banner表结构以支持歌单类别轮播图
-- 执行时间：请在应用此脚本前备份数据库

USE tp_music;

-- 为Banner表添加新字段
ALTER TABLE `banner` 
ADD COLUMN `title` VARCHAR(100) DEFAULT NULL COMMENT '轮播图标题',
ADD COLUMN `description` VARCHAR(255) DEFAULT NULL COMMENT '轮播图描述',
ADD COLUMN `category` VARCHAR(20) DEFAULT NULL COMMENT '歌单类别：华语、粤语、欧美、日韩、轻音乐、BGM、乐器',
ADD COLUMN `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
ADD COLUMN `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 清空现有数据（因为要改变数据结构）
DELETE FROM `banner`;

-- 插入新的歌单类别轮播图数据
INSERT INTO `banner` (`pic`, `title`, `description`, `category`, `sort_order`, `is_active`) VALUES
('/img/banners/category_chinese.jpg', '华语音乐', '感受华语音乐的魅力，聆听经典与流行的完美融合', '华语', 1, 1),
('/img/banners/category_cantonese.jpg', '粤语金曲', '粤语歌曲的独特韵味，传承经典粤语文化', '粤语', 2, 1),
('/img/banners/category_western.jpg', '欧美流行', '欧美音乐的国际范儿，体验不同的音乐风格', '欧美', 3, 1),
('/img/banners/category_asian.jpg', '日韩热门', '日韩音乐的时尚潮流，感受亚洲音乐新风尚', '日韩', 4, 1),
('/img/banners/category_light.jpg', '轻音乐', '舒缓心灵的轻音乐，让音乐成为生活的调味剂', '轻音乐', 5, 1),
('/img/banners/category_bgm.jpg', '背景音乐', '精选BGM合集，为你的生活增添美妙背景', 'BGM', 6, 1),
('/img/banners/category_instrument.jpg', '器乐演奏', '纯器乐的艺术魅力，感受音乐最纯粹的表达', '乐器', 7, 1);

-- 创建索引以提高查询性能
CREATE INDEX idx_banner_category ON `banner`(`category`);
CREATE INDEX idx_banner_active_sort ON `banner`(`is_active`, `sort_order`);

-- 显示更新后的表结构
DESCRIBE `banner`;

-- 显示插入的数据
SELECT * FROM `banner` ORDER BY `sort_order`;