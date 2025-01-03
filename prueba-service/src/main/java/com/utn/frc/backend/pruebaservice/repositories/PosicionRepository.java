package com.utn.frc.backend.pruebaservice.repositories;

import com.utn.frc.backend.pruebaservice.models.Posicion;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface PosicionRepository extends CrudRepository<Posicion, Integer> {

    List<Posicion> findByVehiculoAndPosFechaHoraBetween(Vehiculo vehiculo, Timestamp inicio, Timestamp fin);

    boolean existsByVehiculoAndPosFechaHora(Vehiculo vehiculo, Timestamp fechaHora);

}
