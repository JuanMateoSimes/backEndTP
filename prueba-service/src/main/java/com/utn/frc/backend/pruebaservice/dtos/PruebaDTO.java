package com.utn.frc.backend.pruebaservice.dtos;

import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.models.Interesado;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PruebaDTO {
    private Vehiculo vehiculo;        // ID del vehículo que se probará
    private Interesado interesado;      // ID del interesado (cliente)
    private Empleado empleado;        // ID del empleado que supervisa la prueba
    private Timestamp fechaHoraInicio;  // Fecha y hora de inicio de la prueba
    private Timestamp fechaHoraFin;     // Fecha y hora de fin de la prueba (para finalizar)
    private String comentarios;
}
