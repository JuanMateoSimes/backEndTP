package com.utn.frc.backend.notificacionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Integer pruebaId;
    private Integer vehiculoId;
    private Integer empleadoTelefono;
    private String mensaje;
    private Timestamp fechaHora;
}
