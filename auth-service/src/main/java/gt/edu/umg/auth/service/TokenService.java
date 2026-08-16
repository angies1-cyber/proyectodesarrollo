package gt.edu.umg.auth.service;

import gt.edu.umg.auth.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
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

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();

        List<String> roles = usuario.getRoles().stream()
                .map(r -> r.getNombre())
                .toList();

        List<String> permisos = usuario.getRoles().stream()
                .flatMap(r -> r.getPermisos().stream())
                .map(p -> p.getNombre())
                .distinct()
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plus(8, ChronoUnit.HOURS))
                .subject(usuario.getUsername())
                .claim("userId", usuario.getId())
                .claim("email", usuario.getEmail())
                .claim("roles", roles)
                .claim("permisos", permisos)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
