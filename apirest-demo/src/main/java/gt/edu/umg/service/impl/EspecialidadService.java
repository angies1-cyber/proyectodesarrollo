package gt.edu.umg.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import gt.edu.umg.core.entities.Especialidad;
import gt.edu.umg.dao.EspecialidadRepository;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional
    public Especialidad guardar(Especialidad especialidad) {
        if (especialidad.getId() == null && especialidadRepository.existsByNombreIgnoreCase(especialidad.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una especialidad con ese nombre");
        }
        if (especialidad.getActivo() == null) {
            especialidad.setActivo(true);
        }
        return especialidadRepository.save(especialidad);
    }

    @Transactional(readOnly = true)
    public List<Especialidad> obtenerTodos() {
        return especialidadRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Especialidad> obtenerPaginado(Specification<Especialidad> spec, Pageable pageable) {
        return especialidadRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Especialidad obtenerPorId(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!especialidadRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada con ID: " + id);
        }
        especialidadRepository.deleteById(id);
    }

    @Transactional
    public Especialidad cambiarEstado(Long id, boolean activo) {
        Especialidad especialidad = obtenerPorId(id);
        especialidad.setActivo(activo);
        return especialidadRepository.save(especialidad);
    }
}
