package gt.edu.umg.core.entities.Dtos.Response;

import java.util.Map;

public record DashboardMetricasDto(
        long totalPacientes,
        long pacientesActivos,
        long pacientesInactivos,
        Map<String, Long> rangosEdad,
        long totalMedicos,
        long totalCitas
) {}
