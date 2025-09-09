// src/main/java/com/example/HNR/Service/MissionService.java
package com.example.HNR.Service;

import com.example.HNR.DTO.MissionDTO;
import com.example.HNR.DTO.Request.MissionCreateRequest;
import com.example.HNR.DTO.Request.MissionUpdateRequest;
import com.example.HNR.Model.SqlServer.Mission;

import java.util.List;
import java.util.Optional;

public interface MissionService {
    List<Mission> findAll();
    Optional<Mission> findByMissionId(Long missionId);
    List<MissionDTO> findByAssignedUser(String userId);

    MissionDTO create(MissionCreateRequest req, String createdByUserId);
    MissionDTO update(Long missionId, MissionUpdateRequest req);
    void delete(Long missionId);
}
