package gt.edu.umg.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequestDto(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50)
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100)
        String password,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email
        String email,

        Set<String> roles
) {}
