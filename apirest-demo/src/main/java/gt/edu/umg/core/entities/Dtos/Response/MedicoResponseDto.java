package gt.edu.umg.core.entities.Dtos.Response;

import java.time.LocalDate;

public record MedicoResponseDto(
        Long id,
        String nombre,
        String apellido,
        String dpi,
        LocalDate fechaNacimiento,
        Integer edad,
        String telefono,
        String correo,
        String direccion,
        Boolean activo,
        LocalDate fechaCreacion,
        Long especialidadId,
        String especialidadNombre
) {}
