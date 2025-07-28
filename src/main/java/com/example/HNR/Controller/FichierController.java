package com.example.HNR.Controller;

import com.example.HNR.Model.Fichier;
import com.example.HNR.Service.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichiers")
@CrossOrigin(origins = "*")
public class FichierController {

    @Autowired
    private FichierService fichierService;

    // --- CRUD de base ---

    @GetMapping
    public List<Fichier> getAllFichiers() {
        return fichierService.getAllFichiers();
    }

    @GetMapping("/{id}")
    public Fichier getFichierById(@PathVariable String id) {
        return fichierService.getFichierById(id).orElse(null);
    }

    @PostMapping
    public Fichier createFichier(@RequestBody Fichier fichier) {
        return fichierService.saveFichier(fichier);
    }

    @PutMapping("/{id}")
    public Fichier updateFichier(@PathVariable String id, @RequestBody Fichier fichier) {
        fichier.setId(id);
        return fichierService.saveFichier(fichier);
    }

    @DeleteMapping("/{id}")
    public void deleteFichier(@PathVariable String id) {
        fichierService.deleteFichier(id);
    }

    // --- Requête personnalisée ---

    @GetMapping("/douar/{douarId}")
    public List<Fichier> getFichiersByDouar(@PathVariable String douarId) {
        return fichierService.getFichiersByDouarId(douarId);
    }
}
