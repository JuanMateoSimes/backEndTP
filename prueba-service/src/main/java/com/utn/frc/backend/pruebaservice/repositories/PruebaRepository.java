package com.utn.bda.agenciavehiculos.repositories;

import com.utn.bda.agenciavehiculos.models.Prueba;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface PruebaRepository extends CrudRepository<Prueba, Integer> {
    List<Prueba> findByPrFechaHoraFinIsNull(Timestamp fechaHora) {}
}
