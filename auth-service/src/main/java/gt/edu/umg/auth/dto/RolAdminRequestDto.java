package gt.edu.umg.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RolAdminRequestDto(
        @NotBlank String nombre,
        String descripcion,
        boolean activo,
        @NotNull Long sistemaId,
        @Valid List<ModuloPermisosRequestDto> modulos
) {}
