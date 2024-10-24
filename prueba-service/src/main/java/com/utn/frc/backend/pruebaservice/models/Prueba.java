package com.utn.frc.backend.pruebaservice.models;

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
    private Integer prId;
    @ManyToOne
    @JoinColumn(name = "ID_VEHICULO")
    private Vehiculo vehiculo;
    @ManyToOne
    @JoinColumn(name = "ID_INTERESADO")
    private Interesado interesado;
    @ManyToOne
    @JoinColumn(name = "ID_VEHICULO")
    private Empleado empleado;
    @Column(name = "FECHA_HORA_INICIO")
    private Timestamp prFechaHoraInicio = new Timestamp(System.currentTimeMillis());
    @Column(name = "FECHA_HORA_FIN")
    private Timestamp prFechaHoraFin = new Timestamp(System.currentTimeMillis());
    @Column(name = "COMENTARIOS")
    private String prComentarios;


}
