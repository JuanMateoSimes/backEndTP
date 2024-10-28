package com.utn.frc.backend.pruebaservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    @Id
    @Column(name = "LEGAJO")
    private Integer empLegajo;
    @Column(name = "NOMBRE", length = 30)
    private String empNombre;
    @Column(name = "APELLIDO", length = 50)
    private String empApellido;
    @Column(name = "TELEFONO_CONTACTO")
    private Integer empTelefono;
    @OneToMany(mappedBy = "empleado")
    @JsonManagedReference
    private List<Prueba> pruebas;
}
