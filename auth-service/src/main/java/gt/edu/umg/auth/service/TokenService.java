package gt.edu.umg.auth.service;

import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.dto.ModuloAccesoDto;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Usuario usuario, List<ModuloAccesoDto> modulos) {
        Instant now = Instant.now();

        List<String> roles = usuario.getRoles().stream()
                .map(r -> r.getNombre())
                .toList();

        List<String> authorities = modulos.stream()
                .flatMap(m -> m.permisos().stream().map(p -> m.codigo() + ":" + p))
                .distinct()
                .toList();
        boolean superAdmin = roles.contains(AuthorizationService.SUPER_ADMIN_ROLE);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(8, ChronoUnit.HOURS))
                .subject(usuario.getUsername())
                .claim("userId", usuario.getId())
                .claim("email", usuario.getEmail())
                .claim("roles", roles)
                .claim("authorities", authorities)
                .claim("superAdmin", superAdmin)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(Usuario usuario) {
        return generateToken(usuario, List.of());
    }
}
