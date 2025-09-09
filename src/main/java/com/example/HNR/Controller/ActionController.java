package com.example.HNR.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.HNR.DTO.ActionDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.HNR.Model.SqlServer.Action;
import com.example.HNR.Model.SqlServer.Douar;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Model.enums.TypeAction;
import com.example.HNR.Repository.SqlServer.DouarRepository;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import com.example.HNR.Service.FichierService;

import com.example.HNR.Repository.SqlServer.PVRepository;
import com.example.HNR.Service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.HNR.Util.FileUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.example.HNR.Model.SqlServer.PV;
import com.example.HNR.Service.PVService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
@RestController
@RequestMapping("/api/actions")
public class ActionController {

    @Autowired private ActionService actionService;
    @Autowired private DouarRepository douarRepository;
    @Autowired private MissionRepository missionRepository;
    @Autowired private PVRepository pvRepository;
    @Autowired private final FichierService fichierService;

    public ActionController(FichierService fichierService) {
        this.fichierService = fichierService;
    }

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
    public ResponseEntity<Page<ActionDTO>> getAllActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var dtos = actionService.findAll(PageRequest.of(page, size)).map(this::toDto);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionDTO> getActionById(@PathVariable Long id) {
        return actionService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAction(@RequestBody ActionDTO dto) {
        if (dto == null || dto.getMissionId() == null || dto.getUserId() == null || dto.getType() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Champs obligatoires: type, missionId, userId"));
        }
        if (dto.getDouarId() == null) {
            return ResponseEntity.badRequest().body(Map.of("douarId", "Le douar est obligatoire"));
        }
        try {
            Action saved = actionService.create(fromDto(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Violation d'intégrité", "detail", ex.getMostSpecificCause().getMessage()));
        }
    }
    @Autowired private PVService pvService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/{id}/pv")
    @PreAuthorize("hasRole('AGENT_AUTORITE')")
    public ResponseEntity<PV> createPVForAction(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {

        var actionOpt = actionService.findById(id);
        if (actionOpt.isEmpty()) return ResponseEntity.notFound().build();

        String contenuJson = "{}";
        String redacteurUserId = actionOpt.get().getUserId(); // défaut = créateur de l’action

        try {
            if (body != null) {
                Object contenu = body.get("contenu");
                if (contenu != null) {
                    contenuJson = (contenu instanceof String)
                            ? (String) contenu
                            : objectMapper.writeValueAsString(contenu);
                }
                Object who = body.get("redacteurUserId");
                if (who == null) who = body.get("createdBy");
                if (who == null) who = body.get("userId");
                if (who != null) redacteurUserId = String.valueOf(who);
            }
        } catch (Exception ignore) {}

        PV saved = pvService.createForAction(id, contenuJson, redacteurUserId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }


    private String generateNumeroPv(Long actionId) {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "PV-" + (actionId != null ? actionId : "NA") + "-" + ts;
    }
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<ActionDTO>> getActionsByMission(@PathVariable Long missionId) {
        List<ActionDTO> out = actionService.findByMissionId(missionId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(out);
    }

    @PutMapping("/{id}")

    public ResponseEntity<ActionDTO> updateAction(@PathVariable Long id, @RequestBody ActionDTO dto) {
        var existingOpt = actionService.findById(id);
        if (existingOpt.isEmpty()) return ResponseEntity.notFound().build();
        Action existing = existingOpt.get();
        if (existing.getPv() != null) return new ResponseEntity<>(HttpStatus.FORBIDDEN); // ← figée
        dto.setActionId(id);
        Action entity = fromDto(dto);
        entity.setCreatedAt(existing.getCreatedAt());
        Action saved = actionService.update(entity);
        return ResponseEntity.ok(toDto(saved));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        if (actionService.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        actionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ========= Filtres =========
    @GetMapping("/type/{type}")

    public ResponseEntity<List<ActionDTO>> getActionsByType(@PathVariable TypeAction type) {
        var dtos = actionService.findByType(type).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActionDTO>> getActionsByUserId(@PathVariable String userId) {
        var dtos = actionService.findByUserId(userId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/douar/{douarId}")

    public ResponseEntity<List<ActionDTO>> getActionsByDouarId(@PathVariable Long douarId) {
        var dtos = actionService.findByDouarId(douarId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/location")

    public ResponseEntity<List<ActionDTO>> getActionsByLocation(@RequestParam String prefecture, @RequestParam String commune) {
        var dtos = actionService.findByLocation(prefecture, commune).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/date-range")

    public ResponseEntity<List<ActionDTO>> getActionsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        var dtos = actionService.findByDateRange(startDate, endDate).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/with-pv")

    public ResponseEntity<List<ActionDTO>> getActionsWithPV() {
        var dtos = actionService.findActionsWithPV().stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-actions")

    public ResponseEntity<List<ActionDTO>> getMyActions() {
        String currentUserId = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        var dtos = actionService.findByUserId(currentUserId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/demolitions")

    public ResponseEntity<List<ActionDTO>> getDemolitions() {
        var dtos = actionService.findByType(TypeAction.DEMOLITION).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/signalements")

    public ResponseEntity<List<ActionDTO>> getSignalements() {
        var dtos = actionService.findByType(TypeAction.SIGNALEMENT).stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    // ========= Upload photos =========

    @PostMapping("/{id}/photo-avant")
    @PreAuthorize("hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> uploadPhotoAvant(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
        Action a = actionService.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();
        String url = fichierService.storeActionPhoto(id, "avant", file); // ← maintenant résolu
        a.setPhotoAvantUrl(url);
        Action saved = actionService.update(a);
        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping("/{id}/photo-apres")
    @PreAuthorize("hasRole('AGENT_AUTORITE')")
    public ResponseEntity<ActionDTO> uploadPhotoApres(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
        Action a = actionService.findById(id).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();
        String url = fichierService.storeActionPhoto(id, "apres", file);
        a.setPhotoApresUrl(url);
        Action saved = actionService.update(a);
        return ResponseEntity.ok(toDto(saved));
    }



    // --- utilitaire local pour sauvegarder un fichier uploadé ---
    private String saveActionFile(Long id, MultipartFile file, String slot) throws Exception {
        if (!FileUtils.isValidFileType(file) || !FileUtils.isValidFileSize(file)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file");
        }
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
        String filename = slot + "-" + FileUtils.generateUniqueFileName(original);



        Path uploadRoot = Paths.get("uploads", "actions", id.toString());
        Files.createDirectories(uploadRoot);

        Path target = uploadRoot.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Retourne l'URL publique (assure-toi d'exposer /files/** -> dossier "uploads/")
        return "/files/actions/" + id + "/" + filename;
    }
}
