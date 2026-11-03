package gt.edu.umg.core.entities.Dtos.Response;

import java.time.LocalDate;


public record PacienteResponseDto(
		Long id,
	    String nombre,
	    String apellido,
	    String dpi,
	    String telefono,
	    String correo,
	    LocalDate fechaNacimiento,
	    Integer edad,
	    Boolean activo,
	    String direccion,
	    LocalDate fechaCreacion,
	    LocalDate fechaInhabilitacion,
	    String telefonoAlterno
		) {}
