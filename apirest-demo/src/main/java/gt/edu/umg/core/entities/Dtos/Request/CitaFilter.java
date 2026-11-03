package gt.edu.umg.core.entities.Dtos.Request;

import java.time.LocalDate;

public record CitaFilter(
        Long pacienteId,
        Long medicoId,
        String estado,
        LocalDate fecha
) {}
