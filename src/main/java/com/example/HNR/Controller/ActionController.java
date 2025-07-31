package com.example.HNR.Controller;

import com.example.HNR.Model.Action;
import com.example.HNR.Service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @GetMapping
    public List<Action> getAllActions() {
        return actionService.getAllActions();
    }

    @GetMapping("/{id}")
    public Optional<Action> getActionById(@PathVariable String id) {
        return actionService.getActionById(id);
    }

    @PostMapping
    public Action createAction(@RequestBody Action action) {
        return actionService.createAction(action);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable String id) {
        actionService.deleteAction(id);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Action> getByUtilisateur(@PathVariable String utilisateurId) {
        return actionService.getActionsByUtilisateurId(utilisateurId);
    }

    @GetMapping("/mission/{missionId}")
    public List<Action> getByMission(@PathVariable String missionId) {
        return actionService.getActionsByMissionId(missionId);
    }

    @GetMapping("/douar/{douarId}")
    public List<Action> getByDouar(@PathVariable String douarId) {
        return actionService.getActionsByDouarId(douarId);
    }

    @GetMapping("/pv/{pvId}")
    public List<Action> getByPv(@PathVariable String pvId) {
        return actionService.getActionsByPvId(pvId);
    }
}
