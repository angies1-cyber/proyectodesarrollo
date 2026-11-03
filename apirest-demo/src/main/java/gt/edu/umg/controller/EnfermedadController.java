package gt.edu.umg.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.umg.core.entities.Enfermedad;
import gt.edu.umg.core.entities.Dtos.Request.EnfermedadRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.EnfermedadResponseDto;
import gt.edu.umg.mapper.EnfermedadMapper;
import gt.edu.umg.service.impl.EnfermedadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/enfermedades")
@Tag(name = "Enfermedades", description = "Endpoints para la gestión de enfermedades")
@SecurityRequirement(name = "bearerAuth")
public class EnfermedadController {

	private final EnfermedadService enfermedadService;
	private final EnfermedadMapper enfermedadMapper;

	public EnfermedadController(EnfermedadService enfermedadService, EnfermedadMapper enfermedadMapper) {
		this.enfermedadService = enfermedadService;
		this.enfermedadMapper = enfermedadMapper;
	}

	@PostMapping
	@Operation(summary = "Crear una nueva enfermedad")
	public ResponseEntity<EnfermedadResponseDto> crear(@Valid @RequestBody EnfermedadRequestDto request) {
		Enfermedad entidad = enfermedadMapper.toEntity(request);
		Enfermedad guardada = enfermedadService.guardar(entidad);
		return new ResponseEntity<>(enfermedadMapper.toDto(guardada), HttpStatus.CREATED);
	}

	@GetMapping
	@Operation(summary = "Obtener lista de todas las enfermedades")
	public ResponseEntity<List<EnfermedadResponseDto>> obtenerTodas() {
		List<EnfermedadResponseDto> respuesta = enfermedadService.obtenerTodos()
				.stream()
				.map(enfermedadMapper::toDto)
				.toList();

		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener una enfermedad por su ID")
	public ResponseEntity<EnfermedadResponseDto> obtenerPorId(@PathVariable Long id) {
		Enfermedad enfermedad = enfermedadService.obtenerPorId(id);
		return ResponseEntity.ok(enfermedadMapper.toDto(enfermedad));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar una enfermedad por su ID")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		enfermedadService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
