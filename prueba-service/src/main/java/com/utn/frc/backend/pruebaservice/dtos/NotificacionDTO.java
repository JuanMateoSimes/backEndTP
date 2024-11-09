package com.utn.frc.backend.pruebaservice.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long empleadoTelefono;
    private String mensaje;
    private String fechaHora;
}
