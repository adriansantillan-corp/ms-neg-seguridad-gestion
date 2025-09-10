package com.nitro.ms.neg.seguridad.gestion.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableReactiveMethodSecurity
@Profile("test")
public class TestSecurityConfig {


    @Bean
    @Primary
    public ReactiveJwtDecoder jwtDecoder() {
        return token -> Mono.just(new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "test-user", "iss", "https://test-issuer.com")
        ));
    }

    @Bean
    @Primary
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().authenticated() // ← ¡REQUIERE autenticación!
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    @Primary
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    if (context.getAuthentication() != null) {
                        authorities.addAll(context.getAuthentication().getAuthorities());
                    }
                    if (authorities.isEmpty()) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    }
                    return (AbstractAuthenticationToken) new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("sub"));
                })
                .defaultIfEmpty((AbstractAuthenticationToken) new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER")), jwt.getClaimAsString("sub")))
                .cast(AbstractAuthenticationToken.class);
    }
}
