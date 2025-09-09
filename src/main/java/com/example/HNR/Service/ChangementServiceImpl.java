package com.example.HNR.Service;

import com.example.HNR.Events.ChangementDeclaredEvent;
import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.enums.TypeExtension;
import com.example.HNR.Repository.SqlServer.ChangementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangementServiceImpl implements ChangementService {

    private final ChangementRepository changementRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Changement create(Changement changement) {
        try {
            // Valeurs par défaut / normalisation
            if (changement.getDate() == null) {
                changement.setDate(new Date());
            }

            // Déduire prefecture/commune depuis le Douar si non fournis
            Douar d = changement.getDouar();
            if (d != null) {
                if (changement.getPrefecture() == null) changement.setPrefecture(d.getPrefecture());
                if (changement.getCommune() == null)    changement.setCommune(d.getCommune());
            }

            Changement saved = changementRepository.save(changement);

            // Événement (non bloquant)
            try {
                String pref = saved.getPrefecture();
                String com  = saved.getCommune();
                if (saved.getDouar() != null) {
                    if (pref == null) pref = saved.getDouar().getPrefecture();
                    if (com  == null) com  = saved.getDouar().getCommune();
                }
                eventPublisher.publishEvent(new ChangementDeclaredEvent(
                        saved.getChangementId(),
                        saved.getType(),
                        saved.getDetectedByUserId(),
                        pref,
                        com,
                        saved.getDouar() != null ? saved.getDouar().getDouarId() : null
                ));
            } catch (Exception ev) {
                log.error("Failed to publish ChangementDeclaredEvent for {}: {}", saved.getChangementId(), ev.getMessage());
            }

            return saved;
        } catch (Exception e) {
            log.error("Error creating changement: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override public Optional<Changement> findById(Long id) { return changementRepository.findById(id); }

    @Override public List<Changement> findAll() { return changementRepository.findAll(); }

    @Override
    @Transactional
    public Changement update(Changement changement) {
        try {
            return changementRepository.save(changement);
        } catch (Exception e) {
            log.error("Error updating changement {}: {}", changement.getChangementId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            changementRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting changement {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override public List<Changement> findByType(TypeExtension type) { return changementRepository.findByType(type); }

    @Override public List<Changement> findByDouarId(Long douarId) { return changementRepository.findByDouar_DouarId(douarId); }

    @Override public List<Changement> findByDetectedByUserId(String userId) { return changementRepository.findByDetectedByUserId(userId); }

    @Override public List<Changement> findByDateRange(Date startDate, Date endDate) { return changementRepository.findByDateBetween(startDate, endDate); }
}
