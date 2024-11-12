package com.utn.frc.backend.pruebaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                // Esta ruta puede ser accedida por cualquiera, sin autorización
                .requestMatchers(HttpMethod.POST,"/pruebas")
                .hasRole("EMPLEADO")
                // Esta ruta puede ser accedida por cualquiera, sin autorización
                .requestMatchers("/pruebas/en-curso")
                .hasRole("EMPLEADO")
                // Esta ruta puede ser accedida únicamente por usuarios autenticados con el rol de administrador
                .requestMatchers("/reportes/**")
                .hasRole("ADMIN")
                // Esta ruta puede ser accedida únicamente por usuarios autenticados con el rol de administrador
                .requestMatchers("/vehiculos/posicion")
                .hasRole("VEHICULO")
                // Esta ruta puede ser accedida únicamente por usuarios autenticados con el rol de administrador
                .requestMatchers("/notificaciones")
                .hasAnyRole("ADMIN", "EMPLEADO")


                // Cualquier otra petición...
                .anyRequest()
                .authenticated()

        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Se especifica el nombre del claim a analizar
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        // Se agrega este prefijo en la conversión por una convención de Spring
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        // Se asocia el conversor de Authorities al Bean que convierte el token JWT a un objeto Authorization
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

}

