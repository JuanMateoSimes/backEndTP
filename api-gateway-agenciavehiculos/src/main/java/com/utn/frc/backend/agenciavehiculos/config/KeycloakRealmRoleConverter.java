package com.utn.frc.backend.agenciavehiculos.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Extraer los roles de realm_access.roles
        var realmRoles = jwt.getClaimAsMap("realm_access");
        if (realmRoles == null || !realmRoles.containsKey("roles")) {
            return List.of(); // No hay roles, devuelve una lista vacía
        }

        Collection<String> roles = (Collection<String>) realmRoles.get("roles");
        // Convertir los roles para que Spring Security los interprete con el prefijo "ROLE_"
        return roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}