package com.example.HNR.Service;

import com.example.HNR.Model.PV;
import com.example.HNR.Repository.PVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;

@Service
public class PVService {

    @Autowired
    private PVRepository pvRepository;

    // Créer un PV
    public PV createPV(PV pv) {
        if (pvRepository.existsByNumero(pv.getNumero())) {
            throw new RuntimeException("Un PV avec ce numéro existe déjà");
        }

        pv.setDateRedaction(new Date());
        return pvRepository.save(pv);
    }

    // Trouver par ID
    public Optional<PV> findById(String id) {
        return pvRepository.findById(id);
    }

    // Trouver par numéro
    public Optional<PV> findByNumero(String numero) {
        return pvRepository.findByNumero(numero);
    }

    // Obtenir tous les PVs
    public List<PV> findAll() {
        return pvRepository.findAll();
    }

    // Trouver par rédacteur
    public List<PV> findByRedacteur(String redacteur) {
        return pvRepository.findByRedacteur(redacteur);
    }

    // Trouver par statut de validation
    public List<PV> findByValide(boolean valide) {
        return pvRepository.findByValide(valide);
    }

    // PVs entre deux dates
    public List<PV> findByDateRedactionBetween(Date startDate, Date endDate) {
        return pvRepository.findByDateRedactionBetween(startDate, endDate);
    }

    // PVs validés
    public List<PV> findValidatedPVs() {
        return pvRepository.findValidatedPVs();
    }

    // PVs en attente
    public List<PV> findPendingPVs() {
        return pvRepository.findPendingPVs();
    }

    // PVs récents par rédacteur
    public List<PV> findRecentPVsByRedacteur(String redacteur) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return pvRepository.findRecentPVsByRedacteur(redacteur, cal.getTime());
    }

    // Valider un PV (sans signature)
    public PV validatePV(String id) {
        PV pv = pvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PV non trouvé"));

        pv.setValide(true);
        return pvRepository.save(pv);
    }

    // Mettre à jour un PV
    public PV updatePV(String id, PV pvDetails) {
        PV pv = pvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PV non trouvé avec ID: " + id));

        if (pvDetails.getContenu() != null) {
            pv.setContenu(pvDetails.getContenu());
        }
        if (pvDetails.getUrlPDF() != null) {
            pv.setUrlPDF(pvDetails.getUrlPDF());
        }

        pv.setValide(pvDetails.isValide());

        return pvRepository.save(pv);
    }

    // Supprimer un PV
    public void deletePV(String id) {
        PV pv = pvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PV non trouvé avec ID: " + id));
        pvRepository.delete(pv);
    }

    // Compter PVs validés
    public long countByValide(boolean valide) {
        return pvRepository.countByValide(valide);
    }
}