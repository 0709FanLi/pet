package com.petrecovery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrecovery.config.MqttConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT消息服务类
 */
@Service
public class MqttService {

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送用户通知消息
     */
    public void sendUserNotification(Long userId, String title, String content, String type, Map<String, Object> extraData) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("userId", userId);
            message.put("title", title);
            message.put("content", content);
            message.put("type", type);
            message.put("extraData", extraData);
            message.put("timestamp", LocalDateTime.now().toString());

            String topic = mqttConfig.getTopicPrefix() + "/user/" + userId + "/notifications/" + type;
            String payload = objectMapper.writeValueAsString(message);

            mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .setHeader(MqttHeaders.QOS, 1)
                    .setHeader(MqttHeaders.RETAINED, false)
                    .build()
            );

            System.out.println("MQTT消息已发送 - Topic: " + topic + ", Payload: " + payload);

        } catch (Exception e) {
            System.err.println("发送MQTT消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送订单通知
     */
    public void sendOrderNotification(Long userId, String action, Long orderId, String petName, String detectiveName) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("action", action);
        extraData.put("orderId", orderId);
        extraData.put("petName", petName);
        extraData.put("detectiveName", detectiveName);

        String title = getOrderNotificationTitle(action, petName);
        String content = getOrderNotificationContent(action, petName, detectiveName);

        sendUserNotification(userId, title, content, "order", extraData);
    }

    /**
     * 发送意向通知
     */
    public void sendIntentionNotification(Long ownerId, Long detectiveId, String detectiveName, String petName, Long orderId) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("action", "new_intention");
        extraData.put("detectiveId", detectiveId);
        extraData.put("detectiveName", detectiveName);
        extraData.put("petName", petName);
        extraData.put("orderId", orderId);

        String title = "新的接单意向";
        String content = "侦探 " + detectiveName + " 对您的宠物 " + petName + " 表达了接单意向";

        sendUserNotification(ownerId, title, content, "order", extraData);
    }

    /**
     * 发送确认通知
     */
    public void sendConfirmationNotification(Long detectiveId, String petName, String ownerName) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("action", "intention_confirmed");
        extraData.put("petName", petName);
        extraData.put("ownerName", ownerName);

        String title = "意向被确认";
        String content = "宠物主人 " + ownerName + " 已确认您对宠物 " + petName + " 的接单意向";

        sendUserNotification(detectiveId, title, content, "order", extraData);
    }

    /**
     * 发送拒绝通知
     */
    public void sendRejectionNotification(Long detectiveId, String petName, String reason) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("action", "intention_rejected");
        extraData.put("petName", petName);
        extraData.put("reason", reason);

        String title = "意向被拒绝";
        String content = "很遗憾，您对宠物 " + petName + " 的意向未被选中，感谢您的参与";

        sendUserNotification(detectiveId, title, content, "order", extraData);
    }

    /**
     * 发送推荐通知
     */
    public void sendRecommendationNotification(Long detectiveId, String petName, String location, Long petId) {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("action", "new_recommendation");
        extraData.put("petName", petName);
        extraData.put("location", location);
        extraData.put("petId", petId);

        String title = "新的推荐订单";
        String content = "在您的服务区域 " + location + " 有新的寻宠订单：" + petName;

        sendUserNotification(detectiveId, title, content, "recommend", extraData);
    }

    /**
     * 发送系统公告
     */
    public void sendSystemAnnouncement(Long userId, String title, String content, Map<String, Object> extraData) {
        if (extraData == null) {
            extraData = new HashMap<>();
        }
        extraData.put("action", "system_announcement");

        sendUserNotification(userId, title, content, "system", extraData);
    }

    /**
     * 处理接收到的MQTT消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomingMessage(@Payload String payload, @Header(MqttHeaders.RECEIVED_TOPIC) String topic) {
        try {
            System.out.println("收到MQTT消息 - Topic: " + topic + ", Payload: " + payload);
            
            // 这里可以处理客户端发送的消息，比如在线状态更新等
            if (topic.contains("/status")) {
                handleUserStatusUpdate(payload);
            } else if (topic.contains("/heartbeat")) {
                handleHeartbeat(payload);
            }
            
        } catch (Exception e) {
            System.err.println("处理MQTT消息失败: " + e.getMessage());
        }
    }

    /**
     * 处理用户状态更新
     */
    private void handleUserStatusUpdate(String payload) {
        try {
            Map<String, Object> statusData = objectMapper.readValue(payload, Map.class);
            Long userId = Long.valueOf(statusData.get("userId").toString());
            String status = statusData.get("status").toString();
            
            System.out.println("用户 " + userId + " 状态更新为: " + status);
            
            // 可以在这里更新用户在线状态到数据库
            
        } catch (Exception e) {
            System.err.println("处理用户状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(String payload) {
        try {
            Map<String, Object> heartbeatData = objectMapper.readValue(payload, Map.class);
            Long userId = Long.valueOf(heartbeatData.get("userId").toString());
            
            System.out.println("收到用户 " + userId + " 的心跳");
            
            // 可以在这里更新用户最后活跃时间
            
        } catch (Exception e) {
            System.err.println("处理心跳消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单通知标题
     */
    private String getOrderNotificationTitle(String action, String petName) {
        switch (action) {
            case "started":
                return "订单已开始";
            case "progress_updated":
                return "进度更新";
            case "completed":
                return "订单完成";
            case "cancelled":
                return "订单取消";
            default:
                return "订单状态更新";
        }
    }

    /**
     * 获取订单通知内容
     */
    private String getOrderNotificationContent(String action, String petName, String detectiveName) {
        switch (action) {
            case "started":
                return "侦探 " + detectiveName + " 已开始为您的宠物 " + petName + " 寻找";
            case "progress_updated":
                return "侦探 " + detectiveName + " 更新了宠物 " + petName + " 的寻找进度";
            case "completed":
                return "侦探 " + detectiveName + " 已完成宠物 " + petName + " 的寻找任务";
            case "cancelled":
                return "宠物 " + petName + " 的寻找订单已被取消";
            default:
                return "您的宠物 " + petName + " 订单状态有更新";
        }
    }
}
