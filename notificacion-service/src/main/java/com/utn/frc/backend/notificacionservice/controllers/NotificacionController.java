package com.utn.frc.backend.notificacionservice.controllers;

import com.utn.frc.backend.notificacionservice.dtos.NotificacionDTO;
import com.utn.frc.backend.notificacionservice.models.Notificacion;
import com.utn.frc.backend.notificacionservice.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Autowired
    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void crearNotificacion(@RequestBody NotificacionDTO notificacionDTO) {
        notificacionService.guardarNotificacion(notificacionDTO);
    }
    @GetMapping()
    public List<Notificacion> obtenerNotificaciones() {return notificacionService.obtenerNotificaciones();}
}
