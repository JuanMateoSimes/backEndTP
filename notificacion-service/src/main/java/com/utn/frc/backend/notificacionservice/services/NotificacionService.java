package com.utn.frc.backend.notificacionservice.services;

import com.utn.frc.backend.notificacionservice.dtos.NotificacionDTO;
import com.utn.frc.backend.notificacionservice.models.Notificacion;
import com.utn.frc.backend.notificacionservice.repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public void guardarNotificacion(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = new Notificacion();
        notificacion.setPruebaId(notificacionDTO.getPruebaId());
        notificacion.setVehiculoId(notificacionDTO.getVehiculoId());
        notificacion.setEmpleadoTelefono(notificacionDTO.getEmpleadoTelefono());
        notificacion.setMensaje(notificacionDTO.getMensaje());
        notificacion.setFechaHora(notificacionDTO.getFechaHora());
        System.out.println("Fecha antes de guardar: " + notificacion.getFechaHora());
        System.out.println(notificacion);


        notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificaciones() {
        return (List<Notificacion>) notificacionRepository.findAll();
    }


}
