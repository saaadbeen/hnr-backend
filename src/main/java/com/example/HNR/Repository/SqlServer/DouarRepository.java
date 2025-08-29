package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DouarRepository extends JpaRepository<Douar, Long> {

    // Recherche par nom
    Optional<Douar> findByNom(String nom);

    // Recherche par statut
    List<Douar> findByStatut(StatutDouar statut);

    // Recherche par prefecture
    List<Douar> findByPrefecture(String prefecture);

    // Recherche par commune
    List<Douar> findByCommune(String commune);

    // Recherche par prefecture et commune
    List<Douar> findByPrefectureAndCommune(String prefecture, String commune);

    // Recherche par mission
    List<Douar> findByMissionMissionId(Long missionId);

    // Recherche par créateur
    List<Douar> findByCreatedByUserId(String userId);

    // Douars avec coordonnées
    @Query("SELECT d FROM Douar d WHERE d.latitude IS NOT NULL AND d.longitude IS NOT NULL")
    List<Douar> findDouarsWithCoordinates();

    // Douars non supprimés
    @Query("SELECT d FROM Douar d WHERE d.deletedAt IS NULL")
    List<Douar> findActiveDouars();

    // MÉTHODES DASHBOARD
    // Compter par statut
    long countByStatut(StatutDouar statut);

    // Compter par préfecture
    long countByPrefecture(String prefecture);

    // Compter par préfecture et statut
    long countByPrefectureAndStatut(String prefecture, StatutDouar statut);

    // Préfectures distinctes
    @Query("SELECT DISTINCT d.prefecture FROM Douar d")
    List<String> findDistinctPrefectures();

    // Grouper par préfecture
    @Query("SELECT d.prefecture, COUNT(d) FROM Douar d GROUP BY d.prefecture")
    List<Object[]> countByPrefectureGrouped();
}