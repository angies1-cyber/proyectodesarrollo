package gt.edu.umg.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gt.edu.umg.core.entities.Medico;

@Repository
public interface MedicoRepository extends IGenericRepository<Medico, Long>, JpaSpecificationExecutor<Medico> {
    boolean existsByDpi(String dpi);
}
