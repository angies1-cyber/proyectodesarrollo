package gt.edu.umg.auth.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ModuloPermisosRequestDto(@NotNull Long moduloId, Set<Long> permisoIds) {}
