package gt.edu.umg.service.impl;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import gt.edu.umg.core.entities.Enfermedad;

public interface EnfermedadService {
	Enfermedad guardar(Enfermedad enfermedad);
	List<Enfermedad> obtenerTodos();
	Enfermedad obtenerPorId(Long id);
	void eliminar(Long id);

	List<Enfermedad> buscarConFiltro(Specification<Enfermedad> spec);
}
