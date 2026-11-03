package gt.edu.umg.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gt.edu.umg.core.entities.Especialidad;

@Repository
public interface EspecialidadRepository extends IGenericRepository<Especialidad, Long>, JpaSpecificationExecutor<Especialidad> {
    boolean existsByNombreIgnoreCase(String nombre);
}
