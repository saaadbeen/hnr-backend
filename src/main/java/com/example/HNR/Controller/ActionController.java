package com.example.HNR.Controller;

import com.example.HNR.Model.Action;
import com.example.HNR.Model.TypeAction;
import com.example.HNR.Service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/actions")
@CrossOrigin(origins = "*")
public class ActionController {

    @Autowired
    private ActionService actionService;

    // GET /api/actions - Obtenir toutes les actions
    @GetMapping
    public ResponseEntity<List<Action>> getAllActions() {
        List<Action> actions = actionService.findAll();
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/{id} - Obtenir action par ID
    @GetMapping("/{id}")
    public ResponseEntity<Action> getActionById(@PathVariable String id) {
        Optional<Action> action = actionService.findById(id);
        if (action.isPresent()) {
            return ResponseEntity.ok(action.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/actions/type/{type} - Obtenir actions par type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Action>> getActionsByType(@PathVariable TypeAction type) {
        List<Action> actions = actionService.findByType(type);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/douar/{douarId} - Obtenir actions par douar
    @GetMapping("/douar/{douarId}")
    public ResponseEntity<List<Action>> getActionsByDouar(@PathVariable String douarId) {
        List<Action> actions = actionService.findByDouar(douarId);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/mission/{missionId} - Obtenir actions par mission
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Action>> getActionsByMission(@PathVariable String missionId) {
        List<Action> actions = actionService.findByMission(missionId);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/utilisateur/{utilisateurId} - Obtenir actions par utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Action>> getActionsByUtilisateur(@PathVariable String utilisateurId) {
        List<Action> actions = actionService.findByUtilisateur(utilisateurId);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/pv/{pvId} - Obtenir actions par PV
    @GetMapping("/pv/{pvId}")
    public ResponseEntity<List<Action>> getActionsByPV(@PathVariable String pvId) {
        List<Action> actions = actionService.findByPV(pvId);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/periode - Actions entre deux dates
    @GetMapping("/periode")
    public ResponseEntity<List<Action>> getActionsByPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Action> actions = actionService.findByDateActionBetween(startDate, endDate);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/with-avis - Actions avec avis préfecture
    @GetMapping("/with-avis")
    public ResponseEntity<List<Action>> getActionsWithAvis() {
        List<Action> actions = actionService.findActionsWithAvisPrefecture();
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/prefecture/{prefecture} - Actions par préfecture
    @GetMapping("/prefecture/{prefecture}")
    public ResponseEntity<List<Action>> getActionsByPrefecture(@PathVariable String prefecture) {
        List<Action> actions = actionService.findByPrefectureCommune(prefecture);
        return ResponseEntity.ok(actions);
    }

    // GET /api/actions/douar/{douarId}/recentes - Actions récentes par douar
    @GetMapping("/douar/{douarId}/recentes")
    public ResponseEntity<List<Action>> getRecentActionsByDouar(@PathVariable String douarId) {
        List<Action> actions = actionService.findRecentActionsByDouar(douarId);
        return ResponseEntity.ok(actions);
    }

    // POST /api/actions - Créer nouvelle action
    @PostMapping
    public ResponseEntity<Action> createAction(@RequestBody Action action) {
        Action createdAction = actionService.createAction(action);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAction);
    }

    // PUT /api/actions/{id} - Mettre à jour action
    @PutMapping("/{id}")
    public ResponseEntity<Action> updateAction(@PathVariable String id, @RequestBody Action actionDetails) {
        try {
            Action updatedAction = actionService.updateAction(id, actionDetails);
            return ResponseEntity.ok(updatedAction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/actions/{id} - Supprimer action
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable String id) {
        try {
            actionService.deleteAction(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}