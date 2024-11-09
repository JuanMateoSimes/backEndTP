package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.api.ZonaRestringida;
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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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

    public void registrarPosicionVehiculo(PosicionDTO posicionDTO) {
        // Llama al cliente para obtener la configuración
        if (configuracion == null) {
            configuracion = apiClient.obtenerConfiguracion();
        }

        Vehiculo vehiculo = vehiculoRepository.findById(posicionDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        Prueba pruebaEnCurso = pruebaRepository.findByVehiculoAndPrFechaHoraFinIsNull(vehiculo).orElse(null);

        if (pruebaEnCurso != null) {
            // Crear y guardar la nueva posición en la base de datos
            Posicion posicion = new Posicion();
            posicion.setVehiculo(vehiculo);
            posicion.setPosLatitud(posicionDTO.getLatitud());
            posicion.setPosLongitud(posicionDTO.getLongitud());
            posicion.setPosFechaHora(posicionDTO.getFechaHora());

            posicionRepository.save(posicion);

            // Verificar si es necesario enviar una notificación
            boolean limiteExcedido = excedeLimitePermitido(posicionDTO.getLatitud(), posicionDTO.getLongitud());
            boolean enZonaPeligrosa = estaEnZonaPeligrosa(posicionDTO.getLatitud(), posicionDTO.getLongitud());

            if (limiteExcedido || enZonaPeligrosa) {
                NotificacionDTO notificacion = new NotificacionDTO();
                notificacion.setPruebaId(pruebaEnCurso.getPrId());
                notificacion.setVehiculoId(posicionDTO.getVehiculoId());
                System.out.println("Teléfono empleado antes de set: " + pruebaEnCurso.getEmpleado().getEmpTelefono());
                notificacion.setEmpleadoTelefono(pruebaEnCurso.getEmpleado().getEmpTelefono());
                System.out.println("Teléfono empleado en DTO: " + notificacion.getEmpleadoTelefono());
                notificacion.setMensaje(limiteExcedido ? "Excedió el límite permitido" : "Ingresó a una zona peligrosa");
                notificacion.setFechaHora(String.valueOf(posicionDTO.getFechaHora()));

                System.out.println(notificacion);

                try {
                    notificacionClient.crearNotificacion(notificacion);
                    System.out.println("Notificación creada");
                } catch (Exception e) {
                    System.err.println("Error al enviar notificación: " + e.getMessage());
                }
            }
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

    // Reportes
    public List<PruebaDTO> reporteIncidentes() {
        // Consulta y devuelve pruebas con incidentes
        return null;
    }

    public List<PruebaDTO> reporteIncidentesPorEmpleado(Long empleadoId) {
        // Consulta y devuelve incidentes para un empleado específico
        return null;
    }

    public double kilometrajeRecorrido(Integer vehiculoId, Timestamp inicio, Timestamp fin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        List<Posicion> posiciones = posicionRepository.findByVehiculoAndPosFechaHoraBetween(vehiculo, inicio, fin);

        double distanciaTotal = 0;
        for (int i = 1; i < posiciones.size(); i++) {
            // Extraemos latitud y longitud de las posiciones consecutivas
            double lat1 = posiciones.get(i - 1).getPosLatitud();
            double lon1 = posiciones.get(i - 1).getPosLongitud();
            double lat2 = posiciones.get(i).getPosLatitud();
            double lon2 = posiciones.get(i).getPosLongitud();

            // Calculamos la distancia entre las posiciones consecutivas
            distanciaTotal += calcularDistancia(lat1, lon1, lat2, lon2);
        }
        return distanciaTotal;
    }


    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Calculando la distancia euclidiana
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }



    public List<PruebaDTO> pruebasPorVehiculo(Integer vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        List<Prueba> pruebas = pruebaRepository.findByVehiculo(vehiculo);
        return pruebas.stream().map(PruebaDTO::new).toList();
    }
}
