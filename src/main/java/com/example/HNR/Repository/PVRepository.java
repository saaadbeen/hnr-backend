package com.example.HNR.Repository;

import com.example.HNR.Model.PV;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PVRepository extends MongoRepository<PV, String> {
    List<PV> findByIdDouar(String idDouar);
    List<PV> findByMissionId(String missionId);
    List<PV> findByRedacteurId(String redacteurId);

}
