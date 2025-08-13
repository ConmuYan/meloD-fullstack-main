-- 清理孤立数据脚本
-- 用于清理数据库中因用户删除而产生的孤立记录

-- 1. 查找孤立的评论（用户不存在的评论）
SELECT 
    c.id as comment_id,
    c.user_id,
    c.content,
    c.create_time
FROM comment c
LEFT JOIN consumer u ON c.user_id = u.id
WHERE u.id IS NULL;

-- 2. 删除孤立的评论
DELETE c FROM comment c
LEFT JOIN consumer u ON c.user_id = u.id
WHERE u.id IS NULL;

-- 3. 查找孤立的收藏记录
SELECT 
    col.id as collect_id,
    col.user_id,
    col.song_id,
    col.song_list_id,
    col.create_time
FROM collect col
LEFT JOIN consumer u ON col.user_id = u.id
WHERE u.id IS NULL;

-- 4. 删除孤立的收藏记录
DELETE col FROM collect col
LEFT JOIN consumer u ON col.user_id = u.id
WHERE u.id IS NULL;

-- 5. 查找孤立的点赞记录
SELECT 
    us.id as support_id,
    us.user_id,
    us.comment_id
FROM user_support us
LEFT JOIN consumer u ON us.user_id = u.id
WHERE u.id IS NULL;

-- 6. 删除孤立的点赞记录
DELETE us FROM user_support us
LEFT JOIN consumer u ON us.user_id = u.id
WHERE u.id IS NULL;

-- 7. 统计清理结果
SELECT 
    '清理完成' as status,
    (
        SELECT COUNT(*) FROM comment c
        LEFT JOIN consumer u ON c.user_id = u.id
        WHERE u.id IS NULL
    ) as orphaned_comments,
    (
        SELECT COUNT(*) FROM collect col
        LEFT JOIN consumer u ON col.user_id = u.id
        WHERE u.id IS NULL
    ) as orphaned_collects,
    (
        SELECT COUNT(*) FROM user_support us
        LEFT JOIN consumer u ON us.user_id = u.id
        WHERE u.id IS NULL
    ) as orphaned_supports;