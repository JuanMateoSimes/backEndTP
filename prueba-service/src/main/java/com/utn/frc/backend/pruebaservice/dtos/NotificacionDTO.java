package com.utn.frc.backend.pruebaservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
