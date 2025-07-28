package com.example.HNR.repository;

import com.example.HNR.Model.Action;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ActionRepository extends MongoRepository<Action, String> {
    List<Action> findByIdDouar(String idDouar);
}
