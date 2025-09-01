package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Fichier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FichierService {
    Fichier create(Fichier fichier);
    Optional<Fichier> findById(Long id);
    Page<Fichier> findAll(Pageable pageable);
    Fichier update(Fichier fichier);
    void delete(Long id);

    // Méthodes métier spécifiques
    List<Fichier> findByContentType(String contentType);
    List<Fichier> findByUserId(String userId);
    List<Fichier> findByChangementId(Long changementId);
    List<Fichier> findByEntity(String entityType, Long entityId);
    List<Fichier> findImages();
    List<Fichier> findActiveFichiers();
}