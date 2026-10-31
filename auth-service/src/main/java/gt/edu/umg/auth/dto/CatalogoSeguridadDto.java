package gt.edu.umg.auth.dto;

import java.util.List;

public record CatalogoSeguridadDto(
        List<SistemaItemDto> sistemas,
        List<ModuloItemDto> modulos,
        List<PermisoItemDto> permisos
) {
    public record SistemaItemDto(Long id, String codigo, String nombre, boolean activo) {}
    public record ModuloItemDto(Long id, String codigo, String nombre, Long sistemaId, Long moduloPadreId, boolean activo) {}
    public record PermisoItemDto(Long id, String nombre, String descripcion) {}
}
