package com.example.HNR.Repository;

import com.example.HNR.Model.PV;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PVRepository extends MongoRepository<PV, String> {

    // Recherche par numéro unique
    Optional<PV> findByNumero(String numero);

    // Vérifier si numéro existe
    boolean existsByNumero(String numero);

    // Recherche par rédacteur
    List<PV> findByRedacteur(String redacteur);

    // Recherche par statut de validation
    List<PV> findByValide(boolean valide);

    // PVs rédigés entre deux dates
    List<PV> findByDateRedactionBetween(Date startDate, Date endDate);

    // PVs validés
    @Query("{'valide': true}")
    List<PV> findValidatedPVs();

    // PVs en attente de validation
    @Query("{'valide': false}")
    List<PV> findPendingPVs();

    // Compter PVs validés
    long countByValide(boolean valide);

    // PVs récents par rédacteur
    @Query("{'redacteur': ?0, 'dateRedaction': {$gte: ?1}}")
    List<PV> findRecentPVsByRedacteur(String redacteur, Date sinceDate);
}