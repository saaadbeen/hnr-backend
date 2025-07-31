package com.example.HNR.Controller;

import com.example.HNR.Model.Changement;
import com.example.HNR.Service.ChangementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/changements")
@RequiredArgsConstructor
public class ChangementController {

    private final ChangementService changementService;

    @GetMapping
    public List<Changement> getAllChangements() {
        return changementService.getAllChangements();
    }

    @GetMapping("/{id}")
    public Optional<Changement> getChangementById(@PathVariable String id) {
        return changementService.getChangementById(id);
    }

    @PostMapping
    public Changement createChangement(@RequestBody Changement changement) {
        return changementService.createChangement(changement);
    }

    @DeleteMapping("/{id}")
    public void deleteChangement(@PathVariable String id) {
        changementService.deleteChangement(id);
    }

    @GetMapping("/douar/{douarId}")
    public List<Changement> getChangementsByDouarId(@PathVariable String douarId) {
        return changementService.getChangementsByDouarId(douarId);
    }
}