-- 每日推荐表
CREATE TABLE `daily_recommendation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `song_list_id` int(10) unsigned NOT NULL COMMENT '推荐歌单ID',
  `theme` varchar(50) NOT NULL COMMENT '推荐主题：热门推荐/个性推荐/新歌推荐',
  `score` decimal(5,2) DEFAULT '0.00' COMMENT '推荐分数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_theme` (`theme`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='每日推荐表';

-- 推荐主题歌单表
CREATE TABLE `recommendation_theme_playlist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `theme` varchar(50) NOT NULL COMMENT '主题名称',
  `title` varchar(100) NOT NULL COMMENT '歌单标题',
  `pic` varchar(255) DEFAULT NULL COMMENT '歌单封面',
  `description` varchar(500) DEFAULT NULL COMMENT '歌单描述',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_theme` (`theme`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推荐主题歌单表';

-- 插入三个主题歌单
INSERT INTO `recommendation_theme_playlist` (`theme`, `title`, `pic`, `description`, `create_time`, `update_time`) VALUES
('热门推荐', '今日热门精选', '/img/swiper/hot_recommendation.jpg', '根据全站播放量和收藏数据精选的热门歌曲', NOW(), NOW()),
('个性推荐', '为你推荐', '/img/swiper/personal_recommendation.jpg', '基于你的收藏和播放历史个性化推荐', NOW(), NOW()),
('新歌推荐', '新歌首发', '/img/swiper/new_recommendation.jpg', '最新发布的优质歌曲推荐', NOW(), NOW());