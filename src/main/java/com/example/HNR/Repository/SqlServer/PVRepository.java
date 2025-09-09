package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.PV;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PVRepository extends JpaRepository<PV, Long> {

    /** Charge PV + action (+ douar) pour éviter LazyInitialization côté mapping DTO */
    @EntityGraph(attributePaths = {"action", "action.douar"})
    Optional<PV> findByAction_ActionId(Long actionId);

    /** Idem pour findById */
    @Override
    @EntityGraph(attributePaths = {"action", "action.douar"})
    Optional<PV> findById(Long id);
}
