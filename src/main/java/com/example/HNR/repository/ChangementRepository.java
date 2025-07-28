package com.example.HNR.repository;

import com.example.HNR.Model.Changement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChangementRepository extends MongoRepository<Changement, String> {


    List<Changement> findByIdDouar(String idDouar);

    Changement findByCodeChangement(String codeChangement);
}
