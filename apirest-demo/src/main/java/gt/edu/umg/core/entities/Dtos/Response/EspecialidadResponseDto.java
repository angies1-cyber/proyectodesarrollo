package gt.edu.umg.core.entities.Dtos.Response;

public record EspecialidadResponseDto(
        Long id,
        String nombre,
        String descripcion,
        Boolean activo
) {}
