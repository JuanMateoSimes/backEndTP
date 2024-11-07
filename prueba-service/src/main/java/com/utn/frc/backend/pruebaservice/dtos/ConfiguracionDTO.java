package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionDTO {
    private CoordenadaDTO coordenadasAgencia;
    private double radioAdmitidoKm;
    private List<ZonaRestringidaDTO> zonasRestringidas;
}
