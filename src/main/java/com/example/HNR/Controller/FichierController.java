package com.example.HNR.Controller;

import com.example.HNR.Model.Fichier;
import com.example.HNR.Service.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/fichiers")
public class FichierController {

    @Autowired
    private FichierService fichierService;

    @GetMapping
    public List<Fichier> getAll() {
        return fichierService.getAllFichiers();
    }

    @GetMapping("/{id}")
    public Fichier getById(@PathVariable String id) {
        return fichierService.getFichierById(id);
    }

    @GetMapping("/mission/{missionId}")
    public List<Fichier> getByMission(@PathVariable String missionId) {
        return fichierService.findByMissionId(missionId);
    }

    @GetMapping("/changement/{changementId}")
    public List<Fichier> getByChangement(@PathVariable String changementId) {
        return fichierService.findByChangementId(changementId);
    }

    @PostMapping
    public Fichier create(@RequestBody Fichier fichier) {
        return fichierService.createFichier(fichier);
    }

    @PutMapping("/{id}")
    public Fichier update(@PathVariable String id, @RequestBody Fichier fichier) {
        return fichierService.updateFichier(id, fichier);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        fichierService.deleteFichier(id);
    }
}