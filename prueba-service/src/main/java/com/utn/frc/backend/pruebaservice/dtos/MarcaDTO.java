package com.utn.frc.backend.pruebaservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcaDTO {
    private String nombre;
    private List<Integer> modelosId;
}
