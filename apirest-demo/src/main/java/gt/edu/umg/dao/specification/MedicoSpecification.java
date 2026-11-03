package gt.edu.umg.dao.specification;

import gt.edu.umg.core.entities.Dtos.Request.MedicoFilter;
import gt.edu.umg.core.entities.Medico;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MedicoSpecification {

    public static Specification<Medico> conFiltro(MedicoFilter filter) {
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

            if (filter.especialidadId() != null) {
                predicates.add(cb.equal(root.get("especialidad").get("id"), filter.especialidadId()));
            }

            if (filter.activo() != null) {
                predicates.add(cb.equal(root.get("activo"), filter.activo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
