package com.utn.frc.backend.pruebaservice.dtos;

import com.utn.frc.backend.pruebaservice.models.Prueba;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {
    private String patente;
    private Integer anio;
    private Integer modeloId;
    private List<Integer> pruebasId;
    private List<Integer> posicionesId;

}
