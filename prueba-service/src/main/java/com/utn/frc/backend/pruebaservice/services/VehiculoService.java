package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.client.APIClient;
import com.utn.frc.backend.pruebaservice.client.NotificacionClient;
import com.utn.frc.backend.pruebaservice.dtos.*;
import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.models.Posicion;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.models.Vehiculo;
import com.utn.frc.backend.pruebaservice.repositories.EmpleadoRepository;
import com.utn.frc.backend.pruebaservice.repositories.PosicionRepository;
import com.utn.frc.backend.pruebaservice.repositories.PruebaRepository;
import com.utn.frc.backend.pruebaservice.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehiculoService {
    @Autowired
    private APIClient apiClient;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PruebaRepository pruebaRepository;

    @Autowired
    private PosicionRepository posicionRepository;

    @Autowired
    private NotificacionClient notificacionClient;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private ConfiguracionDTO configuracion;

    public List<Vehiculo> obtenerTodosLosVehiculos() {
        return (List<Vehiculo>) vehiculoRepository.findAll();
    }

    public PosicionDTO registrarPosicionVehiculo(PosicionDTO posicionDTO) {
        if (configuracion == null) {
            configuracion = apiClient.obtenerConfiguracion();
        }

        Timestamp fechaHoraActual = new Timestamp(System.currentTimeMillis());

        Vehiculo vehiculo = vehiculoRepository.findById(posicionDTO.getVehiculoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        Prueba pruebaEnCurso = pruebaRepository.findByVehiculoAndPrFechaHoraFinIsNullOrFuture(vehiculo, fechaHoraActual).orElse(null);

        if (pruebaEnCurso == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontró una prueba en curso para el vehículo con ID: " + posicionDTO.getVehiculoId());
        }

        if (posicionDTO.getFechaHora().before(pruebaEnCurso.getPrFechaHoraInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la posición no puede ser anterior a la fecha de inicio de la prueba.");
        }

        if (posicionRepository.existsByVehiculoAndPosFechaHora(vehiculo, posicionDTO.getFechaHora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una posición registrada con la misma fecha y hora para este vehículo.");
        }

        Posicion posicion = new Posicion();
        posicion.setVehiculo(vehiculo);
        posicion.setPosLatitud(posicionDTO.getLatitud());
        posicion.setPosLongitud(posicionDTO.getLongitud());
        posicion.setPosFechaHora(posicionDTO.getFechaHora());

        posicionRepository.save(posicion);

        boolean limiteExcedido = excedeLimitePermitido(posicionDTO.getLatitud(), posicionDTO.getLongitud());
        boolean enZonaPeligrosa = estaEnZonaPeligrosa(posicionDTO.getLatitud(), posicionDTO.getLongitud());

        if (limiteExcedido || enZonaPeligrosa) {
            NotificacionDTO notificacion = new NotificacionDTO();
            notificacion.setPruebaId(pruebaEnCurso.getPrId());
            notificacion.setVehiculoId(posicionDTO.getVehiculoId());
            notificacion.setEmpleadoTelefono(pruebaEnCurso.getEmpleado().getEmpTelefono());
            notificacion.setMensaje(limiteExcedido ? "Excedió el límite permitido" : "Ingresó a una zona peligrosa");
            notificacion.setFechaHora(String.valueOf(posicionDTO.getFechaHora()));

            try {
                notificacionClient.crearNotificacion(notificacion);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar notificación: " + e.getMessage());
            }
        }

        return new PosicionDTO(posicion);
    }

    private boolean excedeLimitePermitido(double latitud, double longitud) {
        double distancia = calcularDistancia(latitud, longitud,
                configuracion.getCoordenadasAgencia().getLat(),
                configuracion.getCoordenadasAgencia().getLon());
        return distancia > configuracion.getRadioAdmitidoKm();
    }

    private boolean estaEnZonaPeligrosa(double latitud, double longitud) {
        for (ZonaRestringidaDTO zona : configuracion.getZonasRestringidas()) {
            if (latitud <= zona.getNoroeste().getLat() && latitud >= zona.getSureste().getLat()
                    && longitud >= zona.getNoroeste().getLon() && longitud <= zona.getSureste().getLon()) {
                return true;
            }
        }
        return false;
    }

    public List<ReporteIncidenteDTO> reporteIncidentes() {
        List<NotificacionDTO> notificaciones = notificacionClient.obtenerNotificaciones();

        if (notificaciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron notificaciones de incidentes.");
        }

        Map<Integer, List<NotificacionDTO>> notificacionesPorPrueba = notificaciones.stream()
                .filter(notificacion -> "Excedió el límite permitido".equals(notificacion.getMensaje()))
                .collect(Collectors.groupingBy(NotificacionDTO::getPruebaId));

        return notificacionesPorPrueba.entrySet().stream()
                .map(entry -> {
                    Integer pruebaId = entry.getKey();
                    List<NotificacionDTO> notificacionesAsociadas = entry.getValue();
                    Prueba prueba = pruebaRepository.findById(pruebaId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);
                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();
    }

    public List<ReporteIncidenteDTO> reporteIncidentesPorEmpleado(Integer empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        List<NotificacionDTO> notificaciones = notificacionClient.obtenerNotificaciones();

        Map<Integer, List<NotificacionDTO>> notificacionesPorPrueba = notificaciones.stream()
                .filter(notificacion -> "Excedió el límite permitido".equals(notificacion.getMensaje()) &&
                        empleado.getEmpTelefono().equals(notificacion.getEmpleadoTelefono()))
                .collect(Collectors.groupingBy(NotificacionDTO::getPruebaId));

        return notificacionesPorPrueba.entrySet().stream()
                .map(entry -> {
                    Integer pruebaId = entry.getKey();
                    List<NotificacionDTO> notificacionesAsociadas = entry.getValue();
                    Prueba prueba = pruebaRepository.findById(pruebaId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);
                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();
    }

    public double kilometrajeRecorrido(Integer vehiculoId, Timestamp inicio, Timestamp fin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        List<Posicion> posiciones = posicionRepository.findByVehiculoAndPosFechaHoraBetween(vehiculo, inicio, fin);

        if (posiciones.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron posiciones para el vehículo en el rango de fechas especificado.");
        }

        double distanciaTotal = 0;
        for (int i = 1; i < posiciones.size(); i++) {
            double lat1 = posiciones.get(i - 1).getPosLatitud();
            double lon1 = posiciones.get(i - 1).getPosLongitud();
            double lat2 = posiciones.get(i).getPosLatitud();
            double lon2 = posiciones.get(i).getPosLongitud();

            distanciaTotal += calcularDistancia(lat1, lon1, lat2, lon2);
        }
        return distanciaTotal;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }

    public List<PruebaDTO> pruebasPorVehiculo(Integer vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        List<Prueba> pruebas = pruebaRepository.findByVehiculo(vehiculo);

        if (pruebas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron pruebas para el vehículo con ID: " + vehiculoId);
        }

        return pruebas.stream().map(PruebaDTO::new).toList();
    }
}
