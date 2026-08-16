package gt.edu.umg.auth.config;

import gt.edu.umg.auth.entity.Permiso;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.PermisoRepository;
import gt.edu.umg.auth.repository.RolRepository;
import gt.edu.umg.auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      RolRepository rolRepository,
                      PermisoRepository permisoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (permisoRepository.count() == 0) {
            Permiso read = permisoRepository.save(Permiso.builder().nombre("READ_PRIVILEGE").descripcion("Permiso de lectura").build());
            Permiso write = permisoRepository.save(Permiso.builder().nombre("WRITE_PRIVILEGE").descripcion("Permiso de escritura").build());
            Permiso delete = permisoRepository.save(Permiso.builder().nombre("DELETE_PRIVILEGE").descripcion("Permiso de eliminación").build());

            Rol adminRole = rolRepository.save(Rol.builder().nombre("ROLE_ADMIN").permisos(Set.of(read, write, delete)).build());
            Rol userRole = rolRepository.save(Rol.builder().nombre("ROLE_USER").permisos(Set.of(read)).build());

            if (usuarioRepository.count() == 0) {
                Usuario admin = Usuario.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@clinica.com")
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();

                Usuario user = Usuario.builder()
                        .username("usuario")
                        .password(passwordEncoder.encode("user123"))
                        .email("user@clinica.com")
                        .enabled(true)
                        .roles(Set.of(userRole))
                        .build();

                usuarioRepository.save(admin);
                usuarioRepository.save(user);
                System.out.println("✅ DataSeeder: Usuarios iniciales creados (admin/admin123, usuario/user123)");
            }
        }
    }
}
