package gt.edu.umg.core.entities.Dtos.Request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record CitaReprogramarDto(
        @NotNull(message = "La nueva fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La nueva hora es obligatoria")
        LocalTime hora
) {}
