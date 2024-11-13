package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.services.PruebaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pruebas")
public class PruebaController {

    @Autowired
    private PruebaService pruebaService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPrueba(@RequestBody PruebaDTO pruebaDTO) {
        // Llamada al servicio para crear la prueba
        PruebaDTO nuevaPrueba = pruebaService.crearPrueba(pruebaDTO);

        // Crear un Map para incluir el mensaje y el objeto de prueba
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Prueba registrada exitosamente");
        response.put("prueba", nuevaPrueba);

        // Retornar el mapa con el código de estado 201 (creado)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping()
    public List<Prueba> listarPruebas() {return pruebaService.obtenerPruebas();}

    @GetMapping("/en-curso")
    public List<Prueba> listarPruebasEnCurso() {
        return pruebaService.obtenerPruebasEnCurso();
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Map<String, Object>> finalizarPrueba(@PathVariable Integer id, @RequestBody PruebaDTO pruebaDTO) {
        // Llamada al servicio para finalizar la prueba y obtener el DTO con los detalles
        PruebaDTO pruebaFinalizada = pruebaService.finalizarPrueba(id, pruebaDTO);

        // Crear un Map para incluir el mensaje y los detalles de la prueba finalizada
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Prueba finalizada exitosamente");
        response.put("prueba", pruebaFinalizada);

        // Retornar el mapa en el cuerpo de la respuesta con un código 200 (OK)
        return ResponseEntity.ok().body(response);
    }



}