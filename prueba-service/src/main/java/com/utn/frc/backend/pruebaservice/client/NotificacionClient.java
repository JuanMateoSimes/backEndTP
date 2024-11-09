package com.utn.frc.backend.pruebaservice.client;

import com.utn.frc.backend.pruebaservice.config.FeignConfig;
import com.utn.frc.backend.pruebaservice.dtos.NotificacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificacion-service", url = "${gateway.url.notificacion-service}", configuration = FeignConfig.class)
public interface NotificacionClient {
    @PostMapping("/notificaciones")
    void crearNotificacion(@RequestBody NotificacionDTO notificacionDTO);
}