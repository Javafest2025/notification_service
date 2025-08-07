package org.solace.scholar_ai.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.dto.NotificationRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.notification.queue.name}")
    public void handleNotification(NotificationRequest request) {
        log.info(
                "Received notification request: {} for {}", request.getNotificationType(), request.getRecipientEmail());

        try {
            switch (NotificationRequest.NotificationType.valueOf(request.getNotificationType())) {
                case WELCOME_EMAIL:
                    emailService.sendWelcomeEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    break;

                case PASSWORD_RESET:
                    emailService.sendPasswordResetEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    break;

                case EMAIL_VERIFICATION:
                    emailService.sendEmailVerificationEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    break;

                case ACCOUNT_UPDATE:
                    // TODO: Implement account update email
                    log.info("Account update email not yet implemented");
                    break;

                default:
                    log.warn("Unknown notification type: {}", request.getNotificationType());
            }
        } catch (Exception e) {
            log.error(
                    "Failed to process notification: {} for {}",
                    request.getNotificationType(),
                    request.getRecipientEmail(),
                    e);
            // In production, you might want to implement dead letter queue or retry logic
        }
    }
}
