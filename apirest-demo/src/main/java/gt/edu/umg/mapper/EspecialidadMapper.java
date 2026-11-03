package gt.edu.umg.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gt.edu.umg.core.entities.Especialidad;
import gt.edu.umg.core.entities.Dtos.Request.EspecialidadRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.EspecialidadResponseDto;

@Mapper(componentModel = "spring")
public interface EspecialidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Especialidad toEntity(EspecialidadRequestDto dto);

    EspecialidadResponseDto toDto(Especialidad entity);
}
