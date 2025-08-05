package com.example.HNR.Service;

import com.example.HNR.Model.Changement;
import com.example.HNR.Model.TypeExtension;
import com.example.HNR.Repository.ChangementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChangementService {

    @Autowired
    private ChangementRepository changementRepository;

    @Autowired
    private DouarService douarService;

    // Créer un changement
    public Changement createChangement(Changement changement) {
        Changement savedChangement = changementRepository.save(changement);

        // Ajouter le changement au douar
        douarService.addChangementToDouar(changement.getDouarId(), savedChangement.getCodeChangement());

        return savedChangement;
    }

    // Trouver par ID
    public Optional<Changement> findById(String id) {
        return changementRepository.findById(id);
    }

    // Obtenir tous les changements
    public List<Changement> findAll() {
        return changementRepository.findAll();
    }

    // Trouver par type
    public List<Changement> findByType(TypeExtension type) {
        return changementRepository.findByType(type);
    }

    // Trouver par douar
    public List<Changement> findByDouar(String douarId) {
        return changementRepository.findByDouarId(douarId);
    }

    // Changements par surface minimale
    public List<Changement> findBySurfaceGreaterThanEqual(double minSurface) {
        return changementRepository.findBySurfaceGreaterThanEqual(minSurface);
    }

    // Changements avec photos
    public List<Changement> findChangementsWithBothPhotos() {
        return changementRepository.findChangementsWithBothPhotos();
    }

    // Compter par type
    public long countByType(TypeExtension type) {
        return changementRepository.countByType(type);
    }

    // Surface totale par douar (maintenant avec @Data)
    public double getTotalSurfaceByDouar(String douarId) {
        List<Changement> changements = changementRepository.findSurfacesByDouarId(douarId);
        return changements.stream().mapToDouble(Changement::getSurface).sum();
    }

    // Mettre à jour un changement (maintenant avec @Data)
    public Changement updateChangement(String id, Changement changementDetails) {
        Changement changement = changementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Changement non trouvé avec ID: " + id));

        if (changementDetails.getType() != null) {
            changement.setType(changementDetails.getType());
        }
        if (changementDetails.getSurface() > 0) {
            changement.setSurface(changementDetails.getSurface());
        }
        if (changementDetails.getPhotoAvant() != null) {
            changement.setPhotoAvant(changementDetails.getPhotoAvant());
        }
        if (changementDetails.getPhotoApres() != null) {
            changement.setPhotoApres(changementDetails.getPhotoApres());
        }
        if (changementDetails.getDateAvant() != null) {
            changement.setDateAvant(changementDetails.getDateAvant());
        }
        if (changementDetails.getDateApres() != null) {
            changement.setDateApres(changementDetails.getDateApres());
        }

        return changementRepository.save(changement);
    }

    // Supprimer un changement
    public void deleteChangement(String id) {
        Changement changement = changementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Changement non trouvé avec ID: " + id));
        changementRepository.delete(changement);
    }
}