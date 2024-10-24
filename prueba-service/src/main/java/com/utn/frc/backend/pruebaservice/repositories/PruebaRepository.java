package com.utn.frc.backend.pruebaservice.repositories;

import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface PruebaRepository<Prueba> extends CrudRepository<Prueba, Integer> {
    // Método para obtener todas las pruebas en curso (que no han finalizado)
    List<Prueba> findByFechaHoraFinIsNull();

    // Método para obtener todas las pruebas en curso antes de una fecha específica
    List<Prueba> findByFechaHoraInicioBeforeAndFechaHoraFinIsNull(Timestamp now);

    // Método para contar las pruebas activas para un vehículo específico
    Integer countByIdVehiculoAndFechaHoraFinIsNull(Vehiculo vehiculo);
}
