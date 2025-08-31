package com.example.HNR.Controller;

import com.example.HNR.DTO.ActionDTO;
import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Service.ActionService;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import com.example.HNR.Repository.SqlServer.PVRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actions")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001"})
public class ActionController {

    @Autowired private ActionService actionService;
    @Autowired private DouarRepository douarRepository;
    @Autowired private MissionRepository missionRepository;
    @Autowired private PVRepository pvRepository;

    // ---------- mapping manuel ----------
    private ActionDTO toDto(Action e) {
        ActionDTO d = new ActionDTO();
        d.actionId   = e.getActionId();
        d.type       = e.getType();
        d.prefecture = e.getPrefecture();
        d.commune    = e.getCommune();
        d.douarId    = (e.getDouar()   != null ? e.getDouar().getDouarId()       : null);
        d.missionId  = (e.getMission() != null ? e.getMission().getMissionId()   : null);
        d.pvId       = (e.getPv()      != null ? e.getPv().getPvId()             : null);
        d.userId     = e.getUserId();
        d.dateAction = e.getDateAction();
        d.createdAt  = e.getCreatedAt();
        d.updatedAt  = e.getUpdatedAt();
        return d;
    }

    private Action fromDto(ActionDTO d) {
        Action e = new Action();
        e.setActionId(d.actionId);
        e.setType(d.type);
        e.setPrefecture(d.prefecture);
        e.setCommune(d.commune);
        e.setUserId(d.userId);
        e.setDateAction(d.dateAction);

        if (d.douarId != null) {
            Douar douar = douarRepository.findById(d.douarId)
                    .orElseThrow(() -> new IllegalArgumentException("Douar introuvable: " + d.douarId));
            e.setDouar(douar);
        } else e.setDouar(null);

        if (d.missionId != null) {
            Mission mission = missionRepository.findById(d.missionId)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + d.missionId));
            e.setMission(mission);
        } else e.setMission(null);

        if (d.pvId != null) {
            PV pv = pvRepository.findById(d.pvId)
                    .orElseThrow(() -> new IllegalArgumentException("PV introuvable: " + d.pvId));
            e.setPv(pv);
        } else e.setPv(null);

        return e;
    }
    // -----------------------------------

    @GetMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getAllActions() {
        try {
            var dtos = actionService.findAll().stream().map(this::toDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> getActionById(@PathVariable Long id) {
        try {
            return actionService.findById(id)
                    .map(this::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @PostMapping
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> createAction(@RequestBody ActionDTO dto) {
        try {
            if (dto.dateAction == null) dto.dateAction = new Date();
            var saved = actionService.create(fromDto(dto));
            return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
        } catch (IllegalArgumentException notFound) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or (@actionServiceImpl.findById(#id).isPresent() and @actionServiceImpl.findById(#id).get().getUserId() == authentication.name)")
    public ResponseEntity<ActionDTO> updateAction(@PathVariable Long id, @RequestBody ActionDTO dto) {
        try {
            var existing = actionService.findById(id);
            if (existing.isEmpty()) return ResponseEntity.notFound().build();

            dto.actionId = id;
            var entity = fromDto(dto);
            entity.setCreatedAt(existing.get().getCreatedAt());

            var saved = actionService.update(entity);
            return ResponseEntity.ok(toDto(saved));
        } catch (IllegalArgumentException notFound) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI')")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        try {
            if (actionService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
            actionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    // Filtres — maintenant en DTO
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getActionsByType(@PathVariable TypeAction type) {
        try {
            var dtos = actionService.findByType(type).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or #userId == authentication.name")
    public ResponseEntity<List<ActionDTO>> getActionsByUserId(@PathVariable String userId) {
        try {
            var dtos = actionService.findByUserId(userId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/douar/{douarId}")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getActionsByDouarId(@PathVariable Long douarId) {
        try {
            var dtos = actionService.findByDouarId(douarId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/location")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getActionsByLocation(@RequestParam String prefecture, @RequestParam String commune) {
        try {
            var dtos = actionService.findByLocation(prefecture, commune).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getActionsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            var dtos = actionService.findByDateRange(startDate, endDate).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/with-pv")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getActionsWithPV() {
        try {
            var dtos = actionService.findActionsWithPV().stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/my-actions")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getMyActions() {
        try {
            String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            var dtos = actionService.findByUserId(currentUserId).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/demolitions")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getDemolitions() {
        try {
            var dtos = actionService.findByType(TypeAction.DEMOLITION).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/signalements")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<List<ActionDTO>> getSignalements() {
        try {
            var dtos = actionService.findByType(TypeAction.SIGNALEMENT).stream().map(this::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) { return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
}
