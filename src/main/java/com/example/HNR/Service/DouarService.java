package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import java.util.List;
import java.util.Optional;

public interface DouarService {
    Douar create(Douar douar);
    Optional<Douar> findById(Long id);
    List<Douar> findAll();
    Douar update(Douar douar);
    void delete(Long id);

    // Méthodes métier spécifiques
    Optional<Douar> findByNom(String nom);
    List<Douar> findByStatut(StatutDouar statut);
    List<Douar> findByLocation(String prefecture, String commune);
    List<Douar> findByMissionId(Long missionId);
    List<Douar> findByCreatedByUserId(String userId);
    List<Douar> findActiveDouars();
    void eradiquerDouar(Long id);
}