package gt.edu.umg.core.entities.Dtos.Request;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

public record MedicoRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @NotBlank(message = "El DPI es obligatorio")
        @Pattern(regexp = "^\\d{13}$", message = "El DPI debe contener 13 dígitos")
        String dpi,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser pasada")
        LocalDate fechaNacimiento,

        @Pattern(regexp = "^$|^\\d{8}$", message = "El teléfono debe contener 8 dígitos")
        String telefono,

        @Email(message = "Debe ser un correo válido")
        String correo,

        String direccion,

        @NotNull(message = "La especialidad es obligatoria")
        Long especialidadId
) {}
