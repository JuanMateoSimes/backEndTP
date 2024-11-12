package com.utn.frc.backend.pruebaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                // Autorizar solicitudes post a empleados
                .requestMatchers(HttpMethod.POST,"/pruebas")
                .hasRole("EMPLEADO")
                // Autorizar el get a pruebas en curso a empleados
                .requestMatchers("/pruebas/en-curso")
                .hasRole("EMPLEADO")
                // Autorizar todas las solicitudes a endpoints de reportes a admin
                .requestMatchers("/reportes/**")
                .hasRole("ADMIN")
                // Autorizar la solicitud post para registrar una posicion a vehiculo
                .requestMatchers("/vehiculos/posicion")
                .hasRole("VEHICULO")
                // Autorizar los endpoints de notificaciones a admin y empleado
                .requestMatchers("/notificaciones")
                .hasAnyRole("ADMIN", "EMPLEADO")

                .anyRequest()
                .authenticated()

        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

}

