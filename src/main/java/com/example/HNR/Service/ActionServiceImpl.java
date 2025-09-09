package com.example.HNR.Service;

import com.example.HNR.Events.ActionCreatedEvent;
import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Repository.SqlServer.ActionRepository;
import com.example.HNR.Service.ActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionServiceImpl implements ActionService {

    private final ActionRepository actionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Action create(Action action) {
        try {
            // Sauvegarder l'action
            Action savedAction = actionRepository.save(action);

            // Publier l'événement pour les notifications
            try {
                ActionCreatedEvent event = new ActionCreatedEvent(
                        savedAction.getActionId(),
                        savedAction.getType(),
                        savedAction.getUserId(),
                        savedAction.getPrefecture(),
                        savedAction.getCommune(),
                        savedAction.getDouar() != null ? savedAction.getDouar().getDouarId() : null,
                        savedAction.getMission() != null ? savedAction.getMission().getMissionId() : null
                );

                eventPublisher.publishEvent(event);
                log.info("ActionCreatedEvent published for action ID: {}", savedAction.getActionId());

            } catch (Exception e) {
                log.error("Failed to publish ActionCreatedEvent for action {}: {}",
                        savedAction.getActionId(), e.getMessage());
            }

            return savedAction;

        } catch (Exception e) {
            log.error("Error creating action: {}", e.getMessage(), e);
            throw e;
        }
    }
    @Override
    public List<Action> findByMissionId(Long missionId) {
        return actionRepository.findByMissionMissionId(missionId);
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
    public Page<Action> findAll(Pageable pageable) {
        return actionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Action update(Action action) {
        try {
            Action updatedAction = actionRepository.save(action);

            // Optionnel : publier un événement de mise à jour
            // Pour l'instant on ne le fait que pour la création, mais on pourrait ajouter
            // un ActionUpdatedEvent si nécessaire

            return updatedAction;

        } catch (Exception e) {
            log.error("Error updating action {}: {}", action.getActionId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            actionRepository.deleteById(id);
            log.info("Action deleted with ID: {}", id);

        } catch (Exception e) {
            log.error("Error deleting action {}: {}", id, e.getMessage(), e);
            throw e;
        }
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
        if (prefecture != null && commune != null) {
            return actionRepository.findByPrefectureAndCommune(prefecture, commune);
        } else if (prefecture != null) {
            return actionRepository.findByPrefecture(prefecture);
        } else if (commune != null) {
            return actionRepository.findByCommune(commune);
        } else {
            return actionRepository.findAll();
        }
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