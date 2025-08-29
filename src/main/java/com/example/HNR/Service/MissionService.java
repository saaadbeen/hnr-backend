package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Mission;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MissionService {
    Mission create(Mission mission);
    Optional<Mission> findById(Long id);
    List<Mission> findAll();
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