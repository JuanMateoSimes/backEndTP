package com.utn.frc.backend.pruebaservice.controllers;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.dtos.ReporteIncidenteDTO;
import com.utn.frc.backend.pruebaservice.services.VehiculoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReportesController {
    private final VehiculoService vehiculoService;

    public ReportesController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/incidentes")
    public List<ReporteIncidenteDTO> obtenerReporteIncidentes() {
        return vehiculoService.reporteIncidentes();
    }

    @GetMapping("/incidentes/{legajo}")
    public List<ReporteIncidenteDTO> obtenerIncidentePorEmpleado(@PathVariable Integer legajo) {
        return vehiculoService.reporteIncidentesPorEmpleado(legajo);
    }
    @GetMapping("/{vehiculoId}/kilometraje")
    public double obtenerKilometrajeRecorrido(
            @PathVariable Integer vehiculoId,
            @RequestParam String inicio,
            @RequestParam String fin) {

        // Convertimos las fechas de entrada a Timestamp
        Timestamp fechaInicio = Timestamp.valueOf(inicio);
        Timestamp fechaFin = Timestamp.valueOf(fin);

        return vehiculoService.kilometrajeRecorrido(vehiculoId, fechaInicio, fechaFin);
    }
    @GetMapping("/detalles/{id}")
    public List<PruebaDTO> obtenerPruebasPorVehiculo(@PathVariable Integer id) {
        return vehiculoService.pruebasPorVehiculo(id);
    }
}
