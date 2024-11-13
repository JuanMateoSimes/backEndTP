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
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private InteresadoRepository interesadoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public PruebaDTO crearPrueba(PruebaDTO pruebaDTO) {
        // Obtener las entidades necesarias
        Vehiculo vehiculo = vehiculoRepository.findById(pruebaDTO.getVehiculoId())
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

        Interesado interesado = interesadoRepository.findById(pruebaDTO.getInteresadoId())
                .orElseThrow(() -> new IllegalStateException("Interesado no encontrado"));

        Empleado empleado = empleadoRepository.findById(pruebaDTO.getEmpleadoLegajo())
                .orElseThrow(() -> new IllegalStateException("Empleado no encontrado"));

        // Validar que el interesado no esté restringido
        if (interesado.getInteRestringido()) {
            throw new IllegalStateException("El interesado esta restringido y no puede realizar pruebas.");
        }

        // Validar que la licencia del interesado no esté vencida
        if (interesado.getInteFechaVencimientoLicencia().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalStateException("La licencia del interesado esta vencida.");
        }

        // Verificar que el vehículo no esté en uso en otra prueba
        if (pruebaRepository.countByVehiculoAndPrFechaHoraFinIsNullOrFuture(vehiculo, new Timestamp(System.currentTimeMillis())) > 0) {
            throw new IllegalStateException("El vehiculo ya esta siendo probado.");
        }

        // Crear nueva entidad Prueba a partir del DTO
        Prueba prueba = new Prueba();
        prueba.setVehiculo(vehiculo);
        prueba.setInteresado(interesado);
        prueba.setEmpleado(empleado);
        prueba.setPrFechaHoraInicio(pruebaDTO.getFechaHoraInicio());

        // Validar que la fecha fin sea correcta
        if (pruebaDTO.getFechaHoraFin() != null) {
            if (pruebaDTO.getFechaHoraFin().before(pruebaDTO.getFechaHoraInicio())) {
                throw new IllegalStateException("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }
            prueba.setPrFechaHoraFin(pruebaDTO.getFechaHoraFin());
        }

        prueba.setPrComentarios(pruebaDTO.getComentarios());

        // Guardar en la base de datos
        pruebaRepository.save(prueba);

        return new PruebaDTO(prueba);


    }



    // Obtener todas las pruebas en curso (enviando por parametro la fecha y hora actual)
    public List<Prueba> obtenerPruebasEnCurso() {
        return pruebaRepository.findByPrFechaHoraFinIsNullOrFuture(new Timestamp(System.currentTimeMillis()));
    }

    // Obtener todas las pruebas
    public List<Prueba> obtenerPruebas() {
        return pruebaRepository.findAll();
    }

    public PruebaDTO finalizarPrueba(Integer id, PruebaDTO pruebaDTO) {
        Optional<Prueba> pruebaOptional = pruebaRepository.findById(id);
        if (!pruebaOptional.isPresent()) {
            throw new IllegalStateException("La prueba no existe.");
        }

        Timestamp fechaHoraActual = new Timestamp(System.currentTimeMillis());
        Prueba prueba = pruebaOptional.get();

        // Verificar si la prueba ya ha finalizado
        if (prueba.getPrFechaHoraFin() != null && prueba.getPrFechaHoraFin().before(fechaHoraActual)) {
            throw new IllegalStateException("La prueba ya ha sido finalizada.");
        }

        // Verificar que la fecha de fin proporcionada no sea anterior a la fecha de inicio
        if (pruebaDTO.getFechaHoraFin().before(prueba.getPrFechaHoraInicio())) {
            throw new IllegalStateException("La fecha de fin no puede ser menor a la fecha de inicio de la prueba....");
        }

        // Verificar que la fecha de fin proporcionada no sea posterior a la fecha actual
        if (pruebaDTO.getFechaHoraFin().after(fechaHoraActual)) {
            throw new IllegalStateException("La fecha de fin no puede ser mayor a la fecha actual. (seguiria en curso...)");
        }

        // Actualizar la fecha de fin y los comentarios
        prueba.setPrFechaHoraFin(pruebaDTO.getFechaHoraFin());
        prueba.setPrComentarios(pruebaDTO.getComentarios());

        // Guardar los cambios
        pruebaRepository.save(prueba);

        return new PruebaDTO(prueba);

    }


}