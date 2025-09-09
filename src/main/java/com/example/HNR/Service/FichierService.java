package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Fichier;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FichierService {
    Fichier create(Fichier fichier);
    Optional<Fichier> findById(Long id);
    List<Fichier> findAll();
    Fichier update(Fichier fichier);
    void delete(Long id);
    String storeActionPhoto(Long actionId, String kind, MultipartFile file);

    // Méthodes métier spécifiques
    List<Fichier> findByContentType(String contentType);
    List<Fichier> findByUserId(String userId);
    List<Fichier> findByChangementId(Long changementId);
    List<Fichier> findByEntity(String entityType, Long entityId);
    List<Fichier> findImages();
    List<Fichier> findActiveFichiers();
}