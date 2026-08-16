package gt.edu.umg.auth.controller;

import gt.edu.umg.auth.dto.AuthResponseDto;
import gt.edu.umg.auth.dto.LoginRequestDto;
import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<AuthResponseDto> registrar(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = authService.registrar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
