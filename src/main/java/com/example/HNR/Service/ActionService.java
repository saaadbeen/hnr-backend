package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.enums.TypeAction;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ActionService {
    Action create(Action action);
    Optional<Action> findById(Long id);
    List<Action> findAll();
    Action update(Action action);
    void delete(Long id);

    // Méthodes métier spécifiques
    List<Action> findByType(TypeAction type);
    List<Action> findByUserId(String userId);
    List<Action> findByDouarId(Long douarId);
    List<Action> findByLocation(String prefecture, String commune);
    List<Action> findByDateRange(Date startDate, Date endDate);
    List<Action> findActionsWithPV();
}