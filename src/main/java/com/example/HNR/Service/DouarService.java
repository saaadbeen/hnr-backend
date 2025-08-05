package com.example.HNR.Service;

import com.example.HNR.Model.Douar;
import com.example.HNR.Model.StatutDouar;
import com.example.HNR.Repository.DouarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DouarService {

    @Autowired
    private DouarRepository douarRepository;

    // Créer un douar
    public Douar createDouar(Douar douar) {
        if (douarRepository.findByNom(douar.getNom()).isPresent()) {
            throw new RuntimeException("Un douar avec ce nom existe déjà");
        }
        return douarRepository.save(douar);
    }

    // Trouver par ID
    public Optional<Douar> findById(String id) {
        return douarRepository.findById(id);
    }

    // Trouver par nom
    public Optional<Douar> findByNom(String nom) {
        return douarRepository.findByNom(nom);
    }

    // Obtenir tous les douars
    public List<Douar> findAll() {
        return douarRepository.findAll();
    }

    // Trouver par statut
    public List<Douar> findByStatut(StatutDouar statut) {
        return douarRepository.findByStatut(statut);
    }

    // Trouver par préfecture
    public List<Douar> findByPrefectureCommune(String prefectureCommune) {
        return douarRepository.findByPrefectureCommune(prefectureCommune);
    }

    // Trouver par statut et préfecture
    public List<Douar> findByStatutAndPrefectureCommune(StatutDouar statut, String prefectureCommune) {
        return douarRepository.findByStatutAndPrefectureCommune(statut, prefectureCommune);
    }

    // Trouver par mission
    public List<Douar> findByMission(String missionId) {
        return douarRepository.findByIdMission(missionId);
    }

    // Recherche par nom (insensible à la casse)
    public List<Douar> searchByName(String nom) {
        return douarRepository.findByNomContainingIgnoreCase(nom);
    }

    // Douars avec actions
    public List<Douar> findDouarsWithActions() {
        return douarRepository.findDouarsWithActions();
    }

    // Mettre à jour un douar
    public Douar updateDouar(String id, Douar douarDetails) {
        Douar douar = douarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Douar non trouvé avec ID: " + id));

        if (douarDetails.getNom() != null) {
            douar.setNom(douarDetails.getNom());
        }
        if (douarDetails.getStatut() != null) {
            douar.setStatut(douarDetails.getStatut());
        }
        if (douarDetails.getPrefectureCommune() != null) {
            douar.setPrefectureCommune(douarDetails.getPrefectureCommune());
        }

        return douarRepository.save(douar);
    }

    // Supprimer un douar
    public void deleteDouar(String id) {
        Douar douar = douarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Douar non trouvé avec ID: " + id));
        douarRepository.delete(douar);
    }

    // Statistiques
    public long countByStatut(StatutDouar statut) {
        return douarRepository.countByStatut(statut);
    }

    public long countByPrefectureCommune(String prefectureCommune) {
        return douarRepository.countByPrefectureCommune(prefectureCommune);
    }

    // Ajouter une action à un douar
    public void addActionToDouar(String douarId, String actionId) {
        Douar douar = douarRepository.findById(douarId)
                .orElseThrow(() -> new RuntimeException("Douar non trouvé"));

        douar.getActions().add(actionId);
        douarRepository.save(douar);
    }

    // Ajouter un changement à un douar
    public void addChangementToDouar(String douarId, String changementId) {
        Douar douar = douarRepository.findById(douarId)
                .orElseThrow(() -> new RuntimeException("Douar non trouvé"));

        douar.getChangements().add(changementId);
        douarRepository.save(douar);
    }
}