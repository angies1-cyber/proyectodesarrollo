package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.LoginRequestDto;
import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private AuthSecurityProperties properties;
    private Clock clock;
    private TokenService tokenService;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        properties = new AuthSecurityProperties();
        clock = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZoneOffset.UTC);
        tokenService = new TokenService(null) {
            @Override
            public String generateToken(Usuario usuario, java.util.List<gt.edu.umg.auth.dto.ModuloAccesoDto> modulos) {
                return "token";
            }
        };
        authorizationService = new AuthorizationService(null, null, null, null) {
            @Override
            public java.util.List<gt.edu.umg.auth.dto.ModuloAccesoDto> obtenerAccesos(Usuario usuario) {
                return java.util.List.of();
            }
        };
    }

    @Test
    void registroPublicoIgnoraRolesSolicitadosYAsignaUser() {
        Rol userRole = Rol.builder().nombre("ROLE_USER").build();
        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("secret12")).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        TokenService tokenService = new TokenService(null) {
            @Override
            public String generateToken(Usuario usuario, java.util.List<gt.edu.umg.auth.dto.ModuloAccesoDto> modulos) {
                return "token";
            }
        };
        AuthorizationService authorizationService = new AuthorizationService(null, null, null, null) {
            @Override
            public java.util.List<gt.edu.umg.auth.dto.ModuloAccesoDto> obtenerAccesos(Usuario usuario) {
                return java.util.List.of();
            }
        };
        AuthService service = new AuthService(
                usuarioRepository, rolRepository, passwordEncoder, tokenService, authorizationService,
                properties, clock);

        service.registrar(new RegisterRequestDto(
                "nuevo", "secret12", "nuevo@example.com", Set.of("ROLE_ADMIN")));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(Set.of("ROLE_USER"), captor.getValue().getRoles().stream()
                .map(Rol::getNombre)
                .collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void quintoFalloBloqueaUsuarioDuranteQuinceMinutos() {
        Usuario usuario = Usuario.builder()
                .username("ana")
                .password("encoded")
                .enabled(true)
                .failedLoginAttempts(4)
                .build();
        when(usuarioRepository.findByUsernameForUpdate("ana")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> service().login(new LoginRequestDto("ana", "wrong", "test-token")));

        assertEquals(5, usuario.getFailedLoginAttempts());
        assertEquals(Instant.parse("2026-11-05T12:15:00Z"), usuario.getLockedUntil());
        assertTrue(exception.getMessage() == null);
    }

    @Test
    void loginExitosoLimpiaIntentosYBloqueoExpirado() {
        Usuario usuario = Usuario.builder()
                .username("ana")
                .email("ana@example.com")
                .password("encoded")
                .enabled(true)
                .failedLoginAttempts(5)
                .lockedUntil(Instant.parse("2026-11-05T11:59:59Z"))
                .build();
        when(usuarioRepository.findByUsernameForUpdate("ana")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("correct", "encoded")).thenReturn(true);
        service().login(new LoginRequestDto("ana", "correct", "test-token"));

        assertEquals(0, usuario.getFailedLoginAttempts());
        assertNull(usuario.getLockedUntil());
    }

    @Test
    void usuarioDesconocidoUsaLaMismaExcepcionPublica() {
        when(usuarioRepository.findByUsernameForUpdate("nadie")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service().login(new LoginRequestDto("nadie", "password", "test-token")));
        verify(passwordEncoder).matches("password", "$2a$10$7EqJtq98hPqEX7fNZaFWoO5Jt7uG6P1C1JYQ9Vh7rV1mQYVYkP1aW");
    }

    private AuthService service() {
        return new AuthService(usuarioRepository, rolRepository, passwordEncoder, tokenService,
                authorizationService, properties, clock);
    }
}
