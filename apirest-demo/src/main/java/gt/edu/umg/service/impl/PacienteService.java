package gt.edu.umg.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import gt.edu.umg.core.entities.Paciente;
import gt.edu.umg.core.entities.Dtos.Response.DashboardMetricasDto;

public interface PacienteService {
	Paciente guardar(Paciente paciente);
    List<Paciente> obtenerTodos();
    Page<Paciente> obtenerPaginado(Specification<Paciente> spec, Pageable pageable);
    Paciente obtenerPorId(Long id);
    void eliminar(Long id);
    Paciente cambiarEstado(Long id, boolean activo);
    List<Paciente> buscarConFiltro(Specification<Paciente> spec);
    DashboardMetricasDto obtenerMetricasDashboard();
}
