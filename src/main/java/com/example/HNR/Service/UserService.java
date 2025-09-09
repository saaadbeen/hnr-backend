package com.example.HNR.Service;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // Méthodes existantes
    User create(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User update(User user);
    void delete(String id);
    boolean existsByEmail(String email);
    boolean deleteByUserid(String userid);

    // Nouvelles méthodes pour le système de notifications

    /**
     * Trouver tous les utilisateurs avec un rôle spécifique
     * @param role Le rôle recherché
     * @return Liste des utilisateurs ayant ce rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouver tous les utilisateurs avec un rôle spécifique dans une localisation
     * @param role Le rôle recherché
     * @param prefecture La préfecture (obligatoire)
     * @param commune La commune (optionnel, peut être null)
     * @return Liste des utilisateurs correspondants
     */
    List<User> findByRoleAndLocation(Role role, String prefecture, String commune);

    /**
     * Trouver tous les utilisateurs dans une préfecture spécifique
     * @param prefecture La préfecture
     * @return Liste des utilisateurs de cette préfecture
     */
    List<User> findByPrefecture(String prefecture);

    /**
     * Trouver tous les utilisateurs dans une commune spécifique
     * @param prefecture La préfecture
     * @param commune La commune
     * @return Liste des utilisateurs de cette commune
     */
    List<User> findByPrefectureAndCommune(String prefecture, String commune);

    /**
     * Trouver les utilisateurs par localisation (alias pour findByPrefectureAndCommune)
     * @param prefecture La préfecture
     * @param commune La commune
     * @return Liste des utilisateurs de cette localisation
     */
    List<User> findByLocation(String prefecture, String commune);

    /**
     * Trouver tous les membres DSI
     * @return Liste des membres DSI
     */
    List<User> findDSIMembers();

    /**
     * Trouver le gouverneur d'une préfecture
     * @param prefecture La préfecture
     * @return Le gouverneur s'il existe, null sinon
     */
    Optional<User> findGovernorByPrefecture(String prefecture);

    /**
     * Trouver tous les agents d'autorité dans une zone
     * @param prefecture La préfecture
     * @param commune La commune (optionnel)
     * @return Liste des agents d'autorité
     */
    List<User> findAuthorityAgentsByLocation(String prefecture, String commune);

    /**
     * Compter le nombre d'utilisateurs par rôle
     * @param role Le rôle
     * @return Nombre d'utilisateurs ayant ce rôle
     */
    long countByRole(Role role);

    /**
     * Vérifier si un utilisateur existe avec un rôle spécifique dans une préfecture
     * @param role Le rôle
     * @param prefecture La préfecture
     * @return true si au moins un utilisateur correspond
     */
    boolean existsByRoleAndPrefecture(Role role, String prefecture);

    /**
     * Obtenir des statistiques utilisateurs par rôle et localisation
     * @return Map avec les statistiques
     */
    java.util.Map<String, Object> getUserStatistics();
}