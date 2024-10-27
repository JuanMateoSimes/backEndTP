package com.utn.frc.backend.pruebaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para facilitar pruebas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test").permitAll() // Permitir acceso sin autenticación a /test
                        .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                )
                .httpBasic(Customizer.withDefaults()); // Usar autenticación básica

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password("{noop}admin") // Usa {noop} para evitar la codificación de la contraseña
                .roles("USER")
                .build());
        return manager;
    }
}


