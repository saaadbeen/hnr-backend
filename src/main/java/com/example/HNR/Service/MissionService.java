package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Mission;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MissionService {
    Mission create(Mission mission);
    Optional<Mission> findById(Long id);
    Page<Mission> findAll(Pageable pageable);
    Mission update(Mission mission);
    void delete(Long id);

    // Méthodes métier spécifiques
    List<Mission> findByStatut(String statut);
    List<Mission> findByLocation(String prefecture, String commune);
    List<Mission> findByCreatedByUserId(String userId);
    List<Mission> findByDateRange(Date startDate, Date endDate);
    List<Mission> findCompletedMissions();
    List<Mission> findActiveMissions();
    void completeMission(Long id);
}