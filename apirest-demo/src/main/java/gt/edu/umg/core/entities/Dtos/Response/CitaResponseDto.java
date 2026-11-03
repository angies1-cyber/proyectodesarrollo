package gt.edu.umg.core.entities.Dtos.Response;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaResponseDto(
        Long id,
        Long pacienteId,
        String pacienteNombre,
        String pacienteApellido,
        String pacienteDpi,
        String pacienteTelefono,
        Long medicoId,
        String medicoNombre,
        String medicoApellido,
        String especialidadNombre,
        LocalDate fecha,
        LocalTime hora,
        String motivo,
        String estado,
        LocalDate fechaCreacion,
        LocalDate fechaModificacion
) {}
