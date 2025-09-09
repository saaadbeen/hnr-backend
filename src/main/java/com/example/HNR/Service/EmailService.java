package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;

public interface EmailService {

    /**
     * Envoyer un email de bienvenue à un nouvel utilisateur
     * @param user Le nouvel utilisateur
     * @param temporaryPassword Le mot de passe temporaire (si applicable)
     * @param loginUrl URL de connexion à l'application
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendWelcomeEmail(User user, String temporaryPassword, String loginUrl);

    /**
     * Envoyer un email de bienvenue avec un lien de première connexion
     * @param user Le nouvel utilisateur
     * @param activationToken Token d'activation pour première connexion
     * @param activationUrl URL d'activation
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendWelcomeEmailWithActivation(User user, String activationToken, String activationUrl);

    /**
     * Envoyer un email de notification générale
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendNotificationEmail(String to, String subject, String content);

    /**
     * Envoyer un email de réinitialisation de mot de passe
     * @param user Utilisateur concerné
     * @param resetToken Token de réinitialisation
     * @param resetUrl URL de réinitialisation
     * @return true si l'email a été envoyé avec succès
     */
    boolean sendPasswordResetEmail(User user, String resetToken, String resetUrl);

    /**
     * Tester la configuration email
     * @return true si la configuration email fonctionne
     */
    boolean testEmailConfiguration();
}