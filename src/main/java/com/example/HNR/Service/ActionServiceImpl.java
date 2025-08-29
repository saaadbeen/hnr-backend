package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Repository.SqlServer.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public Action create(Action action) {
        return actionRepository.save(action);
    }

    @Override
    public Optional<Action> findById(Long id) {
        return actionRepository.findById(id);
    }

    @Override
    public List<Action> findAll() {
        return actionRepository.findAll();
    }

    @Override
    public Action update(Action action) {
        return actionRepository.save(action);
    }

    @Override
    public void delete(Long id) {
        actionRepository.deleteById(id);
    }

    @Override
    public List<Action> findByType(TypeAction type) {
        return actionRepository.findByType(type);
    }

    @Override
    public List<Action> findByUserId(String userId) {
        return actionRepository.findByUserId(userId);
    }

    @Override
    public List<Action> findByDouarId(Long douarId) {
        return actionRepository.findByDouarDouarId(douarId);
    }

    @Override
    public List<Action> findByLocation(String prefecture, String commune) {
        return actionRepository.findByCommune(commune);
    }

    @Override
    public List<Action> findByDateRange(Date startDate, Date endDate) {
        return actionRepository.findByDateActionBetween(startDate, endDate);
    }

    @Override
    public List<Action> findActionsWithPV() {
        return actionRepository.findActionsWithPV();
    }
}