package gt.edu.umg.service.impl;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.edu.umg.core.entities.Enfermedad;
import gt.edu.umg.dao.EnfermedadRepository;

@Service
public class EnfermedadServiceImpl implements EnfermedadService {

	private final EnfermedadRepository enfermedadRepository;

	public EnfermedadServiceImpl(EnfermedadRepository enfermedadRepository) {
		this.enfermedadRepository = enfermedadRepository;
	}

	@Override
	@Transactional
	public Enfermedad guardar(Enfermedad enfermedad) {
		return enfermedadRepository.guardar(enfermedad);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Enfermedad> obtenerTodos() {
		return enfermedadRepository.obtenerTodos();
	}

	@Override
	@Transactional(readOnly = true)
	public Enfermedad obtenerPorId(Long id) {
		return enfermedadRepository.obtenerPorId(id)
				.orElseThrow(() -> new RuntimeException("Enfermedad no encontrada con el ID: " + id));
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		enfermedadRepository.eliminarPorId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Enfermedad> buscarConFiltro(Specification<Enfermedad> spec) {
		return enfermedadRepository.findAll(spec);
	}

}
