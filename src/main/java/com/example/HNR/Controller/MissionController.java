package com.example.HNR.Controller;

import com.example.HNR.DTO.MissionDTO;
import com.example.HNR.DTO.Request.MissionCreateRequest;
import com.example.HNR.DTO.Request.MissionUpdateRequest;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Service.MissionService;
import com.example.HNR.Service.UserService;
import com.example.HNR.Model.Mongodb.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final UserService userService;

    @GetMapping
    public List<Mission> list() {
        return missionService.findAll();
    }

    @GetMapping("/{missionId:\\d+}")
    public ResponseEntity<Mission> get(@PathVariable("missionId") Long missionId) {
        return missionService.findByMissionId(missionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Missions affectées à un agent (front transmet userId fonctionnel) */
    @GetMapping("/agent/{userId}")
    public List<MissionDTO> byAgent(@PathVariable String userId) {
        return missionService.findByAssignedUser(userId);
    }

    // Missions assignées à l'utilisateur authentifié
    @GetMapping("/assigned")
    public ResponseEntity<List<MissionDTO>> assigned(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        java.util.Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        List<MissionDTO> missions = missionService.findByAssignedUser(userOpt.get().getUserid());
        return ResponseEntity.ok(missions);
    }

    @PostMapping
    public MissionDTO create(@RequestBody MissionCreateRequest req, Authentication auth) {
        String createdByUserId = (auth != null) ? auth.getName() : "system";
        return missionService.create(req, createdByUserId);
    }

    @PutMapping("/{missionId:\\d+}")
    public MissionDTO update(@PathVariable("missionId") Long missionId, @RequestBody MissionUpdateRequest req) {
        return missionService.update(missionId, req);
    }

    @DeleteMapping("/{missionId:\\d+}")
    public void delete(@PathVariable("missionId") Long missionId) {
        missionService.delete(missionId);
    }
}
