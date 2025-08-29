package com.nitro.ms.neg.seguridad.gestion.application.service;

import com.nitro.ms.neg.seguridad.gestion.domain.model.Role;
import com.nitro.ms.neg.seguridad.gestion.domain.repository.RoleRepository;
import com.nitro.ms.neg.seguridad.gestion.infrastructure.exception.specific_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    public Mono<Role> findRoleById(Long id) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found with id: " + id)));
    }

    public Flux<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Mono<Role> createRole(Role role) {
        return roleRepository.existsByRoleNameAndCountryCode(role.getRoleName(), role.getCountryCode())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Role with name '" + role.getRoleName() + "' already exists in country '" + role.getCountryCode() + "'"));
                    }
                    return roleRepository.save(role);
                });
    }
}