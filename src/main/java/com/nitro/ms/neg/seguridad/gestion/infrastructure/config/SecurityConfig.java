package com.nitro.ms.neg.seguridad.gestion.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // Inyectamos el valor del issuer-uri desde application.yml
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

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
                // Configuramos el servidor de recursos para que valide JWTs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // Este Bean es opcional si el issuer-uri está en application.yml,
    // pero hacerlo explícito mejora la claridad y las pruebas.
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri);
    }
}