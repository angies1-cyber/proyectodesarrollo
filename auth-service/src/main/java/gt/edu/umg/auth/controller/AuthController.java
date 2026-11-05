package gt.edu.umg.auth.controller;

import gt.edu.umg.auth.dto.AuthResponseDto;
import gt.edu.umg.auth.dto.LoginRequestDto;
import gt.edu.umg.auth.dto.RegisterRequestDto;
import gt.edu.umg.auth.service.AuthService;
import gt.edu.umg.auth.service.TurnstileValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y registro de usuarios")
public class AuthController {

    private final AuthService authService;
    private final TurnstileValidationService turnstileValidationService;

    public AuthController(AuthService authService, TurnstileValidationService turnstileValidationService) {
        this.authService = authService;
        this.turnstileValidationService = turnstileValidationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                  HttpServletRequest httpRequest) {
        turnstileValidationService.validate(request.turnstileToken(), httpRequest.getRemoteAddr());
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
