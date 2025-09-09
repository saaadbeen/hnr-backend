package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT d FROM Douar d WHERE d.mission.missionId = :missionId")
    List<Douar> findByMMissionId(@Param("missionId") Long missionId);

    // Recherche par créateur
    List<Douar> findByCreatedByUserId(String userId);

    // Douars avec coordonnées

    // 🔹 Filtre flexible (utilisé par le contrôleur: /api/douars?prefecture=&commune=)
    @Query("""
       SELECT d FROM Douar d
       WHERE (:prefecture IS NULL OR d.prefecture = :prefecture)
         AND (:commune IS NULL OR d.commune = :commune)
    """)
    List<Douar> findByLocation(@Param("prefecture") String prefecture,
                               @Param("commune") String commune);

    // Douars non supprimés (si tu utilises deletedAt)
    @Query("SELECT d FROM Douar d WHERE d.deletedAt IS NULL")
    List<Douar> findActiveDouars();

    // MÉTHODES DASHBOARD
    long countByStatut(StatutDouar statut);
    long countByPrefecture(String prefecture);
    long countByPrefectureAndStatut(String prefecture, StatutDouar statut);

    @Query("SELECT DISTINCT d.prefecture FROM Douar d")
    List<String> findDistinctPrefectures();

    @Query("SELECT d.prefecture, COUNT(d) FROM Douar d GROUP BY d.prefecture")
    List<Object[]> countByPrefectureGrouped();

    boolean existsByNomAndCommuneAndPrefecture(String nom, String commune, String prefecture);
}
