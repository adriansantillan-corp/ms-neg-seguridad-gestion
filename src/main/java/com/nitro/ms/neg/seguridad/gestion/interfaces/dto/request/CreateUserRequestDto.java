package com.nitro.ms.neg.seguridad.gestion.interfaces.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequestDto {

    @NotEmpty(message = "Cognito SUB cannot be empty.")
    private String cognitoSub;

    @NotEmpty(message = "Username cannot be empty.")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters.")
    private String username;

    @NotEmpty(message = "Email cannot be empty.")
    @Email(message = "Email should be valid.")
    private String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters.")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters.")
    private String lastName;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters.")
    private String phone;

    @Size(max = 5, message = "Country code cannot exceed 5 characters.")
    private String countryCode;
}