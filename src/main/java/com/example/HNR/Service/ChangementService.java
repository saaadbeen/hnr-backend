package com.example.HNR.Service;

import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.enums.TypeExtension;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChangementService {
    Changement create(Changement changement);
    Optional<Changement> findById(Long id);
    Page<Changement> findAll(Pageable pageable);
    Changement update(Changement changement);
    void delete(Long id);

    // Méthodes métier spécifiques
    List<Changement> findByType(TypeExtension type);
    List<Changement> findByDouarId(Long douarId);
    List<Changement> findByDetectedByUserId(String userId);
    List<Changement> findByDateRange(Date startDate, Date endDate);
    List<Changement> findBySurfaceMinimum(Double minSurface);
}