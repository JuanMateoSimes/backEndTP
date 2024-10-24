package com.utn.bda.agenciavehiculos.models;

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
    private Modelo modelo;
    @OneToMany(mappedBy = "vehiculo")
    private List<Prueba> pruebas;
    @OneToMany(mappedBy = "vehiculo")
    private List<Posicion> posiciones;
}
