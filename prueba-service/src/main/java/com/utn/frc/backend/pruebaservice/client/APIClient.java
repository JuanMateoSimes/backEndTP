package com.utn.frc.backend.pruebaservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apiClient", url = "https://labsys.frc.utn.edu.ar/apps-disponibilizadas/backend/api/v1/configuracion/")
public interface APIClient {
    @GetMapping
    ConfiguracionDTO obtenerConfiguracion();
}
