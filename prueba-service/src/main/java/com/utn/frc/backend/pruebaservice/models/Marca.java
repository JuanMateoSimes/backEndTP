package com.utn.bda.agenciavehiculos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Marcas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marca {
    @Id
    @Column(name = "ID")
    private Integer marcId;
    @Column(name = "NOMBRE", length = 30)
    private String marcNombre;
    @OneToMany(mappedBy = "marca")
    private List<Modelo> modelos;
}
