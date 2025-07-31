package com.example.HNR.Repository;

import com.example.HNR.Model.Action;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ActionRepository extends MongoRepository<Action, String> {
    List<Action> findByUtilisateur_Id(String utilisateurId);
    List<Action> findByIdMission(String idMission);
    List<Action> findByIdDouar(String idDouar);
    List<Action> findByIdPv(String idPv);
}
