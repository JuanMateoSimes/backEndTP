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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PruebaDTO {
    private Integer vehiculoId; // ID del vehículo en lugar del objeto
    private Integer interesadoId; // ID del interesado (si deseas cambiarlo)
    private Integer empleadoLegajo; // ID del empleado (si deseas cambiarlo)
    private Timestamp fechaHoraInicio = new Timestamp(System.currentTimeMillis());  // Fecha y hora de inicio de la prueba
    private Timestamp fechaHoraFin;     // Fecha y hora de fin de la prueba (para finalizar)
    private String comentarios; // Comentarios sobre la prueba

    public PruebaDTO(Prueba prueba) {
        this.vehiculoId = prueba.getVehiculo() != null ? prueba.getVehiculo().getVehId() : null;
        this.interesadoId = prueba.getInteresado() != null ? prueba.getInteresado().getInteId() : null;
        this.empleadoLegajo = prueba.getEmpleado() != null ? prueba.getEmpleado().getEmpLegajo() : null;
        this.fechaHoraInicio = prueba.getPrFechaHoraInicio();
        this.fechaHoraFin = prueba.getPrFechaHoraFin();
        this.comentarios = prueba.getPrComentarios();
    }


}




