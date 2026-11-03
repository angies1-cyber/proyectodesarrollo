package gt.edu.umg.dao.specification;

import gt.edu.umg.core.entities.Dtos.Request.PacienteFilter;
import gt.edu.umg.core.entities.Paciente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PacienteSpecification {

    public static Specification<Paciente> conFiltro(PacienteFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.nombre() != null && !filter.nombre().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filter.nombre().trim().toLowerCase() + "%"));
            }

            if (filter.apellido() != null && !filter.apellido().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("apellido")), "%" + filter.apellido().trim().toLowerCase() + "%"));
            }

            if (filter.dpi() != null && !filter.dpi().trim().isEmpty()) {
                predicates.add(cb.like(root.get("dpi"), "%" + filter.dpi().trim() + "%"));
            }

            if (filter.telefono() != null && !filter.telefono().trim().isEmpty()) {
                predicates.add(cb.like(root.get("telefono"), "%" + filter.telefono().trim() + "%"));
            }

            if (filter.correo() != null && !filter.correo().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("correo")), "%" + filter.correo().trim().toLowerCase() + "%"));
            }

            if (filter.activo() != null) {
                predicates.add(cb.equal(root.get("activo"), filter.activo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
