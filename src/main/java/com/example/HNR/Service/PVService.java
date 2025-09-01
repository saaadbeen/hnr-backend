package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.PV;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PVService {
    PV create(PV pv);
    Optional<PV> findById(Long id);
    Page<PV> findAll(Pageable pageable);
    PV update(PV pv);
    void delete(Long id);

    // Méthodes métier spécifiques
    List<PV> findByRedacteurUserId(String userId);
    Optional<PV> findByActionId(Long actionId);
    List<PV> findByDateRange(Date startDate, Date endDate);
    List<PV> findPVsWithPDF();
    void validerPV(Long id, String validateurUserId);
}