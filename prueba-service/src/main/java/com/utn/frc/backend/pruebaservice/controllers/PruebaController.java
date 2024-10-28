package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.services.PruebaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pruebas")
public class PruebaController {

    @Autowired
    private PruebaService pruebaService;

    @PostMapping
    public ResponseEntity<String> crearPrueba(@RequestBody PruebaDTO pruebaDTO) {
        pruebaService.crearPrueba(pruebaDTO);
        return ResponseEntity.ok("Prueba creada exitosamente.");
    }

    @GetMapping("/en-curso")
    public List<Prueba> listarPruebasEnCurso() {
        return pruebaService.obtenerPruebasEnCurso();
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<String> finalizarPrueba(@PathVariable Integer id, @RequestBody PruebaDTO pruebaDTO) {
        pruebaService.finalizarPrueba(id, pruebaDTO);
        return ResponseEntity.ok("Prueba finalizada exitosamente.");
    }

}