package com.example.HNR.Service;

import com.example.HNR.Model.Changement;
import com.example.HNR.Repository.ChangementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class ChangementService {

    private final ChangementRepository changementRepository;

    // 🔍 Récupérer tous les changements
    public List<Changement> getAllChangements() {
        return changementRepository.findAll();
    }

    // 🔍 Récupérer un changement par son ID
    public Optional<Changement> getChangementById(String id) {
        return changementRepository.findById(id);
    }

    // ➕ Créer un changement
    public Changement createChangement(Changement changement) {
        return changementRepository.save(changement);
    }

    // 🗑 Supprimer un changement
    public void deleteChangement(String id) {
        changementRepository.deleteById(id);
    }

    // 🧩 Mettre à jour un changement (version simple)
    public Changement updateChangement(String id, Changement updated) {
        return changementRepository.findById(id).map(changement -> {
            changement.setType(updated.getType());
            changement.setDate(updated.getDate());
            changement.setIdphotoAvant(updated.getIdphotoAvant());
            changement.setIdphotoApres(updated.getIdphotoApres());
            changement.setSurface(updated.getSurface());
            changement.setIdDouar(updated.getIdDouar());
            return changementRepository.save(changement);
        }).orElse(null);
    }

    // 🔍 Récupérer tous les changements d’un Douar
    public List<Changement> getChangementsByDouarId(String douarId) {
        return changementRepository.findByIdDouar(douarId);
    }

    // 📆 Récupérer les changements après une certaine date
    public List<Changement> getChangementsAfter(Date date) {
        return changementRepository.findAll().stream()
                .filter(c -> c.getDate() != null && c.getDate().after(date))
                .collect(Collectors.toList());
    }

    // 📏 Filtrer les changements par surface minimale
    public List<Changement> getChangementsBySurfaceGreaterThan(double minSurface) {
        return changementRepository.findAll().stream()
                .filter(c -> c.getSurface() >= minSurface)
                .collect(Collectors.toList());
    }

    // ✅ Vérifie si un changement avec les deux photos existe déjà
    public boolean existsByPhotos(String idPhotoAvant, String idPhotoApres) {
        return changementRepository.findAll().stream()
                .anyMatch(c -> c.getIdphotoAvant().equals(idPhotoAvant)
                        && c.getIdphotoApres().equals(idPhotoApres));
    }
}