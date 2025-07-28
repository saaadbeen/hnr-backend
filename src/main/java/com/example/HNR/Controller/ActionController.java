package com.example.HNR.Controller;

import com.example.HNR.Model.Action;
import com.example.HNR.Service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@CrossOrigin(origins = "*")
public class ActionController {

    @Autowired
    private ActionService actionService;

    // --- CRUD de base ---

    @GetMapping
    public List<Action> getAllActions() {
        return actionService.getAllActions();
    }

    @GetMapping("/{id}")
    public Action getActionById(@PathVariable String id) {
        return actionService.getActionById(id).orElse(null);
    }

    @PostMapping
    public Action createAction(@RequestBody Action action) {
        return actionService.saveAction(action);
    }

    @PutMapping("/{id}")
    public Action updateAction(@PathVariable String id, @RequestBody Action action) {
        action.setId(id);
        return actionService.saveAction(action);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable String id) {
        actionService.deleteAction(id);
    }

    // --- Requête personnalisée ---

    @GetMapping("/douar/{douarId}")
    public List<Action> getActionsByDouar(@PathVariable String douarId) {
        return actionService.getActionsByDouarId(douarId);
    }
}
