package com.utn.frc.backend.pruebaservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosicionDTO {
    private Integer vehiculoId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp fechaHora;
    private Double latitud;
    private Double longitud;
}
