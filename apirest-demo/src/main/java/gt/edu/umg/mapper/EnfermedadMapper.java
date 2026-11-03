package gt.edu.umg.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gt.edu.umg.core.entities.Enfermedad;
import gt.edu.umg.core.entities.Dtos.Request.EnfermedadRequestDto;
import gt.edu.umg.core.entities.Dtos.Response.EnfermedadResponseDto;

@Mapper(componentModel = "spring")
public interface EnfermedadMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", constant = "true")
	Enfermedad toEntity(EnfermedadRequestDto dto);

	EnfermedadResponseDto toDto(Enfermedad entity);
}
