package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloDTO {
    private Integer marcaId;
    private String descripcion;
    private List<Integer> vehiculosId;
}
