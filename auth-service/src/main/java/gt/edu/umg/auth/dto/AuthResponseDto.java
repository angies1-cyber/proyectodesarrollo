package gt.edu.umg.auth.dto;

import java.util.List;

public record AuthResponseDto(
        String token,
        String type,
        String username,
        String email,
        List<String> roles
) {
    public AuthResponseDto(String token, String username, String email, List<String> roles) {
        this(token, "Bearer", username, email, roles);
    }
}
