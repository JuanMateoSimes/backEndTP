package com.utn.frc.backend.pruebaservice.dtos;

import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.models.Interesado;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PruebaDTO {
    private Vehiculo vehiculo;
    private Interesado interesado;
    private Empleado empleado;
    private LocalDateTime fechaHoraInicio;  // Fecha y hora de inicio de la prueba
    private LocalDateTime fechaHoraFin;     // Fecha y hora de fin de la prueba (para finalizar)
    private String comentarios;

    public PruebaDTO(Prueba prueba) {
        this.vehiculo = prueba.getVehiculo();
        this.interesado = prueba.getInteresado();
        this.empleado = prueba.getEmpleado();
        this.fechaHoraInicio = prueba.getPrFechaHoraInicio();
        this.fechaHoraFin = prueba.getPrFechaHoraFin();
        this.comentarios = prueba.getPrComentarios();
    }
}
