package gt.edu.umg.mapper;

import java.time.LocalDate;
import java.time.Period;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gt.edu.umg.core.entities.Medico;
import gt.edu.umg.core.entities.Dtos.Request.MedicoRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.MedicoResponseDto;

@Mapper(componentModel = "spring")
public interface MedicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "especialidad", ignore = true)
    Medico toEntity(MedicoRequestDto dto);

    @Mapping(target = "edad", expression = "java(calcularEdad(entity.getFechaNacimiento()))")
    @Mapping(target = "especialidadId", source = "especialidad.id")
    @Mapping(target = "especialidadNombre", source = "especialidad.nombre")
    MedicoResponseDto toDto(Medico entity);

    default Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
