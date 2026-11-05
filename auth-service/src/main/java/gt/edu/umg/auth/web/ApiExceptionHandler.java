package gt.edu.umg.auth.web;

import gt.edu.umg.auth.service.InvalidCredentialsException;
import gt.edu.umg.auth.service.InvalidTurnstileException;
import gt.edu.umg.auth.service.TurnstileUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> invalidCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("INVALID_CREDENTIALS", "Credenciales invalidas", 401));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<ApiError> rateLimited(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, Long.toString(exception.getRetryAfterSeconds()))
                .body(new ApiError("RATE_LIMIT_EXCEEDED", "Demasiadas solicitudes", 429));
    }

    @ExceptionHandler(InvalidTurnstileException.class)
    ResponseEntity<ApiError> invalidTurnstile() {
        return ResponseEntity.badRequest()
                .body(new ApiError("TURNSTILE_INVALID", "Verificacion de seguridad invalida", 400));
    }

    @ExceptionHandler(TurnstileUnavailableException.class)
    ResponseEntity<ApiError> turnstileUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("TURNSTILE_UNAVAILABLE", "Verificacion de seguridad no disponible", 503));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalidRequest() {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_REQUEST", "Solicitud invalida", 400));
    }

    public record ApiError(String code, String message, int status) {}
}
