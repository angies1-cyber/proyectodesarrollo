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

import gt.edu.umg.core.entities.Especialidad;
import gt.edu.umg.core.entities.Dtos.Request.EspecialidadFilter;
import gt.edu.umg.core.entities.Dtos.Request.EspecialidadRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.EspecialidadResponseDto;
import gt.edu.umg.core.entities.Dtos.Response.PageMetadataDto;
import gt.edu.umg.core.entities.Dtos.Response.PageResponseDto;
import gt.edu.umg.dao.specification.EspecialidadSpecification;
import gt.edu.umg.mapper.EspecialidadMapper;
import gt.edu.umg.service.impl.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/especialidades")
@Tag(name = "Especialidades", description = "Endpoints para la gestión de especialidades médicas")
@SecurityRequirement(name = "bearerAuth")
public class EspecialidadController {

    private final EspecialidadService especialidadService;
    private final EspecialidadMapper especialidadMapper;

    public EspecialidadController(EspecialidadService especialidadService, EspecialidadMapper especialidadMapper) {
        this.especialidadService = especialidadService;
        this.especialidadMapper = especialidadMapper;
    }

    @PostMapping
    @Operation(summary = "Crear nueva especialidad")
    public ResponseEntity<EspecialidadResponseDto> crear(@Valid @RequestBody EspecialidadRequestDto request) {
        Especialidad entidad = especialidadMapper.toEntity(request);
        Especialidad guardada = especialidadService.guardar(entidad);
        return new ResponseEntity<>(especialidadMapper.toDto(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener lista paginada de especialidades")
    public ResponseEntity<PageResponseDto<EspecialidadResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        EspecialidadFilter filter = new EspecialidadFilter(nombre, activo);
        Specification<Especialidad> spec = EspecialidadSpecification.conFiltro(filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Especialidad> result = especialidadService.obtenerPaginado(spec, pageable);

        List<EspecialidadResponseDto> data = result.getContent().stream()
                .map(especialidadMapper::toDto)
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

    @GetMapping("/todas")
    @Operation(summary = "Obtener todas las especialidades activas para selects")
    public ResponseEntity<List<EspecialidadResponseDto>> obtenerTodas() {
        List<EspecialidadResponseDto> lista = especialidadService.obtenerTodos().stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                .map(especialidadMapper::toDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener especialidad por ID")
    public ResponseEntity<EspecialidadResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadMapper.toDto(especialidadService.obtenerPorId(id)));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado activo/inactivo de especialidad")
    public ResponseEntity<EspecialidadResponseDto> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        Especialidad modificada = especialidadService.cambiarEstado(id, activo);
        return ResponseEntity.ok(especialidadMapper.toDto(modificada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar especialidad por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
