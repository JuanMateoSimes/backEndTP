package com.utn.frc.backend.notificacionservice.repositories;

import com.utn.frc.backend.notificacionservice.models.Notificacion;
import org.springframework.data.repository.CrudRepository;

public interface NotificacionRepository extends CrudRepository<Notificacion, Integer> {
}
