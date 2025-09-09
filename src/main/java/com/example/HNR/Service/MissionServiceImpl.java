package com.example.HNR.Service;

import com.example.HNR.DTO.MissionDTO;
import com.example.HNR.DTO.Request.MissionCreateRequest;
import com.example.HNR.DTO.Request.MissionUpdateRequest;
import com.example.HNR.Model.SqlServer.Mission;
import com.example.HNR.Model.enums.Statut;
import com.example.HNR.Repository.SqlServer.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;

    @Override
    public List<Mission> findAll() {
        return missionRepository.findAll();
    }

    @Override
    public Optional<Mission> findByMissionId(Long missionId) {
        return missionRepository.findById(missionId);
    }

    @Override
    public List<MissionDTO> findByAssignedUser(String userId) {
        return missionRepository.findByAssignedUserId(userId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public MissionDTO create(MissionCreateRequest req, String createdByUserId) {
        Objects.requireNonNull(req, "MissionCreateRequest null");

        Mission m = new Mission();
        m.setTitre(req.getTitre());
        m.setDescription(req.getDescription());
        m.setPrefecture(req.getPrefecture());
        m.setCommune(req.getCommune());
        m.setDateEnvoi(req.getDateEnvoi() != null ? req.getDateEnvoi() : OffsetDateTime.now());
        m.setStatut(Statut.EN_COURS);
        m.setAssignedUserId(req.getAssignedUserId());
        m.setCreatedByUserId(createdByUserId);
        m.setChangementId(req.getChangementId());
        m.setGeometryType(req.getGeometryType() != null ? req.getGeometryType() : "POLYGON");
        m.setPolygonWKT(req.getPolygonWKT());

        Mission saved = missionRepository.save(m);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public MissionDTO update(Long missionId, MissionUpdateRequest req) {
        Mission m = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable: " + missionId));

        if (req.getTitre() != null) m.setTitre(req.getTitre());
        if (req.getDescription() != null) m.setDescription(req.getDescription());
        if (req.getPrefecture() != null) m.setPrefecture(req.getPrefecture());
        if (req.getCommune() != null) m.setCommune(req.getCommune());
        if (req.getDateEnvoi() != null) m.setDateEnvoi(req.getDateEnvoi());
        if (req.getAssignedUserId() != null) m.setAssignedUserId(req.getAssignedUserId());
        if (req.getChangementId() != null) m.setChangementId(req.getChangementId());
        if (req.getGeometryType() != null) m.setGeometryType(req.getGeometryType());
        if (req.getPolygonWKT() != null) m.setPolygonWKT(req.getPolygonWKT());
        if (req.getRapportPdf() != null) m.setRapportPdf(req.getRapportPdf());
        if (req.getStatut() != null) m.setStatut(Statut.valueOf(req.getStatut()));

        Mission saved = missionRepository.save(m);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long missionId) {
        missionRepository.deleteById(missionId);
    }

    private MissionDTO toDTO(Mission m) {
        MissionDTO d = new MissionDTO();
        d.setMissionId(m.getMissionId());
        d.setTitre(m.getTitre());
        d.setDescription(m.getDescription());
        d.setPrefecture(m.getPrefecture());
        d.setCommune(m.getCommune());
        d.setDateEnvoi(m.getDateEnvoi());
        d.setStatut(m.getStatut());
        d.setAssignedUserId(m.getAssignedUserId());
        d.setCreatedByUserId(m.getCreatedByUserId());
        d.setChangementId(m.getChangementId());
        d.setGeometryType(m.getGeometryType());
        d.setPolygonWKT(m.getPolygonWKT());
        d.setRapportPdf(m.getRapportPdf());
        d.setCreatedAt(m.getCreatedAt());
        d.setUpdatedAt(m.getUpdatedAt());
        return d;
    }
}
