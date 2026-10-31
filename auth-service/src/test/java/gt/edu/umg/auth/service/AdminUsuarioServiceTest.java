package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.AdminCreateUsuarioRequestDto;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminUsuarioService service;

    @BeforeEach
    void setUp() {
        service = new AdminUsuarioService(usuarioRepository, rolRepository, passwordEncoder);
    }

    @Test
    void crearNormalizaRolPermitido() {
        Rol admin = Rol.builder().id(2L).nombre("ROLE_ADMIN").activo(true).build();
        when(rolRepository.findAllById(Set.of(2L))).thenReturn(java.util.List.of(admin));
        when(passwordEncoder.encode("secret12")).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        var response = service.crear(new AdminCreateUsuarioRequestDto(
                "nuevo", "secret12", "nuevo@example.com", Set.of(2L)), false);

        assertEquals(Set.of("ROLE_ADMIN"), response.roles().stream().map(r -> r.nombre()).collect(java.util.stream.Collectors.toSet()));
        assertEquals("nuevo", response.username());
    }

    @Test
    void crearRechazaRolArbitrario() {
        var exception = assertThrows(ResponseStatusException.class, () -> service.crear(
                new AdminCreateUsuarioRequestDto("nuevo", "secret12", "nuevo@example.com", Set.of(99L)), false));

        assertEquals(400, exception.getStatusCode().value());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void noPermiteQueUsuarioAutenticadoSeInhabilite() {
        Usuario admin = Usuario.builder()
                .id(7L)
                .username("admin")
                .enabled(true)
                .roles(Set.of(Rol.builder().nombre("ROLE_ADMIN").build()))
                .build();
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(admin));

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.cambiarEnabled(7L, false, "admin"));

        assertEquals(400, exception.getStatusCode().value());
        verify(usuarioRepository, never()).save(any());
    }
}
