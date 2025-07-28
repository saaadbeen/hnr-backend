package com.example.HNR.repository;

import com.example.HNR.Model.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface  UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByLogin(String login);
    Optional<Utilisateur> findByEmail(String email);

}
