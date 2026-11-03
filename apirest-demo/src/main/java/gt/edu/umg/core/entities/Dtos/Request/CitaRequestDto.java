package gt.edu.umg.core.entities.Dtos.Request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CitaRequestDto(
        @NotNull(message = "El ID del paciente es obligatorio")
        Long pacienteId,

        @NotNull(message = "El ID del médico es obligatorio")
        Long medicoId,

        @NotNull(message = "La fecha de la cita es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora de la cita es obligatoria")
        LocalTime hora,

        @NotBlank(message = "El motivo de la cita es obligatorio")
        @Size(max = 255, message = "El motivo no debe exceder 255 caracteres")
        String motivo
) {}
