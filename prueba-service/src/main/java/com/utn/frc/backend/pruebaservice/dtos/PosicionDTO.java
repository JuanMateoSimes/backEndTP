package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosicionDTO {
    private VehiculoDTO vehiculo;
    private LocalDateTime fechaHora;
    private Double latitud;
    private Double longitud;
}
