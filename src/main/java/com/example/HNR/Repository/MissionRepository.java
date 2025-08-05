package com.example.HNR.Repository;

import com.example.HNR.Model.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MissionRepository extends MongoRepository<Mission, String> {

    // Recherche par créateur
    List<Mission> findByCreePar(String creePar);

    // Recherche par préfecture/commune
    List<Mission> findByPrefectureCommune(String prefectureCommune);


    // Recherche par utilisateur assigné
    @Query("{'utilisateursAssignes': ?0}")
    List<Mission> findByUtilisateurAssigne(String utilisateurId);


    // Missions envoyées entre deux dates
    List<Mission> findByDateEnvoiBetween(Date startDate, Date endDate);



    // Missions récentes (30 derniers jours)
    @Query("{'dateCreation': {$gte: ?0}}")
    List<Mission> findRecentMissions(Date thirtyDaysAgo);

    // Compter par statut
    long countByStatut(String statut);

    // Missions avec rapport PDF généré
    @Query("{'rapportPDF': {$exists: true, $ne: null}}")
    List<Mission> findMissionsWithReport();
}