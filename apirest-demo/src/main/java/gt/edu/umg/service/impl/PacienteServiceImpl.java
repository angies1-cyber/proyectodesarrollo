package gt.edu.umg.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import gt.edu.umg.core.entities.Paciente;
import gt.edu.umg.core.entities.Dtos.Response.DashboardMetricasDto;
import gt.edu.umg.dao.CitaRepository;
import gt.edu.umg.dao.MedicoRepository;
import gt.edu.umg.dao.PacienteRepository;

@Service
public class PacienteServiceImpl implements PacienteService {

	private final PacienteRepository pacienteRepository;
	private final MedicoRepository medicoRepository;
	private final CitaRepository citaRepository;

	public PacienteServiceImpl(PacienteRepository pacienteRepository,
							   MedicoRepository medicoRepository,
							   CitaRepository citaRepository) {
		this.pacienteRepository = pacienteRepository;
		this.medicoRepository = medicoRepository;
		this.citaRepository = citaRepository;
	}

	@Override
	@Transactional
	public Paciente guardar(Paciente paciente) {
		if (paciente.getFechaCreacion() == null) {
			paciente.setFechaCreacion(LocalDate.now());
		}
		if (paciente.getActivo() == null) {
			paciente.setActivo(true);
		}
		return pacienteRepository.save(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> obtenerTodos() {
		return pacienteRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Paciente> obtenerPaginado(Specification<Paciente> spec, Pageable pageable) {
		return pacienteRepository.findAll(spec, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Paciente obtenerPorId(Long id) {
		return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado con el ID: " + id));
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		if (!pacienteRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede eliminar. El paciente no existe con ID: " + id);
		}
        pacienteRepository.deleteById(id);
	}

	@Override
	@Transactional
	public Paciente cambiarEstado(Long id, boolean activo) {
		Paciente paciente = obtenerPorId(id);

		paciente.setActivo(activo);
		if (!activo) {
			paciente.setFechaInhabilitacion(LocalDate.now());
		} else {
			paciente.setFechaInhabilitacion(null);
		}

		return pacienteRepository.save(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> buscarConFiltro(Specification<Paciente> spec) {
		return pacienteRepository.findAll(spec);
	}

	@Override
	@Transactional(readOnly = true)
	public DashboardMetricasDto obtenerMetricasDashboard() {
		List<Paciente> pacientes = pacienteRepository.findAll();
		long totalPacientes = pacientes.size();
		long activos = pacientes.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count();
		long inactivos = totalPacientes - activos;

		Map<String, Long> rangosEdad = new HashMap<>();
		rangosEdad.put("0-17", 0L);
		rangosEdad.put("18-30", 0L);
		rangosEdad.put("31-50", 0L);
		rangosEdad.put("51-65", 0L);
		rangosEdad.put("65+", 0L);

		LocalDate now = LocalDate.now();
		for (Paciente p : pacientes) {
			if (p.getFechaNacimiento() != null) {
				int edad = Period.between(p.getFechaNacimiento(), now).getYears();
				if (edad < 18) rangosEdad.put("0-17", rangosEdad.get("0-17") + 1);
				else if (edad <= 30) rangosEdad.put("18-30", rangosEdad.get("18-30") + 1);
				else if (edad <= 50) rangosEdad.put("31-50", rangosEdad.get("31-50") + 1);
				else if (edad <= 65) rangosEdad.put("51-65", rangosEdad.get("51-65") + 1);
				else rangosEdad.put("65+", rangosEdad.get("65+") + 1);
			}
		}

		long totalMedicos = medicoRepository != null ? medicoRepository.count() : 0L;
		long totalCitas = citaRepository != null ? citaRepository.count() : 0L;

		return new DashboardMetricasDto(totalPacientes, activos, inactivos, rangosEdad, totalMedicos, totalCitas);
	}
}
