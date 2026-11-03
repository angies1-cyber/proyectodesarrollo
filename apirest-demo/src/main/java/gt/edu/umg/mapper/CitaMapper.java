package gt.edu.umg.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gt.edu.umg.core.entities.Cita;
import gt.edu.umg.core.entities.Dtos.Request.CitaRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.CitaResponseDto;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "medico", ignore = true)
    @Mapping(target = "estado", constant = "PROGRAMADA")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "fechaModificacion", ignore = true)
    Cita toEntity(CitaRequestDto dto);

    @Mapping(target = "pacienteId", source = "paciente.id")
    @Mapping(target = "pacienteNombre", source = "paciente.nombre")
    @Mapping(target = "pacienteApellido", source = "paciente.apellido")
    @Mapping(target = "pacienteDpi", source = "paciente.dpi")
    @Mapping(target = "pacienteTelefono", source = "paciente.telefono")
    @Mapping(target = "medicoId", source = "medico.id")
    @Mapping(target = "medicoNombre", source = "medico.nombre")
    @Mapping(target = "medicoApellido", source = "medico.apellido")
    @Mapping(target = "especialidadNombre", source = "medico.especialidad.nombre")
    CitaResponseDto toDto(Cita entity);
}
