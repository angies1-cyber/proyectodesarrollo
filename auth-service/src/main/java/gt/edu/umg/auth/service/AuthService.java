package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.AuthResponseDto;
import gt.edu.umg.auth.dto.LoginRequestDto;
import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.entity.Permiso;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.PermisoRepository;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PermisoRepository permisoRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas (usuario no encontrado)"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas (contraseña incorrecta)");
        }

        if (!usuario.getEnabled()) {
            throw new RuntimeException("El usuario está deshabilitado");
        }

        String token = tokenService.generateToken(usuario);

        List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).toList();

        return new AuthResponseDto(token, usuario.getUsername(), usuario.getEmail(), roles);
    }

    public AuthResponseDto registrar(RegisterRequestDto request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El nombre de usuario ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        Set<Rol> roles = new HashSet<>();
        if (request.roles() == null || request.roles().isEmpty()) {
            Rol userRole = rolRepository.findByNombre("ROLE_USER")
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre("ROLE_USER").build()));
            roles.add(userRole);
        } else {
            for (String roleName : request.roles()) {
                String formattedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                Rol rol = rolRepository.findByNombre(formattedRole)
                        .orElseGet(() -> rolRepository.save(Rol.builder().nombre(formattedRole).build()));
                roles.add(rol);
            }
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .enabled(true)
                .roles(roles)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        String token = tokenService.generateToken(guardado);
        List<String> rolesList = guardado.getRoles().stream().map(Rol::getNombre).toList();

        return new AuthResponseDto(token, guardado.getUsername(), guardado.getEmail(), rolesList);
    }
}
