package com.example.HNR.Repository;

import com.example.HNR.Model.Changement;
import com.example.HNR.Model.TypeExtension;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChangementRepository extends MongoRepository<Changement, String> {

    // Recherche par type d'extension
    List<Changement> findByType(TypeExtension type);

    // Recherche par douar
    List<Changement> findByDouarId(String douarId);


    // Changements par surface minimale
    @Query("{'surface': {$gte: ?0}}")
    List<Changement> findBySurfaceGreaterThanEqual(double minSurface);

    // Changements avec photos avant et après
    @Query("{'photoAvant': {$exists: true, $ne: null}, 'photoApres': {$exists: true, $ne: null}}")
    List<Changement> findChangementsWithBothPhotos();


    // Compter par type
    long countByType(TypeExtension type);

    // Surface totale des changements par douar
    @Query(value = "{'douarId': ?0}", fields = "{'surface': 1}")
    List<Changement> findSurfacesByDouarId(String douarId);
}