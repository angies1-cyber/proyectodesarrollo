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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       AuthorizationService authorizationService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authorizationService = authorizationService;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas (usuario no encontrado)"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas (contraseña incorrecta)");
        }

        if (!usuario.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario está inhabilitado");
        }

        var modulos = authorizationService.obtenerAccesos(usuario);
        String token = tokenService.generateToken(usuario, modulos);

        List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).toList();

        return new AuthResponseDto(token, usuario.getUsername(), usuario.getEmail(), roles, modulos);
    }

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
