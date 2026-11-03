package gt.edu.umg.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import gt.edu.umg.core.entities.Cita;
import gt.edu.umg.core.entities.Medico;
import gt.edu.umg.core.entities.Paciente;
import gt.edu.umg.core.entities.Dtos.Request.CitaReprogramarDto;
import gt.edu.umg.dao.CitaRepository;
import gt.edu.umg.dao.MedicoRepository;
import gt.edu.umg.dao.PacienteRepository;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaService(CitaRepository citaRepository,
                       PacienteRepository pacienteRepository,
                       MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public Cita crear(Cita cita, Long pacienteId, Long medicoId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado con ID: " + pacienteId));

        if (!Boolean.TRUE.equals(paciente.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede agendar una cita para un paciente inhabilitado.");
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado con ID: " + medicoId));

        if (!Boolean.TRUE.equals(medico.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede agendar una cita con un médico inhabilitado.");
        }

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setEstado("PROGRAMADA");
        cita.setFechaCreacion(LocalDate.now());

        return citaRepository.save(cita);
    }

    @Transactional(readOnly = true)
    public Page<Cita> obtenerPaginado(Specification<Cita> spec, Pageable pageable) {
        return citaRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada con ID: " + id));
    }

    @Transactional
    public Cita cancelar(Long id) {
        Cita cita = obtenerPorId(id);
        if ("CANCELADA".equalsIgnoreCase(cita.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cita ya se encuentra cancelada.");
        }
        cita.setEstado("CANCELADA");
        cita.setFechaModificacion(LocalDate.now());
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita reprogramar(Long id, CitaReprogramarDto dto) {
        Cita cita = obtenerPorId(id);
        if ("CANCELADA".equalsIgnoreCase(cita.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede reprogramar una cita cancelada.");
        }
        cita.setFecha(dto.fecha());
        cita.setHora(dto.hora());
        cita.setEstado("REPROGRAMADA");
        cita.setFechaModificacion(LocalDate.now());
        return citaRepository.save(cita);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }
}
