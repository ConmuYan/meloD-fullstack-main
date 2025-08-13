package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.model.domain.Comment;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.domain.UserSupport;
import com.example.yin.service.impl.ConsumerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 级联删除功能测试
 * 测试删除用户时是否正确清理相关数据
 */
@SpringBootTest
@Transactional
@Rollback
public class CascadeDeleteTest {

    @Autowired
    private ConsumerService consumerService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CollectService collectService;
    
    @Autowired
    private UserSupportService userSupportService;

    @Test
    public void testCascadeDelete() {
        // 假设用户ID为1的用户存在
        Integer userId = 1;
        
        // 检查删除前的数据
        QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
        commentQuery.eq("user_id", userId);
        long commentCountBefore = commentService.count(commentQuery);
        
        QueryWrapper<Collect> collectQuery = new QueryWrapper<>();
        collectQuery.eq("user_id", userId);
        long collectCountBefore = collectService.count(collectQuery);
        
        QueryWrapper<UserSupport> supportQuery = new QueryWrapper<>();
        supportQuery.eq("user_id", userId);
        long supportCountBefore = userSupportService.count(supportQuery);
        
        System.out.println("删除前 - 评论数量: " + commentCountBefore + 
                          ", 收藏数量: " + collectCountBefore + 
                          ", 点赞数量: " + supportCountBefore);
        
        // 执行级联删除
        consumerService.deleteUser(userId);
        
        // 检查删除后的数据
        long commentCountAfter = commentService.count(commentQuery);
        long collectCountAfter = collectService.count(collectQuery);
        long supportCountAfter = userSupportService.count(supportQuery);
        
        System.out.println("删除后 - 评论数量: " + commentCountAfter + 
                          ", 收藏数量: " + collectCountAfter + 
                          ", 点赞数量: " + supportCountAfter);
        
        // 验证所有相关数据都被删除
        assertEquals(0, commentCountAfter, "用户评论应该被完全删除");
        assertEquals(0, collectCountAfter, "用户收藏应该被完全删除");
        assertEquals(0, supportCountAfter, "用户点赞记录应该被完全删除");
    }
}