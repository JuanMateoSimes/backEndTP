package com.utn.frc.backend.pruebaservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Interesados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interesado {
    @Id
    @Column(name = "ID")
    private Integer inteId;
    @Column(name = "TIPO_DOCUMENTO", length = 3)
    private String inteTipoDocumento = "DNI";
    @Column(name = "DOCUMENTO", length = 10)
    private String inteDocumento;
    @Column(name = "NOMBRE", length = 50)
    private String inteNombre;
    @Column(name = "APELLIDO", length = 50)
    private String inteApellido;
    @Column(name = "RESTRINGIDO")
    private Boolean inteRestringido = false;
    @Column(name = "NRO_LICENCIA")
    private Integer inteNroLicencia;
    @Column(name = "FECHA_VENCIMIENTO_LICENCIA")
    private LocalDateTime inteFechaVencimientoLicencia;
    @OneToMany(mappedBy = "interesado")
    private List<Prueba> pruebas;

    public boolean isRestringido() {
        if (this.inteRestringido = false) {
            return true;
        }
        return false;
    };
}
