package com.example.HNR.Repository;

import com.example.HNR.Model.Mongodb.User;
import com.example.HNR.Model.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Recherche par email (unique)
    Optional<User> findByEmail(String email);

    // Recherche par rôle
    List<User> findByRole(Role role);

    // Recherche par prefecture
    List<User> findByPrefecture(String prefecture);

    // Recherche par commune
    List<User> findByCommune(String commune);

    // Recherche par prefecture et commune
    List<User> findByPrefectureAndCommune(String prefecture, String commune);

    // Vérifier si email existe
    boolean existsByEmail(String email);

    // MÉTHODES DASHBOARD
    // Compter par rôle
    long countByRole(Role role);

    // Compter par préfecture
    long countByPrefecture(String prefecture);
}