package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.PV;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public interface PVService {
    Optional<PV> findById(Long id);
    Optional<PV> findByActionId(Long actionId);
    PV create(PV pv);
    PV update(PV pv);
    void delete(Long id);

    // Crée un PV (brouillon) unique pour une action
    PV createForAction(Long actionId, String contenuJSON, String redacteurUserId);
}
