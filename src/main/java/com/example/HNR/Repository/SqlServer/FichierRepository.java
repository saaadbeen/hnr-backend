package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Long> {

    // Recherche par type de contenu
    List<Fichier> findByContentType(String contentType);

    // Recherche par utilisateur
    List<Fichier> findByUploadedByUserId(String userId);

    // Recherche par changement
    List<Fichier> findByChangementChangementId(Long changementId);

    // Recherche par type d'entité
    List<Fichier> findByEntityType(String entityType);

    // Recherche par entité
    List<Fichier> findByEntityTypeAndEntityId(String entityType, Long entityId);

    // Fichiers images
    @Query("SELECT f FROM Fichier f WHERE f.contentType LIKE 'image/%'")
    List<Fichier> findImages();

    // Fichiers non supprimés
    @Query("SELECT f FROM Fichier f WHERE f.deletedAt IS NULL")
    List<Fichier> findActiveFichiers();

    // Taille totale par utilisateur
    @Query("SELECT f.uploadedByUserId, SUM(f.size) FROM Fichier f WHERE f.deletedAt IS NULL GROUP BY f.uploadedByUserId")
    List<Object[]> getTotalSizeByUser();
}