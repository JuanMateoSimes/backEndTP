package com.utn.frc.backend.pruebaservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Posiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posicion {
    @Id
    @Column(name = "ID")
    private Integer posId;
    @ManyToOne
    @JoinColumn(name = "ID_VEHICULO")
    private Vehiculo vehiculo;
    @Column(name = "FECHA_HORA")
    private LocalDateTime posFechaHora = LocalDateTime.now();
    @Column(name = "LATITUD")
    private Double posLatitud;
    @Column(name = "LONGITUD")
    private Double posLongitud;
}
