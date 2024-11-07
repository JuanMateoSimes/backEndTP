package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRestringidaDTO {
    private CoordenadaDTO noroeste;
    private CoordenadaDTO sureste;
}
