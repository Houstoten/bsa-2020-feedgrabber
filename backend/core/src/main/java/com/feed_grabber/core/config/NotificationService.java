package com.feed_grabber.core.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public <T> void sendMessageToConcreteUser(String userId, String topic, T payload) {
        simpMessagingTemplate.convertAndSendToUser(userId, "/queue/" + topic, payload);
    }

    public <T> void sendMessageToUsers(List<String> userIds, String topic, T payload) {
        userIds.forEach(n -> sendMessageToConcreteUser(n, topic, payload));
    }

    public <T> void sendMessageToAllUsers(String topic, T payload) {
        simpMessagingTemplate.convertAndSend("/topic/" + topic, payload);
    }
}
