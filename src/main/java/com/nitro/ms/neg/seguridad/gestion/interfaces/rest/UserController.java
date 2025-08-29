package com.nitro.ms.neg.seguridad.gestion.interfaces.rest;

import com.nitro.ms.neg.seguridad.gestion.application.service.UserApplicationService;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request.CreateUserRequestDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.dto.response.UserResponseDto;
import com.nitro.ms.neg.seguridad.gestion.interfaces.mapper.UserApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles")
@Validated
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserApiMapper userApiMapper;

    @GetMapping
    @Operation(summary = "Get all user profiles")
    public Flux<UserResponseDto> getAllUsers() {
        return userApplicationService.findAllUsers().map(userApiMapper::toDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user profile by its ID")
    public Mono<UserResponseDto> getUserById(
            @PathVariable @Min(value = 1, message = "User ID must be a positive number.") Long id) {
        return userApplicationService.findUserById(id).map(userApiMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user profile (Just-In-Time Provisioning)")
    public Mono<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        return userApplicationService.createUser(userApiMapper.toDomain(requestDto))
                .map(userApiMapper::toDto);
    }
}
