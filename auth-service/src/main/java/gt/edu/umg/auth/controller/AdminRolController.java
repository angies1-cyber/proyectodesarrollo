package gt.edu.umg.auth.controller;

import gt.edu.umg.auth.dto.CatalogoSeguridadDto;
import gt.edu.umg.auth.dto.RolAdminRequestDto;
import gt.edu.umg.auth.dto.RolAdminResponseDto;
import gt.edu.umg.auth.service.AdminRolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
public class AdminRolController {
    private final AdminRolService service;

    public AdminRolController(AdminRolService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLES:READ') or hasAuthority('USUARIOS:READ') or hasAuthority('SUPER_ADMIN')")
    public List<RolAdminResponseDto> listar() {
        return service.listar();
    }

    @GetMapping("/catalogo")
    @PreAuthorize("hasAuthority('ROLES:READ') or hasAuthority('SUPER_ADMIN')")
    public CatalogoSeguridadDto catalogo() {
        return service.catalogo();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLES:CREATE') or hasAuthority('SUPER_ADMIN')")
    public RolAdminResponseDto crear(@Valid @RequestBody RolAdminRequestDto request, Authentication auth) {
        return service.crear(request, isSuperAdmin(auth));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLES:UPDATE') or hasAuthority('SUPER_ADMIN')")
    public RolAdminResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody RolAdminRequestDto request,
                                           Authentication auth) {
        return service.actualizar(id, request, isSuperAdmin(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLES:DELETE') or hasAuthority('SUPER_ADMIN')")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        service.eliminar(id, isSuperAdmin(auth));
    }

    private boolean isSuperAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SUPER_ADMIN")
                || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }
}
