package com.example.HNR.Repository;

import com.example.HNR.Model.Action;
import com.example.HNR.Model.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;



public interface  UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByEmail(String email);

}
