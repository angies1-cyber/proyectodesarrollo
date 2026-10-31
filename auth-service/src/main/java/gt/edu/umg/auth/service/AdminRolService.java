package gt.edu.umg.auth.service;

import gt.edu.umg.auth.dto.*;
import gt.edu.umg.auth.entity.*;
import gt.edu.umg.auth.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AdminRolService {
    private final RolRepository rolRepository;
    private final SistemaRepository sistemaRepository;
    private final ModuloRepository moduloRepository;
    private final PermisoRepository permisoRepository;
    private final RolModuloRepository rolModuloRepository;
    private final RolModuloPermisoRepository rolModuloPermisoRepository;

    public AdminRolService(RolRepository rolRepository, SistemaRepository sistemaRepository,
                           ModuloRepository moduloRepository, PermisoRepository permisoRepository,
                           RolModuloRepository rolModuloRepository,
                           RolModuloPermisoRepository rolModuloPermisoRepository) {
        this.rolRepository = rolRepository;
        this.sistemaRepository = sistemaRepository;
        this.moduloRepository = moduloRepository;
        this.permisoRepository = permisoRepository;
        this.rolModuloRepository = rolModuloRepository;
        this.rolModuloPermisoRepository = rolModuloPermisoRepository;
    }

    @Transactional(readOnly = true)
    public List<RolAdminResponseDto> listar() {
        return rolRepository.findAllByOrderByNombreAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CatalogoSeguridadDto catalogo() {
        var sistemas = sistemaRepository.findAll().stream()
                .map(s -> new CatalogoSeguridadDto.SistemaItemDto(s.getId(), s.getCodigo(), s.getNombre(), Boolean.TRUE.equals(s.getActivo())))
                .toList();
        var modulos = moduloRepository.findAllByOrderByOrdenAscNombreAsc().stream()
                .map(m -> new CatalogoSeguridadDto.ModuloItemDto(m.getId(), m.getCodigo(), m.getNombre(),
                        m.getSistema().getId(), m.getModuloPadre() == null ? null : m.getModuloPadre().getId(),
                        Boolean.TRUE.equals(m.getActivo())))
                .toList();
        var permisos = permisoRepository.findAll().stream()
                .sorted(Comparator.comparing(Permiso::getNombre))
                .map(p -> new CatalogoSeguridadDto.PermisoItemDto(p.getId(), p.getNombre(), p.getDescripcion()))
                .toList();
        return new CatalogoSeguridadDto(sistemas, modulos, permisos);
    }

    @Transactional
    public RolAdminResponseDto crear(RolAdminRequestDto request, boolean actorEsSuperAdmin) {
        String nombre = normalizar(request.nombre());
        validarRolReservado(nombre, actorEsSuperAdmin);
        if (rolRepository.findByNombre(nombre).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con ese nombre");
        }
        Sistema sistema = sistemaRepository.findById(request.sistemaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sistema no encontrado"));
        Rol rol = rolRepository.save(Rol.builder().nombre(nombre).descripcion(request.descripcion())
                .activo(request.activo()).sistema(sistema).build());
        guardarAsignaciones(rol, request.modulos());
        return toResponse(rol);
    }

    @Transactional
    public RolAdminResponseDto actualizar(Long id, RolAdminRequestDto request, boolean actorEsSuperAdmin) {
        Rol rol = getRol(id);
        validarRolReservado(rol.getNombre(), actorEsSuperAdmin);
        String nombre = normalizar(request.nombre());
        validarRolReservado(nombre, actorEsSuperAdmin);
        rolRepository.findByNombre(nombre).filter(r -> !r.getId().equals(id)).ifPresent(r -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con ese nombre");
        });
        Sistema sistema = sistemaRepository.findById(request.sistemaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sistema no encontrado"));
        rol.setNombre(nombre);
        rol.setDescripcion(request.descripcion());
        rol.setActivo(request.activo());
        rol.setSistema(sistema);
        rolModuloPermisoRepository.deleteByRolId(id);
        rolModuloRepository.deleteByRolId(id);
        rolModuloPermisoRepository.flush();
        rolModuloRepository.flush();
        guardarAsignaciones(rol, request.modulos());
        return toResponse(rol);
    }

    @Transactional
    public void eliminar(Long id, boolean actorEsSuperAdmin) {
        Rol rol = getRol(id);
        validarRolReservado(rol.getNombre(), actorEsSuperAdmin);
        if (List.of(AuthorizationService.SUPER_ADMIN_ROLE, "ROLE_ADMIN", "ROLE_USER").contains(rol.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los roles iniciales no se pueden eliminar; puede inhabilitarlos");
        }
        rolModuloPermisoRepository.deleteByRolId(id);
        rolModuloRepository.deleteByRolId(id);
        rolRepository.delete(rol);
    }

    private void guardarAsignaciones(Rol rol, List<ModuloPermisosRequestDto> solicitudes) {
        if (solicitudes == null) return;
        for (ModuloPermisosRequestDto solicitud : solicitudes) {
            Modulo modulo = moduloRepository.findById(solicitud.moduloId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Módulo no encontrado"));
            if (!modulo.getSistema().getId().equals(rol.getSistema().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El módulo y el rol deben pertenecer al mismo sistema");
            }
            rolModuloRepository.save(RolModulo.builder().rol(rol).modulo(modulo).build());
            Set<Long> permisoIds = solicitud.permisoIds() == null ? Set.of() : solicitud.permisoIds();
            List<Permiso> permisos = permisoRepository.findAllById(permisoIds);
            if (permisos.size() != permisoIds.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o más permisos no existen");
            }
            permisos.forEach(p -> rolModuloPermisoRepository.save(
                    RolModuloPermiso.builder().rol(rol).modulo(modulo).permiso(p).build()));
        }
    }

    private RolAdminResponseDto toResponse(Rol rol) {
        Map<Long, List<Long>> permisos = new HashMap<>();
        rolModuloPermisoRepository.findByRolId(rol.getId()).forEach(rmp ->
                permisos.computeIfAbsent(rmp.getModulo().getId(), key -> new ArrayList<>()).add(rmp.getPermiso().getId()));
        List<AsignacionModuloDto> modulos = rolModuloRepository.findByRolId(rol.getId()).stream()
                .map(rm -> new AsignacionModuloDto(rm.getModulo().getId(),
                        permisos.getOrDefault(rm.getModulo().getId(), List.of()).stream().sorted().toList()))
                .sorted(Comparator.comparing(AsignacionModuloDto::moduloId))
                .toList();
        return new RolAdminResponseDto(rol.getId(), rol.getNombre(), rol.getDescripcion(),
                Boolean.TRUE.equals(rol.getActivo()), rol.getSistema().getId(), rol.getSistema().getNombre(), modulos);
    }

    private Rol getRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    private String normalizar(String nombre) {
        String value = nombre.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        return value.startsWith("ROLE_") ? value : "ROLE_" + value;
    }

    private void validarRolReservado(String nombre, boolean actorEsSuperAdmin) {
        if (AuthorizationService.SUPER_ADMIN_ROLE.equals(nombre) && !actorEsSuperAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un superadministrador puede modificar ese rol");
        }
    }
}
