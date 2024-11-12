package com.utn.frc.backend.pruebaservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "Posiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posicion {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer posId;
    @ManyToOne
    @JoinColumn(name = "ID_VEHICULO")
    @JsonBackReference
    private Vehiculo vehiculo;
    @Column(name = "FECHA_HORA")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp posFechaHora = new Timestamp(System.currentTimeMillis());
    @Column(name = "LATITUD")
    private Double posLatitud;
    @Column(name = "LONGITUD")
    private Double posLongitud;
}
