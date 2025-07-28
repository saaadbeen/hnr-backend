package com.example.HNR.repository;

import com.example.HNR.Model.Fichier;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FichierRepository extends MongoRepository<Fichier, String> {
    // Si tu veux récupérer les fichiers d’un douar
    List<Fichier> findByIdDouar(String idDouar);
}
