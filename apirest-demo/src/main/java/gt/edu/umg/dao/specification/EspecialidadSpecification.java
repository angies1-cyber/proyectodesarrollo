package gt.edu.umg.dao.specification;

import gt.edu.umg.core.entities.Dtos.Request.EspecialidadFilter;
import gt.edu.umg.core.entities.Especialidad;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EspecialidadSpecification {

    public static Specification<Especialidad> conFiltro(EspecialidadFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.nombre() != null && !filter.nombre().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filter.nombre().trim().toLowerCase() + "%"));
            }

            if (filter.activo() != null) {
                predicates.add(cb.equal(root.get("activo"), filter.activo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
