package com.example.HNR.Repository.Mongodb;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Méthodes existantes
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Nouvelles méthodes pour le système de notifications

    /**
     * Trouver tous les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouver tous les utilisateurs par rôle et préfecture
     */
    List<User> findByRoleAndPrefecture(Role role, String prefecture);

    /**
     * Trouver tous les utilisateurs par rôle, préfecture et commune
     */
    List<User> findByRoleAndPrefectureAndCommune(Role role, String prefecture, String commune);

    /**
     * Trouver tous les utilisateurs par préfecture
     */
    List<User> findByPrefecture(String prefecture);

    /**
     * Trouver tous les utilisateurs par préfecture et commune
     */
    List<User> findByPrefectureAndCommune(String prefecture, String commune);

    /**
     * Compter les utilisateurs par rôle
     */
    long countByRole(Role role);

    /**
     * Compter les utilisateurs par préfecture
     */
    long countByPrefecture(String prefecture);

    /**
     * Vérifier l'existence d'utilisateurs avec un rôle spécifique dans une préfecture
     */
    boolean existsByRoleAndPrefecture(Role role, String prefecture);

    /**
     * Vérifier l'existence d'utilisateurs avec un rôle spécifique dans une commune
     */
    boolean existsByRoleAndPrefectureAndCommune(Role role, String prefecture, String commune);

    /**
     * Trouver les utilisateurs créés après une date donnée
     */
    List<User> findByCreatedAtAfter(Date date);

    /**
     * Trouver les utilisateurs créés entre deux dates
     */
    List<User> findByCreatedAtBetween(Date startDate, Date endDate);

    /**
     * Recherche textuelle dans le nom complet
     */
    @Query("{ 'fullName': { $regex: ?0, $options: 'i' } }")
    List<User> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Trouver les utilisateurs par rôle triés par date de création (plus récents en premier)
     */
    List<User> findByRoleOrderByCreatedAtDesc(Role role);

    /**
     * Trouver les utilisateurs par préfecture triés par nom
     */
    List<User> findByPrefectureOrderByFullNameAsc(String prefecture);

    /**
     * Requête personnalisée : trouver tous les gouverneurs
     */
    @Query("{ 'role': 'GOUVERNEUR' }")
    List<User> findAllGovernors();

    /**
     * Requête personnalisée : trouver tous les membres DSI
     */
    @Query("{ 'role': 'MEMBRE_DSI' }")
    List<User> findAllDSIMembers();

    /**
     * Requête personnalisée : trouver tous les agents d'autorité
     */
    @Query("{ 'role': 'AGENT_AUTORITE' }")
    List<User> findAllAuthorityAgents();

    /**
     * Requête personnalisée : statistiques par rôle
     */
    @Query(value = "{}", fields = "{ 'role': 1 }")
    List<User> findAllRoles();

    /**
     * Trouver un utilisateur par son ID utilisateur (pas l'ID MongoDB)
     */
    Optional<User> findByUserid(String userid);

    /**
     * Vérifier l'existence par ID utilisateur
     */
    boolean existsByUserid(String userid);

    /**
     * Supprimer par ID utilisateur
     */
    void deleteByUserid(String userid);

    /**
     * Trouver les utilisateurs actifs (créés dans les 30 derniers jours)
     */
    @Query("{ 'createdAt': { $gte: ?0 } }")
    List<User> findActiveUsers(Date since);

    /**
     * Compter les utilisateurs par rôle et préfecture (aggregation)
     */
    @Query(value = "{ 'role': ?0, 'prefecture': ?1 }", count = true)
    long countByRoleAndPrefecture(Role role, String prefecture);

    /**
     * Recherche avancée : utilisateurs avec critères multiples
     */
    @Query("{ $and: [ " +
            "  { $or: [ { 'role': ?0 }, { ?0: null } ] }, " +
            "  { $or: [ { 'prefecture': { $regex: ?1, $options: 'i' } }, { ?1: null } ] }, " +
            "  { $or: [ { 'commune': { $regex: ?2, $options: 'i' } }, { ?2: null } ] } " +
            "] }")
    List<User> findWithCriteria(Role role, String prefecture, String commune);

    /**
     * Trouver les derniers utilisateurs créés (pour tableau de bord)
     */
    List<User> findTop10ByOrderByCreatedAtDesc();

    /**
     * Trouver les utilisateurs sans commune spécifiée (pour nettoyage de données)
     */
    @Query("{ $or: [ { 'commune': null }, { 'commune': '' } ] }")
    List<User> findUsersWithoutCommune();

    /**
     * Trouver les utilisateurs par email pattern (pour validation)
     */
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<User> findByEmailPattern(String emailPattern);
}