package com.utn.frc.backend.pruebaservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Vehiculos", indexes = {
        @Index(name = "Vehiculo_PATENTE_IDX", columnList = "PATENTE", unique = true),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {
    @Id
    @Column(name = "ID")
    private Integer vehId;
    @Column(name = "PATENTE")
    private String vehPatente;
    @ManyToOne
    @JoinColumn(name = "ID_MODELO")
    @JsonBackReference
    private Modelo modelo;
    @OneToMany(mappedBy = "vehiculo")
    @JsonManagedReference
    private List<Prueba> pruebas;
    @OneToMany(mappedBy = "vehiculo")
    @JsonManagedReference
    private List<Posicion> posiciones;
}
