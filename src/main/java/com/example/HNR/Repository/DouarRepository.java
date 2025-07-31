package com.example.HNR.Repository;

import com.example.HNR.Model.Douar;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DouarRepository extends MongoRepository<Douar, String> {
    List<Douar> findByMissionIdsContaining(String missionId);

}
