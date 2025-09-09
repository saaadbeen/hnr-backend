package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.enums.TypeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    // Recherche par type d'action
    List<Action> findByType(TypeAction type);

    // Recherche par utilisateur
    List<Action> findByUserId(String userId);

    List<Action> findByMissionMissionId(Long missionId);

    // Recherche par douar
    List<Action> findByDouarDouarId(Long douarId);

    // Recherche par prefecture
    List<Action> findByPrefecture(String prefecture);

    // Recherche par commune
    List<Action> findByCommune(String commune);

    // Recherche par prefecture et commune
    List<Action> findByPrefectureAndCommune(String prefecture, String commune);

    // Recherche par plage de dates
    List<Action> findByDateActionBetween(Date startDate, Date endDate);

    // Actions avec PV
    @Query("SELECT a FROM Action a WHERE a.pv IS NOT NULL")
    List<Action> findActionsWithPV();

    // Actions récentes
    @Query("SELECT a FROM Action a ORDER BY a.dateAction DESC")
    List<Action> findRecentActions();

    // MÉTHODES DASHBOARD
    // Compter par type
    long countByType(TypeAction type);

    // Compter par préfecture
    long countByPrefecture(String prefecture);

    // Compter par utilisateur
    long countByUserId(String userId);

    // Grouper par préfecture
    @Query("SELECT a.prefecture, COUNT(a) FROM Action a GROUP BY a.prefecture")
    List<Object[]> countByPrefectureGrouped();

    // Statistiques par type
    @Query("SELECT a.type, COUNT(a) FROM Action a GROUP BY a.type")
    List<Object[]> countByTypeGrouped();
}
