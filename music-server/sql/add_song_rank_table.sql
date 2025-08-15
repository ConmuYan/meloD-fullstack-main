-- 添加歌曲评分表
-- 执行时间：请在应用此脚本前备份数据库

USE tp_music;

-- 创建歌曲评分表
DROP TABLE IF EXISTS `song_rank`;
CREATE TABLE `song_rank` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `song_id` bigint(20) unsigned NOT NULL COMMENT '歌曲ID',
  `consumer_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `score` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评分(1-10分)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_song` (`consumer_id`,`song_id`) COMMENT '用户对同一首歌只能评分一次',
  KEY `idx_song_id` (`song_id`) COMMENT '歌曲ID索引',
  KEY `idx_consumer_id` (`consumer_id`) COMMENT '用户ID索引',
  KEY `idx_score` (`score`) COMMENT '评分索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='歌曲评分表';

-- 显示创建的表结构
DESCRIBE `song_rank`;

-- 插入一些测试数据（可选）
-- INSERT INTO `song_rank` (`song_id`, `consumer_id`, `score`) VALUES
-- (1, 1, 8),
-- (1, 2, 9),
-- (2, 1, 7),
-- (3, 1, 10);

-- 显示插入的数据
-- SELECT * FROM `song_rank` ORDER BY `song_id`, `consumer_id`;