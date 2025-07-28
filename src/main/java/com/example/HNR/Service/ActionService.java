package com.example.HNR.Service;

import com.example.HNR.Model.Action;
import com.example.HNR.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionService {

    @Autowired
    private ActionRepository actionRepository;

    public List<Action> findByIdDouar(String idDouar) {
        return actionRepository.findByIdDouar(idDouar);
    }
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    public Optional<Action> getActionById(String id) {
        return actionRepository.findById(id);
    }

    public Action saveAction(Action action) {
        return actionRepository.save(action);
    }

    public void deleteAction(String id) {
        actionRepository.deleteById(id);
    }

    // Méthode personnalisée : lister les actions pour un douar
    public List<Action> getActionsByDouarId(String douarId) {
        return actionRepository.findByIdDouar(douarId);
    }
}
