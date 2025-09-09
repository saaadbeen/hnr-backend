package com.example.HNR.Service;

import com.example.HNR.DTO.Request.DouarCreateRequest;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.StatutDouar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DouarService {
    Douar create(Douar douar); // EXISTANT — on garde

    // 👇 NOUVELLE MÉTHODE (pour la création depuis GeoJSON)
    Douar createFromGeoJson(DouarCreateRequest req);

    Optional<Douar> findById(Long id);
    List<Douar> findAll();
    Page<Douar> findAll(Pageable pageable);
    Douar update(Douar douar);
    // ...
    Optional<Douar> findByNom(String nom);
    List<Douar> findByStatut(StatutDouar statut);
    List<Douar> findByLocation(String prefecture, String commune);
    List<Douar> findByMissionId(Long missionId);
    List<Douar> findByCreatedByUserId(String userId);
    List<Douar> findActiveDouars();
    void eradiquerDouar(Long id);
}
