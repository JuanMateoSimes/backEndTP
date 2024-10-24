package com.utn.bda.agenciavehiculos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Modelos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Modelo {
    @Id
    @Column(name = "ID")
    private Integer modId;
    @ManyToOne
    @JoinColumn(name = "ID_MARCA")
    private Marca marca;
    @Column(name = "DESCRIPCION")
    private String modDescripcion;
    @OneToMany(mappedBy = "modelo")
    private List<Vehiculo> vehiculos;
}
