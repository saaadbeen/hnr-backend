package com.example.HNR.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust:smtp.gmail.com}")
    private String sslTrust;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        if (mailUsername != null && !mailUsername.isEmpty()) {
            mailSender.setUsername(mailUsername);
        }

        if (mailPassword != null && !mailPassword.isEmpty()) {
            mailSender.setPassword(mailPassword);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.ssl.trust", sslTrust);
        props.put("mail.debug", "false"); // Passer à true pour debug SMTP

        // Configuration SSL/TLS
        if (mailPort == 465) {
            // Port SSL
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else if (mailPort == 587) {
            // Port TLS
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        // Timeouts
        props.put("mail.smtp.connectiontimeout", "20000"); // 20 secondes
        props.put("mail.smtp.timeout", "20000");
        props.put("mail.smtp.writetimeout", "20000");

        log.info("Email configuration initialized - Host: {}, Port: {}, Auth: {}, STARTTLS: {}",
                mailHost, mailPort, smtpAuth, starttlsEnable);

        return mailSender;
    }

    /**
     * Configuration pour différents fournisseurs d'email
     */
    public static class EmailProviders {

        public static Properties getGmailProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            return props;
        }

        public static Properties getOutlookProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp-mail.outlook.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp-mail.outlook.com");
            return props;
        }

        public static Properties getYahooProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.mail.yahoo.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.mail.yahoo.com");
            return props;
        }
    }
}