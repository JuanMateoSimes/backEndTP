package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.models.Interesado;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import com.utn.frc.backend.pruebaservice.repositories.EmpleadoRepository;
import com.utn.frc.backend.pruebaservice.repositories.InteresadoRepository;
import com.utn.frc.backend.pruebaservice.repositories.PruebaRepository;
import com.utn.frc.backend.pruebaservice.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
public class PruebaService {
    @Autowired
    private PruebaRepository pruebaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository; // Añadir el repositorio correspondiente
    @Autowired
    private InteresadoRepository interesadoRepository; // Añadir el repositorio correspondiente
    @Autowired
    private EmpleadoRepository empleadoRepository; // Añadir el repositorio correspondiente

    // Crear una nueva prueba
    public void crearPrueba(PruebaDTO pruebaDTO) {

        // Obtener las entidades necesarias
        Vehiculo vehiculo = vehiculoRepository.findById(pruebaDTO.getVehiculoId())
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));
        Interesado interesado = interesadoRepository.findById(pruebaDTO.getInteresadoId())
                .orElseThrow(() -> new IllegalStateException("Interesado no encontrado"));
        Empleado empleado = empleadoRepository.findById(pruebaDTO.getEmpleadoLegajo())
                .orElseThrow(() -> new IllegalStateException("Empleado no encontrado"));

        // Validar que el interesado no esté restringido
        if (interesado.getInteRestringido()) {
            throw new IllegalStateException("El interesado está restringido y no puede realizar pruebas.");
        }

        // Validar que la licencia del interesado no esté vencida
        if (interesado.getInteFechaVencimientoLicencia().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalStateException("La licencia del interesado está vencida.");
        }

        // Verificar que el vehículo no esté en uso en otra prueba
        if (pruebaRepository.countByVehiculoAndPrFechaHoraFinIsNull(vehiculo) > 0) {
            throw new IllegalStateException("El vehículo ya está siendo probado.");
        }

        // Crear nueva entidad Prueba a partir del DTO
        Prueba prueba = new Prueba();
        prueba.setVehiculo(vehiculo);
        prueba.setInteresado(interesado);
        prueba.setEmpleado(empleado);
        prueba.setPrFechaHoraInicio(pruebaDTO.getFechaHoraInicio());
        if (pruebaDTO.getFechaHoraFin() != null) {
            prueba.setPrFechaHoraFin(pruebaDTO.getFechaHoraFin());
        }
        prueba.setPrComentarios(pruebaDTO.getComentarios());

        // Guardar en la base de datos
        pruebaRepository.save(prueba);
    }


    // Obtener todas las pruebas en curso
    public List<Prueba> obtenerPruebasEnCurso() {
        return pruebaRepository.findByPrFechaHoraFinIsNull();
    }

    // Obtener todas las pruebas

    public List<Prueba> obtenerPruebas() {
        return pruebaRepository.findAll();
    }

    // Finalizar una prueba existente
    public void finalizarPrueba(Integer id, PruebaDTO pruebaDTO) {
        Optional<Prueba> pruebaOptional = pruebaRepository.findById(id);
        if (!pruebaOptional.isPresent()) {
            throw new IllegalStateException("La prueba no existe.");
        }
        Prueba prueba = pruebaOptional.get();
        prueba.setPrFechaHoraFin(pruebaDTO.getFechaHoraFin());
        prueba.setPrComentarios(pruebaDTO.getComentarios());

        // Guardar los cambios
        pruebaRepository.save(prueba);
    }
}