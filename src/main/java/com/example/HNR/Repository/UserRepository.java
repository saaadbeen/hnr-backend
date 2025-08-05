package com.example.HNR.Repository;

import com.example.HNR.Model.User;
import com.example.HNR.Model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Recherche par email (pour l'authentification)
    Optional<User> findByEmail(String email);

    // Vérifier si email existe déjà
    boolean existsByEmail(String email);

    // Recherche par rôle
    List<User> findByRole(Role role);

    // Recherche par préfecture/commune
    List<User> findByPrefectureCommune(String prefectureCommune);

    // Recherche par rôle et préfecture
    List<User> findByRoleAndPrefectureCommune(Role role, String prefectureCommune);

    // Recherche par nom (insensible à la casse)
    @Query("{'fullName': {$regex: ?0, $options: 'i'}}")
    List<User> findByFullNameContainingIgnoreCase(String fullName);

    // Compter par rôle
    long countByRole(Role role);
}