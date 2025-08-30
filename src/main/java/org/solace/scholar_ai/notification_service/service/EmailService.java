package org.solace.scholar_ai.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:scholarai.official@gmail.com}")
    private String fromEmail;

    @Value("${app.name:ScholarAI}")
    private String appName;

    public void sendWelcomeEmail(String toEmail, String toName, Map<String, Object> templateData) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("Mail credentials not configured. Cannot send welcome email to: {}", toEmail);
            throw new RuntimeException("Mail credentials not configured");
        }

        log.info("Attempting to send welcome email to: {} with from: {}", toEmail, fromEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to " + appName + "!");

            Context context = new Context();
            context.setVariables(templateData);
            String htmlContent = templateEngine.process("welcome-email", context);
            helper.setText(htmlContent, true);

            log.debug("Email content prepared, attempting to send...");
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {} from: {}", toEmail, fromEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {} from: {}", toEmail, fromEmail, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String toName, Map<String, Object> templateData) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("Mail credentials not configured. Cannot send password reset email to: {}", toEmail);
            throw new RuntimeException("Mail credentials not configured");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset - " + appName);

            Context context = new Context();
            context.setVariables(templateData);
            String htmlContent = templateEngine.process("password-reset-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendEmailVerificationEmail(String toEmail, String toName, Map<String, Object> templateData) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("Mail credentials not configured. Cannot send email verification to: {}", toEmail);
            throw new RuntimeException("Mail credentials not configured");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - " + appName);

            Context context = new Context();
            context.setVariables(templateData);
            String htmlContent = templateEngine.process("email-verification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email verification sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email verification to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email verification", e);
        }
    }
}
