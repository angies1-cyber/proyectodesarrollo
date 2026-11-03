package gt.edu.umg.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gt.edu.umg.core.entities.Paciente;
import gt.edu.umg.core.entities.Dtos.Request.PacienteFilter;
import gt.edu.umg.core.entities.Dtos.Request.PacienteRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.DashboardMetricasDto;
import gt.edu.umg.core.entities.Dtos.Response.PacienteResponseDto;
import gt.edu.umg.core.entities.Dtos.Response.PageMetadataDto;
import gt.edu.umg.core.entities.Dtos.Response.PageResponseDto;
import gt.edu.umg.dao.specification.PacienteSpecification;
import gt.edu.umg.mapper.PacienteMapper;
import gt.edu.umg.service.impl.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pacientes")
@Tag(name = "Pacientes", description = "Endpoints para la gestión de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class PacienteController {

	private final PacienteService pacienteService;
	private final PacienteMapper pacienteMapper;

	public PacienteController(PacienteService pacienteService, PacienteMapper pacienteMapper) {
		this.pacienteService = pacienteService;
		this.pacienteMapper = pacienteMapper;
	}

	@PostMapping
    @Operation(summary = "Crear nuevo paciente (disponible para operadores y administradores)")
    public ResponseEntity<PacienteResponseDto> crear(@Valid @RequestBody PacienteRequestDto request) {
        Paciente entidad = pacienteMapper.toEntity(request);
        Paciente guardado = pacienteService.guardar(entidad);
        return new ResponseEntity<>(pacienteMapper.toDto(guardado), HttpStatus.CREATED);
    }

	@GetMapping
    @Operation(summary = "Obtener lista paginada de pacientes con filtros")
    public ResponseEntity<PageResponseDto<PacienteResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String dpi,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        PacienteFilter filter = new PacienteFilter(nombre, apellido, dpi, telefono, correo, activo);
        Specification<Paciente> spec = PacienteSpecification.conFiltro(filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Paciente> result = pacienteService.obtenerPaginado(spec, pageable);

        List<PacienteResponseDto> data = result.getContent().stream()
                .map(pacienteMapper::toDto)
                .toList();

        PageMetadataDto metadata = new PageMetadataDto(
                result.getTotalElements(),
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.hasPrevious(),
                result.hasNext()
        );

        return ResponseEntity.ok(new PageResponseDto<>(data, metadata));
    }

    @GetMapping("/todos")
    @Operation(summary = "Obtener todos los pacientes activos para selects")
    public ResponseEntity<List<PacienteResponseDto>> obtenerTodos() {
        List<PacienteResponseDto> respuesta = pacienteService.obtenerTodos()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .map(pacienteMapper::toDto)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener métricas para el dashboard de inicio")
    public ResponseEntity<DashboardMetricasDto> dashboardMetricas() {
        return ResponseEntity.ok(pacienteService.obtenerMetricasDashboard());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un paciente por su ID")
    public ResponseEntity<PacienteResponseDto> obtenerPorId(@PathVariable Long id) {
        Paciente paciente = pacienteService.obtenerPorId(id);
        return ResponseEntity.ok(pacienteMapper.toDto(paciente));
    }

    @PatchMapping("/{id}/inhabilitar")
    @Operation(summary = "Inhabilitar paciente")
    public ResponseEntity<PacienteResponseDto> inhabilitar(@PathVariable Long id) {
        Paciente paciente = pacienteService.cambiarEstado(id, false);
        return ResponseEntity.ok(pacienteMapper.toDto(paciente));
    }

    @PatchMapping("/{id}/habilitar")
    @Operation(summary = "Habilitar paciente")
    public ResponseEntity<PacienteResponseDto> habilitar(@PathVariable Long id) {
        Paciente paciente = pacienteService.cambiarEstado(id, true);
        return ResponseEntity.ok(pacienteMapper.toDto(paciente));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un paciente por su ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
