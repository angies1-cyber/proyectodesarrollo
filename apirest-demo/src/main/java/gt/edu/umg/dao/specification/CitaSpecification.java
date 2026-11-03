package gt.edu.umg.dao.specification;

import gt.edu.umg.core.entities.Dtos.Request.CitaFilter;
import gt.edu.umg.core.entities.Cita;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CitaSpecification {

    public static Specification<Cita> conFiltro(CitaFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.pacienteId() != null) {
                predicates.add(cb.equal(root.get("paciente").get("id"), filter.pacienteId()));
            }

            if (filter.medicoId() != null) {
                predicates.add(cb.equal(root.get("medico").get("id"), filter.medicoId()));
            }

            if (filter.estado() != null && !filter.estado().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("estado")), filter.estado().trim().toUpperCase()));
            }

            if (filter.fecha() != null) {
                predicates.add(cb.equal(root.get("fecha"), filter.fecha()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
