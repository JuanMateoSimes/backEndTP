package com.utn.frc.backend.pruebaservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "Pruebas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prueba {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prId;
    @ManyToOne
    @JoinColumn(name = "ID_VEHICULO")
    @JsonBackReference
    private Vehiculo vehiculo;
    @ManyToOne
    @JoinColumn(name = "ID_INTERESADO")
    @JsonBackReference
    private Interesado interesado;
    @ManyToOne
    @JoinColumn(name = "ID_EMPLEADO")
    @JsonBackReference
    private Empleado empleado;
    @Column(name = "FECHA_HORA_INICIO")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp prFechaHoraInicio = new Timestamp(System.currentTimeMillis());
    @Column(name = "FECHA_HORA_FIN", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "America/Argentina/Buenos_Aires")
    private Timestamp prFechaHoraFin;
    @Column(name = "COMENTARIOS", length = 500)
    private String prComentarios;


}
