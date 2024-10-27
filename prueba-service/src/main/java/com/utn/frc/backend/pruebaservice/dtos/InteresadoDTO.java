package com.utn.frc.backend.pruebaservice.dtos;

import com.utn.frc.backend.pruebaservice.models.Prueba;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteresadoDTO {
    private String tipoDocumento;
    private String documento;
    private String nombre;
    private String apellido;
    private Boolean restringido;
    private Integer nroLicencia;
    private Timestamp fechaVencimientoLicencia;
    private List<Integer> pruebasId;
}
