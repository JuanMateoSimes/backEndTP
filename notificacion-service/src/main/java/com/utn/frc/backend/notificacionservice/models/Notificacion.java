package com.utn.frc.backend.notificacionservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private Long empleadoTelefono;
    private String mensaje;
    private String fechaHora;

}
