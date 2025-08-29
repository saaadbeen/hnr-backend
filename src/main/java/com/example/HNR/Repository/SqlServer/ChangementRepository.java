package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.enums.TypeExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangementRepository extends JpaRepository<Changement, Long> {

    // Recherche par type d'extension
    List<Changement> findByType(TypeExtension type);

    // Recherche par douar
    List<Changement> findByDouarDouarId(Long douarId);

    // Recherche par utilisateur détecteur
    List<Changement> findByDetectedByUserId(String userId);

    // Recherche par plage de dates (avant)
    List<Changement> findByDateBeforeBetween(Date startDate, Date endDate);

    // Changements avec photos
    @Query("SELECT c FROM Changement c WHERE c.photoBefore IS NOT NULL AND c.photoAfter IS NOT NULL")
    List<Changement> findChangementsWithPhotos();

    // Changements par surface minimale
    List<Changement> findBySurfaceGreaterThanEqual(Double minSurface);

    // MÉTHODES DASHBOARD
    // Compter par type
    long countByType(TypeExtension type);

    // Compter par utilisateur détecteur
    long countByDetectedByUserId(String userId);

    // Compter par préfecture de douar
    @Query("SELECT COUNT(c) FROM Changement c WHERE c.douar.prefecture = :prefecture")
    long countByDouarPrefecture(@Param("prefecture") String prefecture);

    // Somme des surfaces
    @Query("SELECT SUM(c.surface) FROM Changement c")
    Optional<Double> sumSurface();

    // Statistiques par type
    @Query("SELECT c.type, COUNT(c), AVG(c.surface) FROM Changement c GROUP BY c.type")
    List<Object[]> getStatsByType();
}