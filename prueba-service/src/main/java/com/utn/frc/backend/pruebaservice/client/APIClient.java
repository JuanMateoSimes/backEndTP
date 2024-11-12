package com.utn.frc.backend.pruebaservice.client;

import com.utn.frc.backend.pruebaservice.dtos.ConfiguracionDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apiClient", url = "https://labsys.frc.utn.edu.ar")
public interface APIClient {
    @GetMapping("/apps-disponibilizadas/backend/api/v1/configuracion/")
    ConfiguracionDTO obtenerConfiguracion();
}
