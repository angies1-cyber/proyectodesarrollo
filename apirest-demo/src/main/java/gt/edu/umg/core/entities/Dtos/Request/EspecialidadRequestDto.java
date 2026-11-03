package gt.edu.umg.core.entities.Dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadRequestDto(
        @NotBlank(message = "El nombre de la especialidad es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
        String descripcion
) {}
