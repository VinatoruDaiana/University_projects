package com.disi.backend.service;

import com.disi.backend.config.RabbitMQConfig;
import com.disi.backend.dto.PostCreatedNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishPostCreatedEvent(PostCreatedNotificationEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    event
            );
            log.info("Published post notification event for post {} by user {}", event.getPostId(), event.getSenderId());
        } catch (Exception e) {
            log.error("Failed to publish notification event for post {}: {}", event.getPostId(), e.getMessage());
        }
    }
}
