package com.disi.backend.service;

import com.disi.backend.config.RabbitMQConfig;
import com.disi.backend.dto.PostCreatedNotificationEvent;
import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.Notification;
import com.disi.backend.entity.User;
import com.disi.backend.repository.FriendshipRepository;
import com.disi.backend.repository.NotificationRepository;
import com.disi.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handlePostCreatedEvent(PostCreatedNotificationEvent event) {
        try {
            log.info("Consumed post notification event for post {} by user {}", event.getPostId(), event.getSenderId());

            User sender = userRepository.findById(event.getSenderId()).orElse(null);
            if (sender == null) {
                log.error("Sender not found for id {}", event.getSenderId());
                return;
            }

            List<Friendship> friendships = friendshipRepository
                    .findActiveOrPendingFriendships(sender, FriendshipStatus.DECLINED)
                    .stream()
                    .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                    .toList();

            for (Friendship friendship : friendships) {
                User friend = friendship.getRequester().getUserId().equals(sender.getUserId())
                        ? friendship.getAddressee()
                        : friendship.getRequester();

                if (Boolean.TRUE.equals(friend.getIsBanned())) continue;

                Notification notification = new Notification();
                notification.setUserId(friend.getUserId());
                notification.setSenderId(sender.getUserId());
                notification.setType("NEW_POST");
                notification.setReferenceId(event.getPostId());
                notification.setContent(event.getSenderUsername() + " shared a new post");
                notificationRepository.save(notification);
            }

            log.info("Created notifications for {} friends of user {}", friendships.size(), event.getSenderId());
        } catch (Exception e) {
            log.error("Failed to process notification event for post {}: {}", event.getPostId(), e.getMessage());
        }
    }
}
