package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByAssignedUserId(String assignedUserId);
    long countByStatut(String statut);

}


