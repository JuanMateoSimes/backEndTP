package com.utn.frc.backend.agenciavehiculos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakRealmRoleConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var realmRoles = jwt.getClaimAsMap("realm_access");
        if (realmRoles == null || !realmRoles.containsKey("roles")) {
            logger.warn("No se encontraron roles en realm_access.roles");
            return List.of(); // No hay roles, devuelve una lista vacía
        }

        Collection<String> roles = (Collection<String>) realmRoles.get("roles");
        logger.debug("Roles extraídos del token: {}", roles);

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        logger.debug("Authorities creadas (con prefijo ROLE_): {}", authorities);
        return authorities;
    }
}
