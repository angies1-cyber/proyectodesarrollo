package gt.edu.umg.core.entities.Dtos.Request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PacienteRequestDto(
		@NotBlank(message = "El nombre es obligatorio")
	    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
	    String nombre,

	    @NotBlank(message = "El apellido es obligatorio")
	    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
	    String apellido,

	    @NotBlank(message = "El DPI es obligatorio")
	    @Pattern(regexp = "^\\d{13}$", message = "El DPI debe contener exactamente 13 dígitos")
	    String dpi,

	    @NotBlank(message = "El teléfono es obligatorio")
	    @Pattern(regexp = "^\\d{8}$", message = "El teléfono debe contener 8 dígitos")
	    String telefono,

	    @NotBlank(message = "El correo es obligatorio")
	    @Email(message = "Debe ser una dirección de correo válida")
	    String correo,

	    @NotNull(message = "La fecha de nacimiento es obligatoria")
	    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
	    LocalDate fechaNacimiento,

	    @NotBlank(message = "La dirección es obligatoria")
	    @Size(max = 150, message = "La dirección no debe exceder 150 caracteres")
	    String direccion,

	    @Pattern(regexp = "^$|^\\d{8}$", message = "El teléfono alterno debe contener 8 dígitos")
	    String telefonoAlterno
		) {}
