package com.utn.frc.backend.notificacionservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer pruebaId;
    private Integer vehiculoId;
    private Integer empleadoTelefono;
    private String mensaje;
    private Timestamp fechaHora;

    // Getters y Setters

}
