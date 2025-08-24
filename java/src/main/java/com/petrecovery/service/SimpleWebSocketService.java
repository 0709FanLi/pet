package com.petrecovery.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 简化的WebSocket通知服务
 * 暂时不使用真实WebSocket，而是模拟推送行为
 */
@Service
public class SimpleWebSocketService {

    // 模拟WebSocket连接存储
    private static final Map<String, Boolean> userConnections = new ConcurrentHashMap<>();

    /**
     * 模拟用户连接
     */
    public void simulateConnection(String userId) {
        userConnections.put(userId, true);
        System.out.println("模拟用户 " + userId + " WebSocket连接建立");
    }

    /**
     * 模拟用户断开连接
     */
    public void simulateDisconnection(String userId) {
        userConnections.remove(userId);
        System.out.println("模拟用户 " + userId + " WebSocket连接断开");
    }

    /**
     * 发送通知给指定用户
     */
    public void sendNotificationToUser(Long userId, String title, String content, String type, Map<String, Object> extraData) {
        String userIdStr = userId.toString();
        
        if (!userConnections.containsKey(userIdStr)) {
            System.out.println("用户 " + userId + " 的WebSocket连接不存在（模拟）");
            return;
        }

        try {
            // 模拟WebSocket推送
            System.out.println("模拟WebSocket通知推送给用户 " + userId);
            System.out.println("标题: " + title);
            System.out.println("内容: " + content);
            System.out.println("类型: " + type);
            
            if (extraData != null && !extraData.isEmpty()) {
                System.out.println("额外数据: " + extraData);
            }
            
        } catch (Exception e) {
            System.err.println("模拟WebSocket通知发送失败: " + e.getMessage());
        }
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userConnections.size();
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        return userConnections.containsKey(userId.toString());
    }
}
