package com.utn.frc.backend.pruebaservice.services;

import com.utn.frc.backend.pruebaservice.client.NotificacionClient;
import com.utn.frc.backend.pruebaservice.dtos.NotificacionDTO;
import com.utn.frc.backend.pruebaservice.dtos.PruebaDTO;
import com.utn.frc.backend.pruebaservice.dtos.ReporteIncidenteDTO;
import com.utn.frc.backend.pruebaservice.models.Empleado;
import com.utn.frc.backend.pruebaservice.models.Prueba;
import com.utn.frc.backend.pruebaservice.repositories.EmpleadoRepository;
import com.utn.frc.backend.pruebaservice.repositories.PruebaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportesService {

    @Autowired
    private NotificacionClient notificacionClient;

    @Autowired
    private PruebaRepository pruebaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<ReporteIncidenteDTO> reporteIncidentes() {
        List<NotificacionDTO> notificaciones = notificacionClient.obtenerNotificaciones();

        // Filtra solo las notificaciones que corresponden a incidentes
        List<NotificacionDTO> incidentes = notificaciones.stream()
                .filter(notificacion -> "Excedió el límite permitido".equals(notificacion.getMensaje()))
                .toList();

        if (incidentes.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron notificaciones de incidentes.");
        }

        // Agrupa los incidentes por el ID de prueba
        Map<Integer, List<NotificacionDTO>> notificacionesPorPrueba = incidentes.stream()
                .collect(Collectors.groupingBy(NotificacionDTO::getPruebaId));

        // Mapea los incidentes agrupados a ReporteIncidenteDTO
        return notificacionesPorPrueba.entrySet().stream()
                .map(entry -> {
                    Integer pruebaId = entry.getKey();
                    List<NotificacionDTO> notificacionesAsociadas = entry.getValue();
                    Prueba prueba = pruebaRepository.findById(pruebaId)
                            .orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);
                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();
    }

    public List<ReporteIncidenteDTO> reporteIncidentesPorEmpleado(Integer empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

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
                            .orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));
                    PruebaDTO pruebaDTO = new PruebaDTO(prueba);
                    return new ReporteIncidenteDTO(pruebaDTO, notificacionesAsociadas);
                })
                .toList();
    }
}
