package com.utn.frc.backend.pruebaservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String prFechaHoraInicio;
    @Column(name = "FECHA_HORA_FIN", nullable = true)
    private String prFechaHoraFin;
    @Column(name = "COMENTARIOS")
    private String prComentarios;


}
