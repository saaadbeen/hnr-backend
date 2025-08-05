package com.example.HNR.Repository;

import com.example.HNR.Model.Douar;
import com.example.HNR.Model.StatutDouar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DouarRepository extends MongoRepository<Douar, String> {

    // Recherche par nom
    Optional<Douar> findByNom(String nom);

    // Recherche par statut
    List<Douar> findByStatut(StatutDouar statut);

    // Recherche par préfecture/commune
    List<Douar> findByPrefectureCommune(String prefectureCommune);

    // Recherche par statut et préfecture
    List<Douar> findByStatutAndPrefectureCommune(StatutDouar statut, String prefectureCommune);

    // Recherche par mission associée
    List<Douar> findByIdMission(String idMission);

    // Recherche par nom (insensible à la casse)
    @Query("{'nom': {$regex: ?0, $options: 'i'}}")
    List<Douar> findByNomContainingIgnoreCase(String nom);

    // Compter par statut
    long countByStatut(StatutDouar statut);

    // Compter par préfecture
    long countByPrefectureCommune(String prefectureCommune);

    // Douars ayant des actions
    @Query("{'actions': {$exists: true, $not: {$size: 0}}}")
    List<Douar> findDouarsWithActions();
}