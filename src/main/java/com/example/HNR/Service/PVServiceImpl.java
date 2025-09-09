package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Repository.SqlServer.PVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PVServiceImpl implements PVService {

    @Autowired private PVRepository pvRepository;
    @Autowired private ActionService actionService;

    @Override
    public Optional<PV> findById(Long id) {
        return pvRepository.findById(id);
    }

    @Override
    public Optional<PV> findByActionId(Long actionId) {
        return pvRepository.findByAction_ActionId(actionId);
    }

    @Override
    public PV create(PV pv) {
        if (pv.getDateRedaction() == null) pv.setDateRedaction(new Date());
        if (pv.getContenu() == null) pv.setContenu("{}");
        pv.setCreatedAt(new Date());
        pv.setUpdatedAt(new Date());
        return pvRepository.save(pv);
    }

    @Override
    public PV update(PV pv) {
        pv.setUpdatedAt(new Date());
        return pvRepository.save(pv);
    }

    @Override
    public void delete(Long id) {
        pvRepository.deleteById(id);
    }

    @Override
    public PV createForAction(Long actionId, String contenuJSON, String redacteurUserId) {
        // 1 PV par action : si déjà existant, on le renvoie
        Optional<PV> existing = pvRepository.findByAction_ActionId(actionId);
        if (existing.isPresent()) return existing.get();

        Action action = actionService.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable: " + actionId));

        PV pv = new PV();
        pv.setAction(action);
        pv.setContenu(contenuJSON != null ? contenuJSON : "{}");
        pv.setDateRedaction(new Date());
        pv.setRedacteurUserId(redacteurUserId);
        pv.setCreatedAt(new Date());
        pv.setUpdatedAt(new Date());

        PV saved = pvRepository.save(pv);

        // Si ton entité Action a un champ pv, on l’actualise
        try {
            action.setPv(saved);
            actionService.update(action);
        } catch (Exception ignore) {}

        return saved;
    }
}
