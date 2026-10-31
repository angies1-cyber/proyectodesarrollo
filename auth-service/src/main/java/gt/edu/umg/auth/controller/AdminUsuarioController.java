package gt.edu.umg.auth.controller;

import gt.edu.umg.auth.dto.AdminCreateUsuarioRequestDto;
import gt.edu.umg.auth.dto.AdminEnabledRequestDto;
import gt.edu.umg.auth.dto.AdminRolesRequestDto;
import gt.edu.umg.auth.dto.AdminUsuarioResponseDto;
import gt.edu.umg.auth.service.AdminUsuarioService;
import gt.edu.umg.auth.service.AuthorizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class AdminUsuarioController {

    private final AdminUsuarioService adminUsuarioService;

    public AdminUsuarioController(AdminUsuarioService adminUsuarioService) {
        this.adminUsuarioService = adminUsuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIOS:READ') or hasAuthority('SUPER_ADMIN')")
    public List<AdminUsuarioResponseDto> listar() {
        return adminUsuarioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USUARIOS:CREATE') or hasAuthority('SUPER_ADMIN')")
    public AdminUsuarioResponseDto crear(@Valid @RequestBody AdminCreateUsuarioRequestDto request,
                                          Authentication authentication) {
        return adminUsuarioService.crear(request, isSuperAdmin(authentication));
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USUARIOS:UPDATE') or hasAuthority('SUPER_ADMIN')")
    public AdminUsuarioResponseDto cambiarRoles(@PathVariable Long id,
                                                 @Valid @RequestBody AdminRolesRequestDto request,
                                                 Authentication authentication) {
        return adminUsuarioService.cambiarRoles(id, request.roleIds(), authentication.getName(),
                isSuperAdmin(authentication));
    }

    @PatchMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('USUARIOS:UPDATE') or hasAuthority('SUPER_ADMIN')")
    public AdminUsuarioResponseDto cambiarEnabled(@PathVariable Long id,
                                                   @Valid @RequestBody AdminEnabledRequestDto request,
                                                   Authentication authentication) {
        return adminUsuarioService.cambiarEnabled(id, request.enabled(), authentication.getName());
    }

    @PostMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USUARIOS:RESET_PASSWORD') or hasAuthority('SUPER_ADMIN')")
    public void resetPassword(@PathVariable Long id,
                              @Valid @RequestBody gt.edu.umg.auth.dto.ResetPasswordRequestDto request,
                              Authentication authentication) {
        adminUsuarioService.resetPassword(id, request.newPassword());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USUARIOS:DELETE') or hasAuthority('SUPER_ADMIN')")
    public void eliminar(@PathVariable Long id, Authentication authentication) {
        adminUsuarioService.eliminar(id, authentication.getName());
    }

    private boolean isSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SUPER_ADMIN")
                        || a.getAuthority().equals(AuthorizationService.SUPER_ADMIN_ROLE));
    }
}
