package gt.edu.umg.auth.dto;

import java.util.List;

public record AuthResponseDto(
        String token,
        String type,
        String username,
        String email,
        List<String> roles,
        List<ModuloAccesoDto> modulos
) {
    public AuthResponseDto(String token, String username, String email, List<String> roles,
                           List<ModuloAccesoDto> modulos) {
        this(token, "Bearer", username, email, roles, modulos);
    }
}
