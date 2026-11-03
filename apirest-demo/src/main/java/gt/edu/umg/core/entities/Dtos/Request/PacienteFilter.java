package gt.edu.umg.core.entities.Dtos.Request;

public record PacienteFilter(
        String nombre,
        String apellido,
        String dpi,
        String telefono,
        String correo,
        Boolean activo
) {}
