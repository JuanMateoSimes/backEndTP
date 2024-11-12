package com.utn.frc.backend.pruebaservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PruebaDTO {
    private Integer vehiculoId;
    private Integer interesadoId;
    private Integer empleadoLegajo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp fechaHoraInicio;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp fechaHoraFin;
    private String comentarios;

    public PruebaDTO(Prueba prueba) {
        this.vehiculoId = prueba.getVehiculo() != null ? prueba.getVehiculo().getVehId() : null;
        this.interesadoId = prueba.getInteresado() != null ? prueba.getInteresado().getInteId() : null;
        this.empleadoLegajo = prueba.getEmpleado() != null ? prueba.getEmpleado().getEmpLegajo() : null;
        this.fechaHoraInicio = prueba.getPrFechaHoraInicio();
        this.fechaHoraFin = prueba.getPrFechaHoraFin();
        this.comentarios = prueba.getPrComentarios();
    }


}




