package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Fichier;
import com.example.HNR.Repository.SqlServer.FichierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FichierServiceImpl implements FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    @Override
    public Fichier create(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    @Override
    public Optional<Fichier> findById(Long id) {
        return fichierRepository.findById(id);
    }

    @Override
    public List<Fichier> findAll() {
        return fichierRepository.findAll();
    }

    @Override
    public Fichier update(Fichier fichier) {
        return fichierRepository.save(fichier);
    }

    @Override
    public void delete(Long id) {
        Optional<Fichier> fichier = fichierRepository.findById(id);
        if (fichier.isPresent()) {
            fichier.get().softDelete();
            fichierRepository.save(fichier.get());
        }
    }

    @Override
    public List<Fichier> findByContentType(String contentType) {
        return fichierRepository.findByContentType(contentType);
    }

    @Override
    public List<Fichier> findByUserId(String userId) {
        return fichierRepository.findByUploadedByUserId(userId);
    }

    @Override
    public List<Fichier> findByChangementId(Long changementId) {
        return fichierRepository.findByChangementChangementId(changementId);
    }

    @Override
    public List<Fichier> findByEntity(String entityType, Long entityId) {
        return fichierRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    public List<Fichier> findImages() {
        return fichierRepository.findImages();
    }

    @Override
    public List<Fichier> findActiveFichiers() {
        return fichierRepository.findActiveFichiers();
    }
}