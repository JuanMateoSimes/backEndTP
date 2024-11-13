package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.dtos.PosicionDTO;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.utn.frc.backend.pruebaservice.services.VehiculoService;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.obtenerTodosLosVehiculos();
    }

    @PostMapping("/posicion")
    public ResponseEntity<PosicionDTO> recibirPosicionVehiculo(@RequestBody PosicionDTO posicionDTO) {
        // Registrar la posición y obtener el DTO con la información registrada
        PosicionDTO posicionRegistrada = vehiculoService.registrarPosicionVehiculo(posicionDTO);

        // Retornar la posición registrada con el código de estado 201 (creado)
        return ResponseEntity.status(HttpStatus.CREATED).body(posicionRegistrada);
    }
}