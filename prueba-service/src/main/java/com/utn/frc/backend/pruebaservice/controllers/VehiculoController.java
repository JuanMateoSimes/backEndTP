package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.dtos.PosicionDTO;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> recibirPosicionVehiculo(@RequestBody PosicionDTO posicionDTO) {
        vehiculoService.registrarPosicionVehiculo(posicionDTO);
        return ResponseEntity.ok().build();
    }
}
