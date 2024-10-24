package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.repositories.PruebaRepository;

import java.util.List;
import java.util.Optional;

public class PruebaService {
    private PruebaRepository pruebaRepository;

    // Crear una nueva prueba
    public void crearPrueba(PruebaDTO pruebaDTO) {
        // Verificar que el vehículo no esté en uso en otra prueba
        if (pruebaRepository.countByIdVehiculoAndFechaHoraFinIsNull(pruebaDTO.getVehiculo()) > 0) {
            throw new IllegalStateException("El vehículo ya está siendo probado.");
        }
        // Validaciones adicionales (licencia vencida, etc.) podrían ir aquí

        // Crear nueva entidad Prueba a partir del DTO
        Prueba prueba = new Prueba();
        prueba.setVehiculo(pruebaDTO.getVehiculo());
        prueba.setInteresado(pruebaDTO.getInteresado());
        prueba.setEmpleado(pruebaDTO.getEmpleado());
        prueba.setPrFechaHoraInicio(pruebaDTO.getFechaHoraInicio());

        // Guardar en la base de datos
        pruebaRepository.save(prueba);
    }

    // Obtener todas las pruebas en curso
    public List<Prueba> obtenerPruebasEnCurso() {
        return pruebaRepository.findByFechaHoraFinIsNull();
    }

    // Finalizar una prueba existente
    public void finalizarPrueba(Long id, PruebaDTO pruebaDTO) {
        Optional<Prueba> pruebaOptional = pruebaRepository.findById(id);
        if (!pruebaOptional.isPresent()) {
            throw new IllegalStateException("La prueba no existe.");
        }

        Prueba prueba = pruebaOptional.get();
        prueba.setPrFechaHoraInicio(pruebaDTO.getFechaHoraFin());
        prueba.setPrComentarios(pruebaDTO.getComentarios());

        // Guardar los cambios
        pruebaRepository.save(prueba);
    }
}
