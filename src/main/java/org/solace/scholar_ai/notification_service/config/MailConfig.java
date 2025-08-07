package org.solace.scholar_ai.notification_service.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Slf4j
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.protocol:smtp}")
    private String protocol;

    @Bean
    public JavaMailSender javaMailSender() {
        log.info("Configuring JavaMailSender with host: {}, port: {}, username: {}", host, port, username);

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            log.error(
                    "Mail credentials are not configured. Please set GMAIL_ADDRESS and GMAIL_APP_PASSWORD environment variables.");
            throw new IllegalStateException("Mail credentials are not configured");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.debug", "true");
        props.put("mail.debug.auth", "true");

        log.info("JavaMailSender configured successfully");
        return mailSender;
    }
}
