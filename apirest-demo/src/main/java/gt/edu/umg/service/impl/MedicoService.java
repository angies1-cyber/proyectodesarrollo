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
import gt.edu.umg.core.entities.Medico;
import gt.edu.umg.dao.EspecialidadRepository;
import gt.edu.umg.dao.MedicoRepository;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional
    public Medico guardar(Medico medico, Long especialidadId) {
        if (medico.getId() == null && medicoRepository.existsByDpi(medico.getDpi())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un médico registrado con el DPI: " + medico.getDpi());
        }

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada con ID: " + especialidadId));

        medico.setEspecialidad(especialidad);
        if (medico.getActivo() == null) {
            medico.setActivo(true);
        }
        return medicoRepository.save(medico);
    }

    @Transactional(readOnly = true)
    public List<Medico> obtenerTodos() {
        return medicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Medico> obtenerPaginado(Specification<Medico> spec, Pageable pageable) {
        return medicoRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Medico obtenerPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado con ID: " + id);
        }
        medicoRepository.deleteById(id);
    }

    @Transactional
    public Medico cambiarEstado(Long id, boolean activo) {
        Medico medico = obtenerPorId(id);
        medico.setActivo(activo);
        return medicoRepository.save(medico);
    }
}
