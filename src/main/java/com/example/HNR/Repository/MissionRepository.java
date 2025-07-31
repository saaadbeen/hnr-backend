package com.example.HNR.Repository;

import com.example.HNR.Model.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MissionRepository extends MongoRepository<Mission, String> {
    List<Mission> findByCreateurId(String createurId);
    List<Mission> findByPrefecture(String prefecture);
    List<Mission> findByCommune(String commune);
    List<Mission> findByNomContainingIgnoreCase(String nom);

}
