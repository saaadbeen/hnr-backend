package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    // Recherche par statut
    List<Mission> findByStatut(String statut);

    // Recherche par prefecture
    List<Mission> findByPrefecture(String prefecture);

    // Recherche par commune
    List<Mission> findByCommune(String commune);

    // Recherche par prefecture et commune
    List<Mission> findByPrefectureAndCommune(String prefecture, String commune);

    // Recherche par créateur
    List<Mission> findByCreatedByUserId(String userId);

    // Recherche par plage de dates
    List<Mission> findByDateEnvoiBetween(Date startDate, Date endDate);

    // Missions terminées
    @Query("SELECT m FROM Mission m WHERE m.statut = 'TERMINEE'")
    List<Mission> findCompletedMissions();

    // Missions en cours
    @Query("SELECT m FROM Mission m WHERE m.statut != 'TERMINEE'")
    List<Mission> findActiveMissions();

    // Missions avec rapport PDF
    @Query("SELECT m FROM Mission m WHERE m.rapportPDF IS NOT NULL")
    List<Mission> findMissionsWithReport();

    // MÉTHODES DASHBOARD
    // Compter par statut
    long countByStatut(String statut);

    // Statistiques par statut
    @Query("SELECT m.statut, COUNT(m) FROM Mission m GROUP BY m.statut")
    List<Object[]> countByStatutGrouped();
}
