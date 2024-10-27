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
    private Integer vehiculoId;
    private Timestamp fechaHora;
    private Double latitud;
    private Double longitud;
}
