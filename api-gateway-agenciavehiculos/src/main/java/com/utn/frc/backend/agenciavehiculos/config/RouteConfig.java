package com.utn.frc.backend.agenciavehiculos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;




@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        @Value("${gateway.url.prueba-service}") String uriPruebaService,
                                        @Value("${gateway.url.notificacion-service}") String uriNotificacionService) {
        return builder.routes()
                .route(p -> p.path("/pruebas/**", "/vehiculos/**", "/reportes/**").uri(uriPruebaService))
                .route(p -> p.path("/notificaciones/**").uri(uriNotificacionService))
                .build();
    }

}
