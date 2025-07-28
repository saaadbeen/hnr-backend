package com.example.HNR.Controller;

import com.example.HNR.Model.Action;
import com.example.HNR.Model.Changement;
import com.example.HNR.Model.Douar;
import com.example.HNR.Model.PV;
import com.example.HNR.Service.DouarService;
import com.example.HNR.repository.ActionRepository;
import com.example.HNR.repository.ChangementRepository;
import com.example.HNR.repository.PVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/douars")
@CrossOrigin(origins = "*")
public class DouarController {

    @Autowired
    private DouarService douarService;

    // Pour l’instant, on garde ces repos, on migrera vers des services plus tard
    @Autowired
    private com.example.HNR.Service.PVService pvService;
    @Autowired
    private com.example.HNR.Service.ChangementService changementService;
    @Autowired
    private com.example.HNR.Service.ActionService actionService;


    // --- CRUD sur les douars ---

    @GetMapping
    public List<Douar> getAllDouars() {
        return douarService.getAllDouars();
    }

    @GetMapping("/{id}")
    public Douar getDouarById(@PathVariable String id) {
        return douarService.getDouarById(id).orElse(null);
    }

    @PostMapping
    public Douar createDouar(@RequestBody Douar douar) {
        return douarService.saveDouar(douar);
    }

    @PutMapping("/{id}")
    public Douar updateDouar(@PathVariable String id, @RequestBody Douar douar) {
        // Mets à jour l’ID de l’entité avant enregistrement
        douar.setIdDouar(id);
        return douarService.saveDouar(douar);
    }

    @DeleteMapping("/{id}")
    public void deleteDouar(@PathVariable String id) {
        douarService.deleteDouar(id);
    }

    // --- Endpoints “filles” pour PV, Changement et Action ---
    @GetMapping("/{id}/pvs")
    public List<PV> getPvsByDouar(@PathVariable String id) {
        return pvService.findByIdDouar(id);
    }

    @GetMapping("/{id}/changements")
    public List<Changement> getChangementsByDouar(@PathVariable String id) {
        return changementService.findByIdDouar(id);
    }

    @GetMapping("/{id}/actions")
    public List<Action> getActionsByDouar(@PathVariable String id) {
        return actionService.findByIdDouar(id);
    }
}
