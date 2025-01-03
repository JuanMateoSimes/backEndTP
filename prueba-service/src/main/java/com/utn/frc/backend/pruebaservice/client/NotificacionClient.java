package com.utn.frc.backend.pruebaservice.client;

import com.utn.frc.backend.pruebaservice.dtos.NotificacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "notificacion-service", url = "${gateway.url.notificacion-service}")
public interface NotificacionClient {
    @PostMapping("/notificaciones")
    void crearNotificacion(@RequestBody NotificacionDTO notificacionDTO);

    @GetMapping("/notificaciones")
    List<NotificacionDTO> obtenerNotificaciones();
}