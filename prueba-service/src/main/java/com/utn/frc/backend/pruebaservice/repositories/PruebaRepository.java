package com.utn.frc.backend.pruebaservice.repositories;

import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PruebaRepository extends CrudRepository<Prueba, Integer> {

    @Query("SELECT p FROM Prueba p WHERE p.prFechaHoraFin IS NULL OR p.prFechaHoraFin > :now")
    List<Prueba> findByPrFechaHoraFinIsNullOrFuture(@Param("now")Timestamp now);

    List<Prueba> findAll();

    @Query("SELECT COUNT(p) FROM Prueba p WHERE p.vehiculo = :vehiculo AND (p.prFechaHoraFin IS NULL OR p.prFechaHoraFin > :now)")
    Integer countByVehiculoAndPrFechaHoraFinIsNullOrFuture(@Param("vehiculo") Vehiculo vehiculo, @Param("now") Timestamp now);

    @Query("SELECT p FROM Prueba p WHERE p.vehiculo = :vehiculo AND (p.prFechaHoraFin IS NULL OR p.prFechaHoraFin > :now)")
    Optional<Prueba> findByVehiculoAndPrFechaHoraFinIsNullOrFuture(@Param("vehiculo") Vehiculo vehiculo, @Param("now") Timestamp now);


    List<Prueba> findByVehiculo(Vehiculo vehiculo);

}
