package gt.edu.umg.core.entities.Dtos.Response;

public record EnfermedadResponseDto(
		Long id,
		String nombre,
		String descripcion,
		Boolean activo
		) {}
