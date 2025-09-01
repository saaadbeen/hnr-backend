package com.example.HNR.Controller;

import com.example.HNR.DTO.ActionDTO;
import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import com.example.HNR.Repository.SqlServer.PVRepository;
import com.example.HNR.Service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
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
        d.setActionId(e.getActionId());
        d.setType(e.getType());
        d.setPrefecture(e.getPrefecture());
        d.setCommune(e.getCommune());
        d.setDouarId(e.getDouar() != null ? e.getDouar().getDouarId() : null);
        d.setMissionId(e.getMission() != null ? e.getMission().getMissionId() : null);
        d.setPvId(e.getPv() != null ? e.getPv().getPvId() : null);
        d.setUserId(e.getUserId());
        d.setDateAction(e.getDateAction());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        // nouveaux champs photos
        d.setPhotoAvantUrl(e.getPhotoAvantUrl());
        d.setPhotoApresUrl(e.getPhotoApresUrl());
        return d;
    }

    private Action fromDto(ActionDTO d) {
        Action e = new Action();
        e.setActionId(d.getActionId());
        e.setType(d.getType());
        e.setPrefecture(d.getPrefecture());
        e.setCommune(d.getCommune());
        e.setUserId(d.getUserId());
        e.setDateAction(d.getDateAction());
        // nouveaux champs photos
        e.setPhotoAvantUrl(d.getPhotoAvantUrl());
        e.setPhotoApresUrl(d.getPhotoApresUrl());

        if (d.getDouarId() != null) {
            Douar douar = douarRepository.findById(d.getDouarId())
                    .orElseThrow(() -> new IllegalArgumentException("Douar introuvable: " + d.getDouarId()));
            e.setDouar(douar);
        } else e.setDouar(null);

        if (d.getMissionId() != null) {
            Mission mission = missionRepository.findById(d.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + d.getMissionId()));
            e.setMission(mission);
        } else e.setMission(null);

        if (d.getPvId() != null) {
            PV pv = pvRepository.findById(d.getPvId())
                    .orElseThrow(() -> new IllegalArgumentException("PV introuvable: " + d.getPvId()));
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
            // Validation minimale des champs obligatoires
            if (dto == null || dto.getDouarId() == null || dto.getUserId() == null || dto.getType() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (dto.getDateAction() == null) dto.setDateAction(new Date());
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

            dto.setActionId(id);
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

    // ========= Filtres =========
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

    // ========= Upload photos =========

    @PostMapping("/{id}/photo-avant")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> uploadPhotoAvant(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
        try {
            var opt = actionService.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            String url = saveActionFile(id, file, "avant");
            var action = opt.get();
            action.setPhotoAvantUrl(url);
            var saved = actionService.update(action);

            return ResponseEntity.ok(toDto(saved));
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/photo-apres")
    @PreAuthorize("hasRole('GOUVERNEUR') or hasRole('MEMBRE_DSI') or hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> uploadPhotoApres(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
        try {
            var opt = actionService.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            String url = saveActionFile(id, file, "apres");
            var action = opt.get();
            action.setPhotoApresUrl(url);
            var saved = actionService.update(action);

            return ResponseEntity.ok(toDto(saved));
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- utilitaire local pour sauvegarder un fichier uploadé ---
    private String saveActionFile(Long id, MultipartFile file, String slot) throws Exception {
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
        String filename = slot + "-" + System.currentTimeMillis() + ext;

        Path uploadRoot = Paths.get("uploads", "actions", id.toString());
        Files.createDirectories(uploadRoot);

        Path target = uploadRoot.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Retourne l'URL publique (assure-toi d'exposer /files/** -> dossier "uploads/")
        return "/files/actions/" + id + "/" + filename;
    }
}
