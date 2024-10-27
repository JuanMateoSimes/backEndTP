package com.utn.frc.backend.pruebaservice.repositories;

import com.utn.frc.backend.pruebaservice.models.Empleado;
import org.springframework.data.repository.CrudRepository;

public interface EmpleadoRepository extends CrudRepository <Empleado, Integer> {
}
