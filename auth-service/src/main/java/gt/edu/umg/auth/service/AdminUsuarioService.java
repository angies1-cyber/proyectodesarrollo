package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.AdminCreateUsuarioRequestDto;
import gt.edu.umg.auth.dto.AdminUsuarioResponseDto;
import gt.edu.umg.auth.dto.RolResumenDto;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminUsuarioResponseDto> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AdminUsuarioResponseDto crear(AdminCreateUsuarioRequestDto request, boolean actorEsSuperAdmin) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }
        Set<Rol> roles = obtenerRoles(request.roleIds(), actorEsSuperAdmin);
        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .enabled(true)
                .roles(roles)
                .build();
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public AdminUsuarioResponseDto cambiarRoles(Long id, Set<Long> roleIds, String actor,
                                                 boolean actorEsSuperAdmin) {
        Usuario usuario = getUsuario(id);
        Set<Rol> nuevosRoles = obtenerRoles(roleIds, actorEsSuperAdmin);
        boolean pierdeSuperAdmin = tieneRol(usuario, AuthorizationService.SUPER_ADMIN_ROLE)
                && nuevosRoles.stream().noneMatch(r -> AuthorizationService.SUPER_ADMIN_ROLE.equals(r.getNombre()));
        validarUltimoSuperAdmin(usuario, pierdeSuperAdmin);
        if (usuario.getUsername().equals(actor) && pierdeSuperAdmin) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puede quitarse su propio rol de superadministrador");
        }
        usuario.setRoles(nuevosRoles);
        return toResponse(usuario);
    }

    @Transactional
    public AdminUsuarioResponseDto cambiarEnabled(Long id, boolean enabled, String actor) {
        Usuario usuario = getUsuario(id);
        if (!enabled && usuario.getUsername().equals(actor)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puede inhabilitar su propio usuario");
        }
        validarUltimoSuperAdmin(usuario, !enabled);
        usuario.setEnabled(enabled);
        return toResponse(usuario);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        Usuario usuario = getUsuario(id);
        usuario.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void eliminar(Long id, String actor) {
        Usuario usuario = getUsuario(id);
        if (usuario.getUsername().equals(actor)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puede eliminar su propio usuario");
        }
        validarUltimoSuperAdmin(usuario, true);
        usuarioRepository.delete(usuario);
    }

    private Set<Rol> obtenerRoles(Set<Long> roleIds, boolean actorEsSuperAdmin) {
        List<Rol> roles = rolRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o más roles no existen");
        }
        if (roles.stream().anyMatch(r -> !Boolean.TRUE.equals(r.getActivo()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pueden asignar roles inactivos");
        }
        if (!actorEsSuperAdmin && roles.stream().anyMatch(r -> AuthorizationService.SUPER_ADMIN_ROLE.equals(r.getNombre()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un superadministrador puede asignar ese rol");
        }
        return new HashSet<>(roles);
    }

    private void validarUltimoSuperAdmin(Usuario usuario, boolean operacionQuitaAcceso) {
        if (operacionQuitaAcceso && Boolean.TRUE.equals(usuario.getEnabled())
                && tieneRol(usuario, AuthorizationService.SUPER_ADMIN_ROLE)
                && usuarioRepository.countByEnabledTrueAndRolesNombre(AuthorizationService.SUPER_ADMIN_ROLE) <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe existir al menos un superadministrador habilitado");
        }
    }

    private boolean tieneRol(Usuario usuario, String nombre) {
        return usuario.getRoles().stream().anyMatch(r -> nombre.equals(r.getNombre()));
    }

    private Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private AdminUsuarioResponseDto toResponse(Usuario usuario) {
        List<RolResumenDto> roles = usuario.getRoles().stream()
                .map(r -> new RolResumenDto(r.getId(), r.getNombre(), r.getDescripcion(), Boolean.TRUE.equals(r.getActivo())))
                .sorted(java.util.Comparator.comparing(RolResumenDto::nombre))
                .toList();
        return new AdminUsuarioResponseDto(usuario.getId(), usuario.getUsername(), usuario.getEmail(), roles,
                Boolean.TRUE.equals(usuario.getEnabled()));
    }
}
