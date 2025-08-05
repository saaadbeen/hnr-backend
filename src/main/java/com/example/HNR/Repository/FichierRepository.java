package com.example.HNR.Repository;

import com.example.HNR.Model.Fichier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FichierRepository extends MongoRepository<Fichier, String> {

    // Recherche par nom de fichier
    List<Fichier> findByNomFichierContainingIgnoreCase(String nomFichier);
/**
    // Recherche par type de fichier
    List<Fichier> findByFileType(String fileType);

    // Recherche par utilisateur qui a uploadé
    List<Fichier> findByUploadedBy(String uploadedBy);

    // Recherche par type d'entité liée
    List<Fichier> findByEntityType(String entityType);

    // Recherche par ID entité liée
    List<Fichier> findByEntityId(String entityId);

    // Recherche par type et ID d'entité
    List<Fichier> findByEntityTypeAndEntityId(String entityType, String entityId);

    // Fichiers uploadés entre deux dates
    List<Fichier> findByDateuploadBetween(Date startDate, Date endDate);

    // Fichiers par taille minimale
    @Query("{'fileSize': {$gte: ?0}}")
    List<Fichier> findByFileSizeGreaterThanEqual(long minSize);

    // Fichiers par taille maximale
    @Query("{'fileSize': {$lte: ?0}}")
    List<Fichier> findByFileSizeLessThanEqual(long maxSize);

    // Fichiers récents
    @Query("{'Dateupload': {$gte: ?0}}")
    List<Fichier> findRecentFiles(Date sinceDate);

    // Compter par type de fichier
    long countByFileType(String fileType);

    // Taille totale des fichiers par utilisateur
    @Query(value = "{'uploadedBy': ?0}", fields = "{'fileSize': 1}")
    List<Fichier> findFileSizesByUser(String uploadedBy);*/
}