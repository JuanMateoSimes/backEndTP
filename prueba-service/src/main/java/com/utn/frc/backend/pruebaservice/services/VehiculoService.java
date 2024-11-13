package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.client.APIClient;
import com.utn.frc.backend.pruebaservice.client.NotificacionClient;
import com.utn.frc.backend.pruebaservice.dtos.*;
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
                .orElseThrow(() -> new IllegalStateException("Vehículo no encontrado"));

        Prueba pruebaEnCurso = pruebaRepository.findByVehiculoAndPrFechaHoraFinIsNullOrFuture(vehiculo, fechaHoraActual).orElse(null);

        if (pruebaEnCurso == null) {
            throw new IllegalStateException("No se encontró una prueba en curso para el vehículo con ID: " + posicionDTO.getVehiculoId());
        }

        if (posicionDTO.getFechaHora().before(pruebaEnCurso.getPrFechaHoraInicio())) {
            throw new IllegalArgumentException("La fecha de la posición no puede ser anterior a la fecha de inicio de la prueba.");
        }

        if (posicionRepository.existsByVehiculoAndPosFechaHora(vehiculo, posicionDTO.getFechaHora())) {
            throw new IllegalArgumentException("Ya existe una posición registrada con la misma fecha y hora para este vehículo.");
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
                throw new IllegalStateException("Error al enviar notificación: " + e.getMessage());
            }
        }

        return new PosicionDTO(posicion);
    }

    private boolean excedeLimitePermitido(double latitud, double longitud) {
        if (configuracion.getCoordenadasAgencia() == null || configuracion.getRadioAdmitidoKm() == 0) {
            throw new IllegalStateException("Configuración de la agencia no está disponible o el radio admitido es inválido.");
        }

        double distancia = calcularDistancia(latitud, longitud,
                configuracion.getCoordenadasAgencia().getLat(),
                configuracion.getCoordenadasAgencia().getLon());
        return distancia > configuracion.getRadioAdmitidoKm();
    }

    private boolean estaEnZonaPeligrosa(double latitud, double longitud) {
        if (configuracion.getZonasRestringidas() == null || configuracion.getZonasRestringidas().isEmpty()) {
            throw new IllegalStateException("La configuración de zonas restringidas no está disponible.");
        }

        for (ZonaRestringidaDTO zona : configuracion.getZonasRestringidas()) {
            if (latitud <= zona.getNoroeste().getLat() && latitud >= zona.getSureste().getLat()
                    && longitud >= zona.getNoroeste().getLon() && longitud <= zona.getSureste().getLon()) {
                return true;
            }
        }
        return false;
    }

    public double kilometrajeRecorrido(Integer vehiculoId, Timestamp inicio, Timestamp fin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        List<Posicion> posiciones = posicionRepository.findByVehiculoAndPosFechaHoraBetween(vehiculo, inicio, fin);

        if (posiciones.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron posiciones para el vehículo en el rango de fechas especificado.");
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
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) {
            throw new IllegalArgumentException("Coordenadas no válidas para el cálculo de la distancia.");
        }
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }

    public List<PruebaDTO> pruebasPorVehiculo(Integer vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        List<Prueba> pruebas = pruebaRepository.findByVehiculo(vehiculo);

        if (pruebas.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron pruebas para el vehículo con ID: " + vehiculoId);
        }

        return pruebas.stream().map(PruebaDTO::new).toList();
    }

}
