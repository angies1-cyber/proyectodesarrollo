package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.ModuloAccesoDto;
import gt.edu.umg.auth.entity.Modulo;
import gt.edu.umg.auth.entity.Rol;
import gt.edu.umg.auth.entity.RolModuloPermiso;
import gt.edu.umg.auth.entity.Usuario;
import gt.edu.umg.auth.repository.ModuloRepository;
import gt.edu.umg.auth.repository.PermisoRepository;
import gt.edu.umg.auth.repository.RolModuloPermisoRepository;
import gt.edu.umg.auth.repository.RolModuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {
    public static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    private final RolModuloRepository rolModuloRepository;
    private final RolModuloPermisoRepository rolModuloPermisoRepository;
    private final ModuloRepository moduloRepository;
    private final PermisoRepository permisoRepository;

    public AuthorizationService(RolModuloRepository rolModuloRepository,
                                RolModuloPermisoRepository rolModuloPermisoRepository,
                                ModuloRepository moduloRepository,
                                PermisoRepository permisoRepository) {
        this.rolModuloRepository = rolModuloRepository;
        this.rolModuloPermisoRepository = rolModuloPermisoRepository;
        this.moduloRepository = moduloRepository;
        this.permisoRepository = permisoRepository;
    }

    @Transactional(readOnly = true)
    public List<ModuloAccesoDto> obtenerAccesos(Usuario usuario) {
        List<Rol> rolesActivos = usuario.getRoles().stream()
                .filter(r -> Boolean.TRUE.equals(r.getActivo()))
                .filter(r -> r.getSistema() != null && Boolean.TRUE.equals(r.getSistema().getActivo()))
                .toList();
        boolean superAdmin = rolesActivos.stream().anyMatch(r -> SUPER_ADMIN_ROLE.equals(r.getNombre()));

        List<Modulo> modulos;
        Map<Long, Set<String>> permisosPorModulo = new HashMap<>();
        if (superAdmin) {
            modulos = moduloRepository.findAllByOrderByOrdenAscNombreAsc().stream()
                    .filter(this::estaDisponible)
                    .toList();
            Set<String> todos = permisoRepository.findAll().stream()
                    .map(p -> p.getNombre())
                    .collect(Collectors.toCollection(TreeSet::new));
            modulos.forEach(m -> permisosPorModulo.put(m.getId(), new TreeSet<>(todos)));
        } else {
            List<Long> roleIds = rolesActivos.stream().map(Rol::getId).toList();
            if (roleIds.isEmpty()) return List.of();
            modulos = rolModuloRepository.findByRolIdIn(roleIds).stream()
                    .map(rm -> rm.getModulo())
                    .filter(this::estaDisponible)
                    .collect(Collectors.toMap(Modulo::getId, Function.identity(), (a, b) -> a))
                    .values().stream().toList();
            Set<Long> idsVisibles = modulos.stream().map(Modulo::getId).collect(Collectors.toSet());
            for (RolModuloPermiso asignacion : rolModuloPermisoRepository.findByRolIdIn(roleIds)) {
                if (idsVisibles.contains(asignacion.getModulo().getId())) {
                    permisosPorModulo.computeIfAbsent(asignacion.getModulo().getId(), id -> new TreeSet<>())
                            .add(asignacion.getPermiso().getNombre());
                }
            }
        }

        return modulos.stream()
                .sorted(Comparator.comparing((Modulo m) -> m.getModuloPadre() == null ? 0 : 1)
                        .thenComparing(Modulo::getOrden).thenComparing(Modulo::getNombre))
                .map(m -> new ModuloAccesoDto(
                        m.getId(), m.getCodigo(), m.getNombre(), m.getDescripcion(), m.getRuta(), m.getIcono(),
                        m.getModuloPadre() == null ? null : m.getModuloPadre().getId(), m.getOrden(),
                        List.copyOf(permisosPorModulo.getOrDefault(m.getId(), Set.of()))))
                .toList();
    }

    private boolean estaDisponible(Modulo modulo) {
        return Boolean.TRUE.equals(modulo.getActivo())
                && modulo.getSistema() != null
                && Boolean.TRUE.equals(modulo.getSistema().getActivo())
                && (modulo.getModuloPadre() == null || Boolean.TRUE.equals(modulo.getModuloPadre().getActivo()));
    }
}
