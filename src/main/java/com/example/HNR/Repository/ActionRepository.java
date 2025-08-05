package com.example.HNR.Repository;

import com.example.HNR.Model.Action;
import com.example.HNR.Model.TypeAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActionRepository extends MongoRepository<Action, String> {

    // Recherche par type d'action
    List<Action> findByType(TypeAction type);

    // Recherche par douar
    List<Action> findByDouarId(String douarId);

    // Recherche par mission
    List<Action> findByMissionId(String missionId);

    // Recherche par utilisateur
    List<Action> findByUtilisateurDouarId(String utilisateurId);

    // Recherche par PV
    List<Action> findByPvId(String pvId);

    // Actions entre deux dates
    List<Action> findByDateActionBetween(Date startDate, Date endDate);


    // Actions avec avis préfecture
    @Query("{'avisPrefecture': {$exists: true, $ne: null}}")
    List<Action> findActionsWithAvisPrefecture();

    // Actions par préfecture/commune
    List<Action> findByPrefectureCommune(String prefectureCommune);


    // Actions récentes par douar
    @Query("{'douarId': ?0, 'dateAction': {$gte: ?1}}")
    List<Action> findRecentActionsByDouar(String douarId, Date sinceDate);
}