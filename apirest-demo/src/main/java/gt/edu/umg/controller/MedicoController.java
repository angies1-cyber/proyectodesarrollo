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

import gt.edu.umg.core.entities.Medico;
import gt.edu.umg.core.entities.Dtos.Request.MedicoFilter;
import gt.edu.umg.core.entities.Dtos.Request.MedicoRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.MedicoResponseDto;
import gt.edu.umg.core.entities.Dtos.Response.PageMetadataDto;
import gt.edu.umg.core.entities.Dtos.Response.PageResponseDto;
import gt.edu.umg.dao.specification.MedicoSpecification;
import gt.edu.umg.mapper.MedicoMapper;
import gt.edu.umg.service.impl.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/medicos")
@Tag(name = "Médicos", description = "Endpoints para la gestión de médicos")
@SecurityRequirement(name = "bearerAuth")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;

    public MedicoController(MedicoService medicoService, MedicoMapper medicoMapper) {
        this.medicoService = medicoService;
        this.medicoMapper = medicoMapper;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo médico")
    public ResponseEntity<MedicoResponseDto> crear(@Valid @RequestBody MedicoRequestDto request) {
        Medico entidad = medicoMapper.toEntity(request);
        Medico guardado = medicoService.guardar(entidad, request.especialidadId());
        return new ResponseEntity<>(medicoMapper.toDto(guardado), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener lista paginada de médicos con filtros")
    public ResponseEntity<PageResponseDto<MedicoResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String dpi,
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        MedicoFilter filter = new MedicoFilter(nombre, apellido, dpi, especialidadId, activo);
        Specification<Medico> spec = MedicoSpecification.conFiltro(filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Medico> result = medicoService.obtenerPaginado(spec, pageable);

        List<MedicoResponseDto> data = result.getContent().stream()
                .map(medicoMapper::toDto)
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
    @Operation(summary = "Obtener todos los médicos activos para selects")
    public ResponseEntity<List<MedicoResponseDto>> obtenerTodos() {
        List<MedicoResponseDto> lista = medicoService.obtenerTodos().stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .map(medicoMapper::toDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener médico por ID")
    public ResponseEntity<MedicoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoMapper.toDto(medicoService.obtenerPorId(id)));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado activo/inactivo de médico")
    public ResponseEntity<MedicoResponseDto> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        Medico modificado = medicoService.cambiarEstado(id, activo);
        return ResponseEntity.ok(medicoMapper.toDto(modificado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar médico por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
