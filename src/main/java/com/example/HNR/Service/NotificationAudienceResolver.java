package com.example.HNR.Service;

import com.example.HNR.Model.enums.Role;

import java.util.List;
import java.util.Map;

public interface NotificationAudienceResolver {

    /**
     * Résoudre l'audience pour la création d'une mission
     * @param missionId ID de la mission
     * @param creatorUserId ID du créateur
     * @param assignedUserIds IDs des utilisateurs assignés
     * @param prefecture préfecture de la mission
     * @param commune commune de la mission (optionnel)
     * @return Liste des userIds à notifier
     */
    List<String> resolveMissionCreatedAudience(Long missionId, String creatorUserId,
                                               List<String> assignedUserIds,
                                               String prefecture, String commune);

    /**
     * Résoudre l'audience pour l'assignation d'une mission
     * @param missionId ID de la mission
     * @param assignerUserId ID de celui qui assigne
     * @param assignedUserIds IDs des utilisateurs assignés
     * @return Liste des userIds à notifier
     */
    List<String> resolveMissionAssignedAudience(Long missionId, String assignerUserId,
                                                List<String> assignedUserIds);

    /**
     * Résoudre l'audience pour la création d'une action
     * @param actionId ID de l'action
     * @param actionUserId ID de l'utilisateur qui a créé l'action
     * @param prefecture préfecture de l'action
     * @param commune commune de l'action
     * @return Liste des userIds à notifier
     */
    List<String> resolveActionCreatedAudience(Long actionId, String actionUserId,
                                              String prefecture, String commune);

    /**
     * Résoudre l'audience pour la complétion d'une action
     * @param actionId ID de l'action
     * @param actionUserId ID de l'utilisateur qui a complété l'action
     * @param prefecture préfecture de l'action
     * @param commune commune de l'action
     * @return Liste des userIds à notifier
     */
    List<String> resolveActionCompletedAudience(Long actionId, String actionUserId,
                                                String prefecture, String commune);

    /**
     * Résoudre l'audience pour la déclaration d'un changement
     * @param changementId ID du changement
     * @param detectorUserId ID de l'utilisateur qui a détecté le changement
     * @param prefecture préfecture du changement
     * @param commune commune du changement
     * @return Liste des userIds à notifier
     */
    List<String> resolveChangementDeclaredAudience(Long changementId, String detectorUserId,
                                                   String prefecture, String commune);

    /**
     * Résoudre l'audience pour la génération d'un PV
     * @param pvId ID du PV
     * @param actionId ID de l'action liée (optionnel)
     * @param prefecture préfecture du PV
     * @param commune commune du PV
     * @return Liste des userIds à notifier
     */
    List<String> resolvePVGeneratedAudience(Long pvId, Long actionId,
                                            String prefecture, String commune);

    /**
     * Résoudre l'audience pour la création d'un utilisateur
     * @param newUserId ID du nouvel utilisateur
     * @param creatorUserId ID du créateur
     * @param newUserRole Rôle du nouvel utilisateur
     * @param prefecture préfecture du nouvel utilisateur
     * @return Liste des userIds à notifier
     */
    List<String> resolveUserCreatedAudience(String newUserId, String creatorUserId,
                                            Role newUserRole, String prefecture);

    /**
     * Obtenir tous les utilisateurs avec un rôle spécifique
     * @param role Rôle recherché
     * @return Liste des userIds
     */
    List<String> getUsersByRole(Role role);

    /**
     * Obtenir tous les utilisateurs avec un rôle spécifique dans une zone géographique
     * @param role Rôle recherché
     * @param prefecture préfecture (obligatoire)
     * @param commune commune (optionnel)
     * @return Liste des userIds
     */
    List<String> getUsersByRoleAndLocation(Role role, String prefecture, String commune);

    /**
     * Obtenir tous les membres DSI (pour notifications système)
     * @return Liste des userIds des membres DSI
     */
    List<String> getDSIMembers();

    /**
     * Obtenir le gouverneur d'une préfecture
     * @param prefecture préfecture
     * @return userId du gouverneur ou null si non trouvé
     */
    String getGovernorForPrefecture(String prefecture);

    /**
     * Obtenir tous les agents d'autorité dans une zone
     * @param prefecture préfecture
     * @param commune commune (optionnel)
     * @return Liste des userIds des agents d'autorité
     */
    List<String> getAuthorityAgentsForLocation(String prefecture, String commune);

    /**
     * Résoudre une audience personnalisée avec des critères spécifiques
     * @param criteria Map contenant les critères (role, prefecture, commune, etc.)
     * @return Liste des userIds correspondants
     */
    List<String> resolveCustomAudience(Map<String, Object> criteria);
}