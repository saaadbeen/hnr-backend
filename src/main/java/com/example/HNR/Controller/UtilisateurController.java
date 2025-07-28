package com.example.HNR.Controller;

import com.example.HNR.Model.Utilisateur;
import com.example.HNR.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // --- CRUD de base ---

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @GetMapping("/{id}")
    public Utilisateur getUtilisateurById(@PathVariable String id) {
        return utilisateurService.getUtilisateurById(id).orElse(null);
    }

    @PostMapping
    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.saveUtilisateur(utilisateur);
    }

    @PutMapping("/{id}")
    public Utilisateur updateUtilisateur(@PathVariable String id, @RequestBody Utilisateur utilisateur) {
        // Ajuste ici selon le nom du setter de l'ID dans ta classe Utilisateur
        utilisateur.setId(id);
        return utilisateurService.saveUtilisateur(utilisateur);
    }

    @DeleteMapping("/{id}")
    public void deleteUtilisateur(@PathVariable String id) {
        utilisateurService.deleteUtilisateur(id);
    }
}
