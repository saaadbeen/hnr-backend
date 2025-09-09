package com.example.HNR.Events;

import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Repository.SqlServer.ActionRepository;
import com.example.HNR.Service.PVService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionEventListener {

    private final PVService pvService;
    private final ActionRepository actionRepository;

    @EventListener
    public void handleActionCreated(ActionCreatedEvent event) {
        Action action = actionRepository.findById(event.getActionId()).orElse(null);
        if (action == null) return;

        PV pv = new PV();
        pv.setAction(action);
        pv.setDateRedaction(new Date());
        pv.setValide(false);
        pv.setRedacteurUserId(event.getActionUserId());

        // contenu minimal bilingue (sera remplacé par PDF plus tard)
        String fr = "PV généré automatiquement pour l’action " + event.getActionType() +
                " à " + event.getCommune() + ", " + event.getPrefecture() + ".";
        String ar = "تم إنشاء محضر تلقائيًا للإجراء " + event.getActionType() +
                " في " + event.getCommune() + "، " + event.getPrefecture() + ".";
        pv.setContenu(fr + "\n" + ar);

        PV saved = pvService.create(pv);
        log.info("PV auto créé: {} pour action {}", saved.getPvId(), action.getActionId());
    }
}
