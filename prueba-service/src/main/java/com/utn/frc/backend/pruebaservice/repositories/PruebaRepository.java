package com.utn.frc.backend.pruebaservice.repositories;

import com.utn.frc.backend.pruebaservice.models.Posicion;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PruebaRepository extends CrudRepository<Prueba, Integer> {
    // Método para obtener todas las pruebas en curso (que no han finalizado)
    List<Prueba> findByPrFechaHoraFinIsNull();

    // Método para obtener todas las pruebas en curso antes de una fecha específica
    List<Prueba> findByPrFechaHoraInicioBeforeAndPrFechaHoraFinIsNull(Timestamp now);

    // Método para contar las pruebas activas para un vehículo específico
    Integer countByVehiculoAndPrFechaHoraFinIsNull(Vehiculo vehiculo);

    Boolean existsByVehiculoAndPrFechaHoraFinIsNull(Vehiculo vehiculo);

    Optional<Prueba> findByVehiculoAndPrFechaHoraFinIsNull(Vehiculo vehiculo);

    List<Prueba> findByVehiculo(Vehiculo vehiculo);

}
