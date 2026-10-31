package gt.edu.umg.auth.dto;

import java.util.List;

public record AdminUsuarioResponseDto(
        Long id,
        String username,
        String email,
        List<RolResumenDto> roles,
        boolean enabled
) {}
