package com.nitro.ms.neg.seguridad.gestion.infrastructure.config;

import com.nitro.ms.neg.seguridad.gestion.domain.repository.RoleRepository;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class JwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        // 1. Extraemos el 'sub' del token, que es nuestro cognito_sub
        String cognitoSub = jwt.getSubject();

        // 2. Buscamos al usuario en nuestra base de datos
        return userRepository.findByCognitoSub(cognitoSub)
                // 3. Si el usuario existe, usamos flatMap para encadenar la siguiente operación asíncrona
                .flatMap(user ->
                        // 4. Buscamos los roles de ese usuario en nuestra base de datos
                        roleRepository.findRolesByUserId(user.getId())
                                // 5. Recolectamos todos los roles en una lista
                                .collectList()
                                // 6. Mapeamos la lista de roles a una colección de GrantedAuthority de Spring Security
                                .map(roles -> {
                                    Collection<GrantedAuthority> authorities = roles.stream()
                                            // Es una convención de Spring Security prefijar los roles con "ROLE_"
                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                                            .collect(Collectors.toSet());

                                    // 7. Creamos el token de autenticación final con los roles de NUESTRA BASE DE DATOS
                                    return new JwtAuthenticationToken(jwt, authorities);
                                })
                )
                // 8. Si en el paso 2 el usuario no se encuentra en nuestra DB,
                // creamos un token de autenticación sin roles.
                .cast(AbstractAuthenticationToken.class)
                .defaultIfEmpty(new JwtAuthenticationToken(jwt, Collections.emptyList()));
    }
}
