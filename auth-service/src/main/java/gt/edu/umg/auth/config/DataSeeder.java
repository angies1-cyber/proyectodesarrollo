package gt.edu.umg.auth.config;

import gt.edu.umg.auth.entity.*;
import gt.edu.umg.auth.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final SistemaRepository sistemaRepository;
    private final ModuloRepository moduloRepository;
    private final RolModuloRepository rolModuloRepository;
    private final RolModuloPermisoRepository rolModuloPermisoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.initial-admin.username:}")
    private String initialAdminUsername;

    @Value("${app.initial-admin.email:}")
    private String initialAdminEmail;

    @Value("${app.initial-admin.password:}")
    private String initialAdminPassword;

    public DataSeeder(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                      PermisoRepository permisoRepository, SistemaRepository sistemaRepository,
                      ModuloRepository moduloRepository, RolModuloRepository rolModuloRepository,
                      RolModuloPermisoRepository rolModuloPermisoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.sistemaRepository = sistemaRepository;
        this.moduloRepository = moduloRepository;
        this.rolModuloRepository = rolModuloRepository;
        this.rolModuloPermisoRepository = rolModuloPermisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Sistema sistema = sistemaRepository.findByCodigo("CLINICA").orElseGet(() -> sistemaRepository.save(
                Sistema.builder().codigo("CLINICA").nombre("Sistema de Gestión Clínica")
                        .dominio(frontendUrl).activo(true).build()));

        Map<String, Permiso> permisos = new LinkedHashMap<>();
        permisos.put("READ", permiso("READ", "Consultar información"));
        permisos.put("CREATE", permiso("CREATE", "Crear registros"));
        permisos.put("UPDATE", permiso("UPDATE", "Modificar registros"));
        permisos.put("DELETE", permiso("DELETE", "Eliminar registros"));
        permisos.put("RESET_PASSWORD", permiso("RESET_PASSWORD", "Restablecer contraseñas de usuarios"));

        Map<String, Modulo> modulos = new LinkedHashMap<>();
        modulos.put("DASHBOARD", modulo(sistema, "DASHBOARD", "Inicio", "Panel principal", "/dashboard", "dashboard", 1, null));
        modulos.put("PACIENTES", modulo(sistema, "PACIENTES", "Pacientes", "Gestión de pacientes", "/pacientes", "people", 2, null));
        modulos.put("MEDICOS", modulo(sistema, "MEDICOS", "Médicos", "Gestión de médicos", "/medicos", "medical", 3, null));
        modulos.put("ESPECIALIDADES", modulo(sistema, "ESPECIALIDADES", "Especialidades", "Catálogo de especialidades", "/especialidades", "specialty", 4, null));
        modulos.put("CITAS", modulo(sistema, "CITAS", "Citas", "Agenda de citas", "/citas", "calendar", 5, null));
        modulos.put("ENFERMEDADES", modulo(sistema, "ENFERMEDADES", "Enfermedades", "Catálogo clínico interno", null, "health", 6, null));
        Modulo administracion = modulo(sistema, "ADMINISTRACION", "Administración", "Seguridad del sistema", null, "settings", 7, null);
        modulos.put("ADMINISTRACION", administracion);
        modulos.put("USUARIOS", modulo(sistema, "USUARIOS", "Usuarios", "Administración de cuentas", "/administracion/usuarios", "users", 1, administracion));
        modulos.put("ROLES", modulo(sistema, "ROLES", "Roles y permisos", "Administración de accesos", "/administracion/roles", "security", 2, administracion));

        Rol superAdmin = rol("ROLE_SUPER_ADMIN", "Control total del sistema", sistema);
        Rol admin = rol("ROLE_ADMIN", "Administración operativa", sistema);
        Rol user = rol("ROLE_USER", "Operación clínica", sistema);

        asignarTodos(superAdmin, modulos.values(), permisos.values());
        asignarTodos(admin, modulos.values(), permisos.values());
        asignar(user, modulos.get("DASHBOARD"), List.of(permisos.get("READ")));
        asignar(user, modulos.get("PACIENTES"), List.of(permisos.get("READ"), permisos.get("CREATE"), permisos.get("UPDATE")));
        asignar(user, modulos.get("MEDICOS"), List.of(permisos.get("READ")));
        asignar(user, modulos.get("ESPECIALIDADES"), List.of(permisos.get("READ")));
        asignar(user, modulos.get("CITAS"), List.of(permisos.get("READ"), permisos.get("CREATE"), permisos.get("UPDATE")));

        if (!initialAdminUsername.isBlank() && !initialAdminEmail.isBlank() && !initialAdminPassword.isBlank()) {
            usuario(initialAdminUsername, initialAdminPassword, initialAdminEmail, superAdmin);
        }
    }

    private Permiso permiso(String nombre, String descripcion) {
        return permisoRepository.findByNombre(nombre).orElseGet(() -> permisoRepository.save(
                Permiso.builder().nombre(nombre).descripcion(descripcion).build()));
    }

    private Modulo modulo(Sistema sistema, String codigo, String nombre, String descripcion, String ruta,
                          String icono, int orden, Modulo padre) {
        return moduloRepository.findByCodigoAndSistemaId(codigo, sistema.getId()).orElseGet(() -> moduloRepository.save(
                Modulo.builder().sistema(sistema).codigo(codigo).nombre(nombre).descripcion(descripcion)
                        .ruta(ruta).icono(icono).orden(orden).moduloPadre(padre).activo(true).build()));
    }

    private Rol rol(String nombre, String descripcion, Sistema sistema) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(
                Rol.builder().nombre(nombre).descripcion(descripcion).activo(true).sistema(sistema).build()));
    }

    private void asignarTodos(Rol rol, Collection<Modulo> modulos, Collection<Permiso> permisos) {
        modulos.forEach(modulo -> asignar(rol, modulo, permisos));
    }

    private void asignar(Rol rol, Modulo modulo, Collection<Permiso> permisos) {
        rolModuloRepository.findByRolIdAndModuloId(rol.getId(), modulo.getId()).orElseGet(() ->
                rolModuloRepository.save(RolModulo.builder().rol(rol).modulo(modulo).build()));
        permisos.forEach(permiso -> rolModuloPermisoRepository
                .findByRolIdAndModuloIdAndPermisoId(rol.getId(), modulo.getId(), permiso.getId())
                .orElseGet(() -> rolModuloPermisoRepository.save(RolModuloPermiso.builder()
                        .rol(rol).modulo(modulo).permiso(permiso).build())));
    }

    private void usuario(String username, String password, String email, Rol rol) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            usuarioRepository.save(Usuario.builder().username(username).password(passwordEncoder.encode(password))
                    .email(email).enabled(true).roles(new HashSet<>(Set.of(rol))).build());
        }
    }
}
