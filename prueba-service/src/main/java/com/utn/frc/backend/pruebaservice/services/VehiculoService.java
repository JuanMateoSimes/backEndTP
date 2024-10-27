package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.models.Interesado;
import com.utn.frc.backend.pruebaservice.models.Posicion;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.repositories.InteresadoRepository;
import com.utn.frc.backend.pruebaservice.repositories.PosicionRepository;
import com.utn.frc.backend.pruebaservice.repositories.PruebaRepository;
import com.utn.frc.backend.pruebaservice.repositories.VehiculoRepository;
import com.utn.frc.backend.pruebaservice.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class VehiculoService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private VehiculoRepository vehiculoRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private PruebaRepository pruebaRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private InteresadoRepository interesadoRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private PosicionRepository posicionRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmpleadoRepository empleadoRepository;

    // a. Crear una nueva prueba
    public PruebaDTO crearPrueba(Integer vehiculoId, Integer interesadoId, Integer empleadoId) throws Throwable {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        Interesado interesado = interesadoRepository.findById(interesadoId).orElseThrow(() -> new RuntimeException("Interesado no encontrado"));
        Empleado empleado = empleadoRepository.findById(empleadoId).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Validaciones: licencia no vencida y no restringido
        if (interesado.isRestringido()) {
            throw new RuntimeException("El interesado está restringido para probar vehículos.");
        }
        if (interesado.getInteFechaVencimientoLicencia().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La licencia del interesado está vencida.");
        }

        // Validación: el vehículo no debe estar en prueba
        if (pruebaRepository.existsByVehiculoAndPrFechaHoraFinIsNull(vehiculo)) {
            throw new RuntimeException("El vehículo ya está siendo probado.");
        }

        // Crear y guardar la nueva prueba
        Prueba prueba = new Prueba();
        prueba.setVehiculo(vehiculo);
        prueba.setInteresado(interesado);
        prueba.setEmpleado(empleado);
        prueba.setPrFechaHoraInicio(LocalDateTime.now());

        prueba = pruebaRepository.save(prueba);
        return new PruebaDTO(prueba);
    }

    // b. Listar todas las pruebas en curso
    public List<PruebaDTO> listarPruebasEnCurso() {
        List<Prueba> pruebas = pruebaRepository.findByPrFechaHoraFinIsNull();
        return pruebas.stream().map(PruebaDTO::new).toList();
    }

    // c. Finalizar una prueba con un comentario
    public PruebaDTO finalizarPrueba(Integer pruebaId, String comentario) {
        Prueba prueba = pruebaRepository.findById(pruebaId).orElseThrow(() -> new RuntimeException("Prueba no encontrada"));
        prueba.setPrFechaHoraFin(LocalDateTime.now());
        prueba.setPrComentarios(comentario);
        prueba = pruebaRepository.save(prueba);
        return new PruebaDTO(prueba);
    }

    // d. Recibir la posición actual de un vehículo y verificar límites
    public void registrarPosicionVehiculo(Integer vehiculoId, double latitud, double longitud) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        Prueba pruebaEnCurso = pruebaRepository.findByVehiculoAndPrFechaHoraFinIsNull(vehiculo)
                .orElse(null);

        Posicion posicion = new Posicion();
        posicion.setVehiculo(vehiculo);
        posicion.setPosLatitud(latitud);
        posicion.setPosLongitud(longitud);
        posicion.setPosFechaHora(LocalDateTime.now());
        posicionRepository.save(posicion);

        // Verificar si el vehículo está en prueba y ha excedido límites
        if (pruebaEnCurso != null) {
            if (excedeLimitePermitido(latitud, longitud)) {
                // Aquí se podría generar una notificación o guardar el incidente
                registrarIncidente(pruebaEnCurso, "Excedió el límite permitido");
            }
            if (estaEnZonaPeligrosa(latitud, longitud)) {
                registrarIncidente(pruebaEnCurso, "Ingresó a una zona peligrosa");
            }
        }
    }

    private boolean excedeLimitePermitido(double latitud, double longitud) {
        // Lógica para calcular si excede el radio permitido
        // Esto sería una verificación usando distancia a un punto permitido
        return false;
    }

    private boolean estaEnZonaPeligrosa(double latitud, double longitud) {
        // Lógica para verificar si está en una zona peligrosa
        return false;
    }

    private void registrarIncidente(Prueba prueba, String descripcion) {
        // Aquí se registrarían los detalles del incidente en la base de datos
    }

    // f. Reportes
    // i. Incidentes (pruebas donde se excedieron los límites)
    public List<PruebaDTO> reporteIncidentes() {
        // Aquí se consulta y devuelve pruebas con incidentes
        return null;
    }

    // ii. Detalle de incidentes para un empleado
    public List<PruebaDTO> reporteIncidentesPorEmpleado(Long empleadoId) {
        // Aquí se consulta y devuelve incidentes para un empleado específico
        return null;
    }

    // iii. Cantidad de kilómetros de prueba recorridos por un vehículo en un período
    public double kilometrajeRecorrido(Integer vehiculoId, LocalDateTime inicio, LocalDateTime fin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        List<Posicion> posiciones = posicionRepository.findByVehiculoAndPosFechaHoraBetween(vehiculo, inicio, fin);

        double distanciaTotal = 0;
        for (int i = 1; i < posiciones.size(); i++) {
            distanciaTotal += calcularDistancia(posiciones.get(i - 1), posiciones.get(i));
        }
        return distanciaTotal;
    }

    private double calcularDistancia(Posicion pos1, Posicion pos2) {
        // Lógica para calcular la distancia entre dos puntos (pos1 y pos2)
        return 0;
    }

    // iv. Detalle de pruebas realizadas para un vehículo
    public List<PruebaDTO> pruebasPorVehiculo(Integer vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        List<Prueba> pruebas = pruebaRepository.findByVehiculo(vehiculo);
        return pruebas.stream().map(PruebaDTO::new).toList();
    }
}
