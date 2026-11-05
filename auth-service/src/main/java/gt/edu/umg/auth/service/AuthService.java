package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.AuthResponseDto;
import gt.edu.umg.auth.dto.LoginRequestDto;
import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Clock;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;
    private final AuthSecurityProperties securityProperties;
    private final Clock clock;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       AuthorizationService authorizationService,
                       AuthSecurityProperties securityProperties,
                       Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authorizationService = authorizationService;
        this.securityProperties = securityProperties;
        this.clock = clock;
    }

    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public AuthResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByUsernameForUpdate(request.username()).orElse(null);
        if (usuario == null) {
            passwordEncoder.matches(request.password(), DUMMY_PASSWORD_HASH);
            throw new InvalidCredentialsException();
        }

        Instant now = clock.instant();
        if (usuario.getLockedUntil() != null && usuario.getLockedUntil().isAfter(now)) {
            throw new InvalidCredentialsException();
        }
        if (usuario.getLockedUntil() != null) {
            clearFailures(usuario);
        }

        boolean passwordMatches = passwordEncoder.matches(request.password(), usuario.getPassword());
        if (!passwordMatches) {
            int attempts = usuario.getFailedLoginAttempts() + 1;
            usuario.setFailedLoginAttempts(attempts);
            if (attempts >= securityProperties.getMaxFailedAttempts()) {
                usuario.setLockedUntil(now.plus(securityProperties.getLockDuration()));
            }
            throw new InvalidCredentialsException();
        }

        if (!Boolean.TRUE.equals(usuario.getEnabled())) {
            throw new InvalidCredentialsException();
        }

        clearFailures(usuario);

        var modulos = authorizationService.obtenerAccesos(usuario);
        String token = tokenService.generateToken(usuario, modulos);

        List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).toList();

        return new AuthResponseDto(token, usuario.getUsername(), usuario.getEmail(), roles, modulos);
    }

    private void clearFailures(Usuario usuario) {
        usuario.setFailedLoginAttempts(0);
        usuario.setLockedUntil(null);
    }

    private static final String DUMMY_PASSWORD_HASH =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoO5Jt7uG6P1C1JYQ9Vh7rV1mQYVYkP1aW";

    @Transactional
    public AuthResponseDto registrar(RegisterRequestDto request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        Rol userRole = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("El rol predeterminado ROLE_USER no está configurado"));
        Set<Rol> roles = new HashSet<>(Set.of(userRole));

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .enabled(true)
                .roles(roles)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        var modulos = authorizationService.obtenerAccesos(guardado);
        String token = tokenService.generateToken(guardado, modulos);
        List<String> rolesList = guardado.getRoles().stream().map(Rol::getNombre).toList();

        return new AuthResponseDto(token, guardado.getUsername(), guardado.getEmail(), rolesList, modulos);
    }

}
