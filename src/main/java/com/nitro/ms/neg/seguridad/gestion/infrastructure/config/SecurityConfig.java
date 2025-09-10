package com.nitro.ms.neg.seguridad.gestion.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity // Habilita la seguridad a nivel de método (ej. @PreAuthorize)
@RequiredArgsConstructor
@Profile("!local & !test")
public class SecurityConfig {

    // Inyectamos el valor del issuer-uri desde application.yml
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Deshabilitamos CSRF para APIs stateless
                .authorizeExchange(exchanges -> exchanges
                        // PERMITIMOS el acceso sin autenticación a la documentación de la API
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        // EXIGIMOS autenticación para cualquier otra ruta bajo /v1/
                        .pathMatchers("/v1/**").authenticated()
                        // Denegamos cualquier otra petición por defecto
                        .anyExchange().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)) // <-- AQUÍ ESTÁ LA MAGIA
                );

        return http.build();
    }

    // Este Bean es opcional si el issuer-uri está en application.yml,
    // pero hacerlo explícito mejora la claridad y las pruebas.
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri);
    }
}