package com.example.HNR.Service;

import com.example.HNR.Model.Action;
import com.example.HNR.Model.TypeAction;
import com.example.HNR.Repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;

@Service
public class ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private DouarService douarService;

    // Créer une action
    public Action createAction(Action action) {
        action.setDateAction(new Date());
        Action savedAction = actionRepository.save(action);

        // Ajouter l'action au douar
        douarService.addActionToDouar(action.getDouarId(), savedAction.getIdAction());

        return savedAction;
    }

    // Trouver par ID
    public Optional<Action> findById(String id) {
        return actionRepository.findById(id);
    }

    // Obtenir toutes les actions
    public List<Action> findAll() {
        return actionRepository.findAll();
    }

    // Trouver par type
    public List<Action> findByType(TypeAction type) {
        return actionRepository.findByType(type);
    }

    // Trouver par douar
    public List<Action> findByDouar(String douarId) {
        return actionRepository.findByDouarId(douarId);
    }

    // Trouver par mission
    public List<Action> findByMission(String missionId) {
        return actionRepository.findByMissionId(missionId);
    }

    // Trouver par utilisateur
    public List<Action> findByUtilisateur(String utilisateurId) {
        return actionRepository.findByUtilisateurDouarId(utilisateurId);
    }

    // Trouver par PV
    public List<Action> findByPV(String pvId) {
        return actionRepository.findByPvId(pvId);
    }

    // Actions entre deux dates
    public List<Action> findByDateActionBetween(Date startDate, Date endDate) {
        return actionRepository.findByDateActionBetween(startDate, endDate);
    }

    // Actions avec avis préfecture
    public List<Action> findActionsWithAvisPrefecture() {
        return actionRepository.findActionsWithAvisPrefecture();
    }

    // Actions par préfecture
    public List<Action> findByPrefectureCommune(String prefectureCommune) {
        return actionRepository.findByPrefectureCommune(prefectureCommune);
    }

    // Actions récentes par douar
    public List<Action> findRecentActionsByDouar(String douarId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return actionRepository.findRecentActionsByDouar(douarId, cal.getTime());
    }

    // Mettre à jour une action
    public Action updateAction(String id, Action actionDetails) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée avec ID: " + id));

        if (actionDetails.getType() != null) {
            action.setType(actionDetails.getType());
        }
        if (actionDetails.getDateDebut() != null) {
            action.setDateDebut(actionDetails.getDateDebut());
        }
        if (actionDetails.getAvisPrefecture() != null) {
            action.setAvisPrefecture(actionDetails.getAvisPrefecture());
        }
        if (actionDetails.getPrefectureCommune() != null) {
            action.setPrefectureCommune(actionDetails.getPrefectureCommune());
        }

        return actionRepository.save(action);
    }

    // Supprimer une action
    public void deleteAction(String id) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée avec ID: " + id));
        actionRepository.delete(action);
    }
}