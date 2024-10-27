package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {
    private String nombre;
    private String apellido;
    private Integer telefono;
    private List<Integer> pruebasId;
}
