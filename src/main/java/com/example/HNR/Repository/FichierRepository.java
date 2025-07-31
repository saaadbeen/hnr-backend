package com.example.HNR.Repository;

import com.example.HNR.Model.Fichier;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FichierRepository extends MongoRepository<Fichier, String> {
    List<Fichier> findByMissionId(String missionId);
    List<Fichier> findByChangementId(String changementId);
}
