package gt.edu.umg.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gt.edu.umg.core.entities.Cita;
import gt.edu.umg.core.entities.Dtos.Request.CitaFilter;
import gt.edu.umg.core.entities.Dtos.Request.CitaReprogramarDto;
import gt.edu.umg.core.entities.Dtos.Request.CitaRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.CitaResponseDto;
import gt.edu.umg.core.entities.Dtos.Response.PageMetadataDto;
import gt.edu.umg.core.entities.Dtos.Response.PageResponseDto;
import gt.edu.umg.dao.specification.CitaSpecification;
import gt.edu.umg.mapper.CitaMapper;
import gt.edu.umg.service.impl.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Endpoints para la gestión de citas médicas")
@SecurityRequirement(name = "bearerAuth")
public class CitaController {

    private final CitaService citaService;
    private final CitaMapper citaMapper;

    public CitaController(CitaService citaService, CitaMapper citaMapper) {
        this.citaService = citaService;
        this.citaMapper = citaMapper;
    }

    @PostMapping
    @Operation(summary = "Crear/Agendar nueva cita")
    public ResponseEntity<CitaResponseDto> crear(@Valid @RequestBody CitaRequestDto request) {
        Cita entidad = citaMapper.toEntity(request);
        Cita guardada = citaService.crear(entidad, request.pacienteId(), request.medicoId());
        return new ResponseEntity<>(citaMapper.toDto(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener lista paginada de citas con filtros")
    public ResponseEntity<PageResponseDto<CitaResponseDto>> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        CitaFilter filter = new CitaFilter(pacienteId, medicoId, estado, fecha);
        Specification<Cita> spec = CitaSpecification.conFiltro(filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Cita> result = citaService.obtenerPaginado(spec, pageable);

        List<CitaResponseDto> data = result.getContent().stream()
                .map(citaMapper::toDto)
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

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID")
    public ResponseEntity<CitaResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaMapper.toDto(citaService.obtenerPorId(id)));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita")
    public ResponseEntity<CitaResponseDto> cancelar(@PathVariable Long id) {
        Cita cancelada = citaService.cancelar(id);
        return ResponseEntity.ok(citaMapper.toDto(cancelada));
    }

    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar fecha y hora de una cita")
    public ResponseEntity<CitaResponseDto> reprogramar(@PathVariable Long id, @Valid @RequestBody CitaReprogramarDto dto) {
        Cita reprogramada = citaService.reprogramar(id, dto);
        return ResponseEntity.ok(citaMapper.toDto(reprogramada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
