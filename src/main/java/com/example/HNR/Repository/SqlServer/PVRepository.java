package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.PV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PVRepository extends JpaRepository<PV, Long> {

    // Recherche par numéro unique

    // Recherche par rédacteur
    List<PV> findByRedacteurUserId(String userId);

    // Recherche par action
    Optional<PV> findByActionActionId(Long actionId);

    // Recherche par plage de dates de rédaction
    List<PV> findByDateRedactionBetween(Date startDate, Date endDate);

    // PV avec PDF
    @Query("SELECT p FROM PV p WHERE p.urlPDF IS NOT NULL")
    List<PV> findPVsWithPDF();

    // PV récents
    @Query("SELECT p FROM PV p ORDER BY p.dateRedaction DESC")
    List<PV> findRecentPVs();

    // MÉTHODES DASHBOARD
    // Compter par statut de validation
    long countByValide(boolean valide);

    // Compter par rédacteur
    long countByRedacteurUserId(String userId);

    // Statistiques de validation
    @Query("SELECT p.valide, COUNT(p) FROM PV p GROUP BY p.valide")
    List<Object[]> countByValidationStatusGrouped();
}