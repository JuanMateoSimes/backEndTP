package com.utn.frc.backend.notificacionservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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
    @Column(name = "fecha_hora")
    private String fechaHora;
}
