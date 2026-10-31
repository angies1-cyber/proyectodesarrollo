package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                usuarioRepository, rolRepository, passwordEncoder, tokenService, authorizationService);

        service.registrar(new RegisterRequestDto(
                "nuevo", "secret12", "nuevo@example.com", Set.of("ROLE_ADMIN")));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(Set.of("ROLE_USER"), captor.getValue().getRoles().stream()
                .map(Rol::getNombre)
                .collect(java.util.stream.Collectors.toSet()));
    }
}
