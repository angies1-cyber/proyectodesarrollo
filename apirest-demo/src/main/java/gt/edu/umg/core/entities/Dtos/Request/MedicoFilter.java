package gt.edu.umg.core.entities.Dtos.Request;

public record MedicoFilter(
        String nombre,
        String apellido,
        String dpi,
        Long especialidadId,
        Boolean activo
) {}
