package gt.edu.umg.auth.dto;

import java.util.List;

public record RolAdminResponseDto(
        Long id,
        String nombre,
        String descripcion,
        boolean activo,
        Long sistemaId,
        String sistemaNombre,
        List<AsignacionModuloDto> modulos
) {}
