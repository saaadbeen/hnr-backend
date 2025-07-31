package com.example.HNR.Service;

import com.example.HNR.Model.Action;
import com.example.HNR.Repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    public Optional<Action> getActionById(String id) {
        return actionRepository.findById(id);
    }

    public Action createAction(Action action) {
        return actionRepository.save(action);
    }

    public void deleteAction(String id) {
        actionRepository.deleteById(id);
    }

    public List<Action> getActionsByUtilisateurId(String utilisateurId) {
        return actionRepository.findByUtilisateur_Id(utilisateurId);
    }

    public List<Action> getActionsByMissionId(String missionId) {
        return actionRepository.findByIdMission(missionId);
    }

    public List<Action> getActionsByDouarId(String douarId) {
        return actionRepository.findByIdDouar(douarId);
    }

    public List<Action> getActionsByPvId(String pvId) {
        return actionRepository.findByIdPv(pvId);
    }
}