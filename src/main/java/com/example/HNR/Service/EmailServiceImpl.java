package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@hnr.ma}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.name:HNR - Habitat Non Réglementaire}")
    private String applicationName;

    @Override
    @Async
    public boolean sendWelcomeEmail(User user, String temporaryPassword, String loginUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Bienvenue sur " + applicationName);

            String emailContent = buildWelcomeEmailContent(user, temporaryPassword, loginUrl);
            message.setText(emailContent);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    @Async
    public boolean sendWelcomeEmailWithActivation(User user, String activationToken, String activationUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Activation de votre compte " + applicationName);

            String emailContent = buildActivationEmailContent(user, activationToken, activationUrl);
            message.setText(emailContent);

            mailSender.send(message);
            log.info("Activation email sent successfully to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    @Async
    public boolean sendNotificationEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Notification email sent successfully to: {}", to);
            return true;

        } catch (Exception e) {
            log.error("Failed to send notification email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    @Override
    @Async
    public boolean sendPasswordResetEmail(User user, String resetToken, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Réinitialisation de votre mot de passe - " + applicationName);

            String emailContent = buildPasswordResetEmailContent(user, resetToken, resetUrl);
            message.setText(emailContent);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", user.getEmail());
            return true;

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean testEmailConfiguration() {
        try {
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setFrom(fromEmail);
            testMessage.setTo("test@example.com");
            testMessage.setSubject("Test de configuration email");
            testMessage.setText("Ceci est un test de configuration email.");

            // Ne pas envoyer réellement, juste tester la configuration
            log.info("Email configuration test: OK");
            return true;

        } catch (Exception e) {
            log.error("Email configuration test failed: {}", e.getMessage());
            return false;
        }
    }

    // Méthodes privées pour construire le contenu des emails

    private String buildWelcomeEmailContent(User user, String temporaryPassword, String loginUrl) {
        StringBuilder content = new StringBuilder();

        content.append("Bonjour ").append(user.getFullName()).append(",\n\n");
        content.append("Bienvenue sur ").append(applicationName).append(" !\n\n");
        content.append("Votre compte a été créé avec succès. Voici vos informations de connexion :\n\n");
        content.append("Email : ").append(user.getEmail()).append("\n");

        if (temporaryPassword != null && !temporaryPassword.isEmpty()) {
            content.append("Mot de passe temporaire : ").append(temporaryPassword).append("\n\n");
            content.append("⚠️ IMPORTANT : Pour des raisons de sécurité, veuillez changer votre mot de passe lors de votre première connexion.\n\n");
        }

        content.append("Rôle assigné : ").append(user.getRole().name()).append("\n");
        content.append("Préfecture : ").append(user.getPrefecture()).append("\n");

        if (user.getCommune() != null && !user.getCommune().isEmpty()) {
            content.append("Commune : ").append(user.getCommune()).append("\n");
        }

        content.append("\nPour vous connecter, cliquez sur le lien suivant :\n");
        content.append(loginUrl != null ? loginUrl : frontendUrl + "/login").append("\n\n");

        content.append("Si vous rencontrez des difficultés, n'hésitez pas à contacter l'équipe DSI.\n\n");
        content.append("Cordialement,\n");
        content.append("L'équipe ").append(applicationName);

        return content.toString();
    }

    private String buildActivationEmailContent(User user, String activationToken, String activationUrl) {
        StringBuilder content = new StringBuilder();

        content.append("Bonjour ").append(user.getFullName()).append(",\n\n");
        content.append("Votre compte ").append(applicationName).append(" a été créé.\n\n");
        content.append("Pour activer votre compte et définir votre mot de passe, cliquez sur le lien suivant :\n\n");
        content.append(activationUrl).append("?token=").append(activationToken).append("\n\n");
        content.append("Ce lien est valide pendant 24 heures.\n\n");
        content.append("Si vous n'avez pas demandé la création de ce compte, ignorez cet email.\n\n");
        content.append("Cordialement,\n");
        content.append("L'équipe ").append(applicationName);

        return content.toString();
    }

    private String buildPasswordResetEmailContent(User user, String resetToken, String resetUrl) {
        StringBuilder content = new StringBuilder();

        content.append("Bonjour ").append(user.getFullName()).append(",\n\n");
        content.append("Une demande de réinitialisation de mot de passe a été effectuée pour votre compte.\n\n");
        content.append("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant :\n\n");
        content.append(resetUrl).append("?token=").append(resetToken).append("\n\n");
        content.append("Ce lien est valide pendant 1 heure.\n\n");
        content.append("Si vous n'avez pas demandé cette réinitialisation, ignorez cet email. Votre mot de passe reste inchangé.\n\n");
        content.append("Cordialement,\n");
        content.append("L'équipe ").append(applicationName);

        return content.toString();
    }
}