package com.utn.frc.backend.pruebaservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRestringida {
    private Coordenadas noroeste;
    private Coordenadas sureste;
}
