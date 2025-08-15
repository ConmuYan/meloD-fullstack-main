package com.example.yin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户会话管理器
 * 实现基于队列的会话管理，支持多设备登录和先进先出逻辑
 */
@Component
public class UserSessionManager {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 会话队列的最大长度（每个用户最多保持的活跃会话数）
    private static final int MAX_SESSIONS_PER_USER = 3;
    
    // 会话活跃时间（分钟）
    private static final int SESSION_ACTIVE_MINUTES = 10;
    
    // 会话心跳检测间隔（分钟）
    private static final int HEARTBEAT_INTERVAL_MINUTES = 1;
    
    /**
     * 用户登录时添加会话
     * @param username 用户名
     * @param sessionId 会话ID
     * @param deviceInfo 设备信息（可选）
     */
    public void addUserSession(String username, String sessionId, String deviceInfo) {
        String queueKey = "user_sessions:" + username;
        String sessionData = createSessionData(sessionId, deviceInfo);
        
        // 移除可能存在的旧会话（同一sessionId）
        removeSessionFromQueue(queueKey, sessionId);
        
        // 添加新会话到队列头部
        stringRedisTemplate.opsForList().leftPush(queueKey, sessionData);
        
        // 如果队列长度超过最大限制，移除最旧的会话（队列尾部）
        Long queueSize = stringRedisTemplate.opsForList().size(queueKey);
        if (queueSize != null && queueSize > MAX_SESSIONS_PER_USER) {
            // 移除最旧的会话
            String oldestSession = stringRedisTemplate.opsForList().rightPop(queueKey);
            if (oldestSession != null) {
                System.out.println("移除最旧会话: " + oldestSession);
            }
        }
        
        // 设置队列过期时间
        stringRedisTemplate.expire(queueKey, SESSION_ACTIVE_MINUTES * 2, TimeUnit.MINUTES);
        
        // 更新会话活跃时间
        updateSessionActivity(username, sessionId);
    }
    
    /**
     * 用户登出时移除会话
     * @param username 用户名
     * @param sessionId 会话ID
     */
    public void removeUserSession(String username, String sessionId) {
        String queueKey = "user_sessions:" + username;
        String activityKey = "session_activity:" + username + ":" + sessionId;
        
        // 从队列中移除会话
        removeSessionFromQueue(queueKey, sessionId);
        
        // 删除活跃时间记录
        stringRedisTemplate.delete(activityKey);
        
        // 如果队列为空，删除整个队列
        Long queueSize = stringRedisTemplate.opsForList().size(queueKey);
        if (queueSize == null || queueSize == 0) {
            stringRedisTemplate.delete(queueKey);
        }
    }
    
    /**
     * 检查用户是否在线
     * @param username 用户名
     * @return 是否在线
     */
    public boolean isUserOnline(String username) {
        String queueKey = "user_sessions:" + username;
        
        // 清理过期会话
        cleanExpiredSessions(username);
        
        // 检查是否还有活跃会话
        Long queueSize = stringRedisTemplate.opsForList().size(queueKey);
        return queueSize != null && queueSize > 0;
    }
    
    /**
     * 更新会话活跃时间（心跳）
     * @param username 用户名
     * @param sessionId 会话ID
     */
    public void updateSessionActivity(String username, String sessionId) {
        String activityKey = "session_activity:" + username + ":" + sessionId;
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        stringRedisTemplate.opsForValue().set(activityKey, currentTime, 
            SESSION_ACTIVE_MINUTES, TimeUnit.MINUTES);
    }
    
    /**
     * 获取用户的活跃会话数量
     * @param username 用户名
     * @return 活跃会话数量
     */
    public int getActiveSessionCount(String username) {
        cleanExpiredSessions(username);
        String queueKey = "user_sessions:" + username;
        Long queueSize = stringRedisTemplate.opsForList().size(queueKey);
        return queueSize != null ? queueSize.intValue() : 0;
    }
    
    /**
     * 强制清除用户的所有会话（用于强制删除用户时）
     * @param username 用户名
     */
    public void clearAllUserSessions(String username) {
        String queueKey = "user_sessions:" + username;
        
        // 获取所有会话并清理活跃时间记录
        List<String> sessions = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        if (sessions != null) {
            for (String sessionData : sessions) {
                String sessionId = extractSessionId(sessionData);
                if (sessionId != null) {
                    String activityKey = "session_activity:" + username + ":" + sessionId;
                    stringRedisTemplate.delete(activityKey);
                }
            }
        }
        
        // 删除会话队列
        stringRedisTemplate.delete(queueKey);
    }
    
    /**
     * 创建会话数据字符串
     * @param sessionId 会话ID
     * @param deviceInfo 设备信息
     * @return 会话数据字符串
     */
    private String createSessionData(String sessionId, String deviceInfo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return sessionId + "|" + timestamp + "|" + (deviceInfo != null ? deviceInfo : "unknown");
    }
    
    /**
     * 从会话数据中提取会话ID
     * @param sessionData 会话数据字符串
     * @return 会话ID
     */
    private String extractSessionId(String sessionData) {
        if (sessionData != null && sessionData.contains("|")) {
            return sessionData.split("\\|")[0];
        }
        return sessionData;
    }
    
    /**
     * 从队列中移除指定的会话
     * @param queueKey 队列键
     * @param sessionId 要移除的会话ID
     */
    private void removeSessionFromQueue(String queueKey, String sessionId) {
        List<String> sessions = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        if (sessions != null) {
            for (String sessionData : sessions) {
                if (sessionData.startsWith(sessionId + "|")) {
                    stringRedisTemplate.opsForList().remove(queueKey, 1, sessionData);
                    break;
                }
            }
        }
    }
    
    /**
     * 清理过期的会话
     * @param username 用户名
     */
    private void cleanExpiredSessions(String username) {
        String queueKey = "user_sessions:" + username;
        List<String> sessions = stringRedisTemplate.opsForList().range(queueKey, 0, -1);
        
        if (sessions != null) {
            for (String sessionData : sessions) {
                String sessionId = extractSessionId(sessionData);
                if (sessionId != null) {
                    String activityKey = "session_activity:" + username + ":" + sessionId;
                    String lastActivity = stringRedisTemplate.opsForValue().get(activityKey);
                    
                    // 如果活跃时间记录不存在，说明会话已过期
                    if (lastActivity == null) {
                        stringRedisTemplate.opsForList().remove(queueKey, 1, sessionData);
                    }
                }
            }
        }
    }
}