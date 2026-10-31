package gt.edu.umg.auth.dto;

import java.util.List;

public record ModuloAccesoDto(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String ruta,
        String icono,
        Long moduloPadreId,
        Integer orden,
        List<String> permisos
) {}
