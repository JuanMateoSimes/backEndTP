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
import org.springframework.stereotype.Service;

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
        // Llama al cliente para obtener la configuración
        if (configuracion == null) {
            configuracion = apiClient.obtenerConfiguracion();
        }

        // Obtener la fecha actual
        Timestamp fechaHoraActual = new Timestamp(System.currentTimeMillis());

        // Obtener las entidades necesarias
        Vehiculo vehiculo = vehiculoRepository.findById(posicionDTO.getVehiculoId())
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

        Prueba pruebaEnCurso = pruebaRepository.findByVehiculoAndPrFechaHoraFinIsNullOrFuture(vehiculo, fechaHoraActual).orElse(null);

        // Chequear que la prueba esté en curso
        if (pruebaEnCurso != null) {

            // Validar que la fecha de la posición no sea anterior a la fecha de inicio de la prueba
            if (posicionDTO.getFechaHora().before(pruebaEnCurso.getPrFechaHoraInicio())) {
                throw new IllegalArgumentException("La fecha de la posición no puede ser anterior a la fecha de inicio de la prueba.");
            }

            // Verificar si ya existe una posición registrada en la misma fecha y hora
            if (posicionRepository.existsByVehiculoAndPosFechaHora(vehiculo, posicionDTO.getFechaHora())) {
                throw new IllegalArgumentException("Ya existe una posición registrada con la misma fecha y hora para este vehículo.");
            }

            // Crear y guardar la nueva posición en la base de datos
            Posicion posicion = new Posicion();
            posicion.setVehiculo(vehiculo);
            posicion.setPosLatitud(posicionDTO.getLatitud());
            posicion.setPosLongitud(posicionDTO.getLongitud());
            posicion.setPosFechaHora(posicionDTO.getFechaHora());

            posicionRepository.save(posicion);

            // Verificar si es necesario enviar una notificación (por límite excedido o zona peligrosa)
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
                    throw new IllegalStateException("Error al enviar notificación: " + e.getMessage());
                }
            }

            // Retornar el DTO de la posición registrada
            return new PosicionDTO(posicion);
        } else {
            // Si no se encontró una prueba en curso
            throw new IllegalStateException("No se encontró una prueba en curso para el vehículo con ID: " + posicionDTO.getVehiculoId());
        }
    }

    private boolean excedeLimitePermitido(double latitud, double longitud) {
        // Llamada a calcularDistancia pasando las coordenadas de la agencia
        double distancia = calcularDistancia(latitud, longitud,
                configuracion.getCoordenadasAgencia().getLat(),
                configuracion.getCoordenadasAgencia().getLon());

        // Comparación con el radio admitido
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

    //
    public List<ReporteIncidenteDTO> reporteIncidentes() {
        List<NotificacionDTO> notificaciones = notificacionClient.obtenerNotificaciones();

        if (notificaciones.isEmpty()) {
            throw new IllegalStateException("No se encontraron notificaciones de incidentes.");
        }

        // Agrupamos las notificaciones por pruebaId
        Map<Integer, List<NotificacionDTO>> notificacionesPorPrueba = notificaciones.stream()
                .filter(notificacion -> "Excedió el límite permitido".equals(notificacion.getMensaje()))
                .collect(Collectors.groupingBy(NotificacionDTO::getPruebaId));

        List<ReporteIncidenteDTO> reporteIncidentes = notificacionesPorPrueba.entrySet().stream()
                .map(entry -> {
                    Integer pruebaId = entry.getKey();
                    List<NotificacionDTO> notificacionesAsociadas = entry.getValue();
                    Prueba prueba = pruebaRepository.findById(pruebaId)
                            .orElseThrow(() -> new IllegalStateException("Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);
                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();

        return reporteIncidentes;
    }

    public List<ReporteIncidenteDTO> reporteIncidentesPorEmpleado(Integer empleadoId) {
        // Verifica si el empleado existe
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Obtiene todas las notificaciones
        List<NotificacionDTO> notificaciones = notificacionClient.obtenerNotificaciones();

        // Filtra notificaciones para el empleado específico y con el mensaje "Excedió el límite permitido"
        Map<Integer, List<NotificacionDTO>> notificacionesPorPrueba = notificaciones.stream()
                .filter(notificacion -> "Excedió el límite permitido".equals(notificacion.getMensaje()) &&
                        empleado.getEmpTelefono().equals(notificacion.getEmpleadoTelefono()))
                .collect(Collectors.groupingBy(NotificacionDTO::getPruebaId));

        // Crea lista de ReporteIncidenteDTO con la información de prueba y notificaciones filtradas
        List<ReporteIncidenteDTO> reporteIncidentes = notificacionesPorPrueba.entrySet().stream()
                .map(entry -> {
                    Integer pruebaId = entry.getKey();
                    List<NotificacionDTO> notificacionesAsociadas = entry.getValue();

                    // Busca la prueba por ID y convierte a PruebaDTO
                    Prueba prueba = pruebaRepository.findById(pruebaId)
                            .orElseThrow(() -> new RuntimeException("Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);

                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();

        return reporteIncidentes;
    }


    public double kilometrajeRecorrido(Integer vehiculoId, Timestamp inicio, Timestamp fin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

        List<Posicion> posiciones = posicionRepository.findByVehiculoAndPosFechaHoraBetween(vehiculo, inicio, fin);

        if (posiciones.isEmpty()) {
            throw new IllegalStateException("No se encontraron posiciones para el vehículo en el rango de fechas especificado.");
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
        // Calculando la distancia euclidiana
        return Math.sqrt(Math.pow(lat2 - lat1, 8) + Math.pow(lon2 - lon1, 8));
    }



    public List<PruebaDTO> pruebasPorVehiculo(Integer vehiculoId) {
        // Buscar el vehículo en el repositorio, si no se encuentra, lanzar una excepción
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

        // Obtener las pruebas asociadas al vehículo
        List<Prueba> pruebas = pruebaRepository.findByVehiculo(vehiculo);

        // Si no se encuentran pruebas, podemos lanzar una excepción también si lo consideras necesario
        if (pruebas.isEmpty()) {
            throw new IllegalStateException("No se encontraron pruebas para el vehículo con ID: " + vehiculoId);
        }

        // Convertir las pruebas a PruebaDTO y devolver la lista
        return pruebas.stream().map(PruebaDTO::new).toList();
    }

}
