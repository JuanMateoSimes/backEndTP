package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import com.utn.frc.backend.pruebaservice.services.VehiculoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
