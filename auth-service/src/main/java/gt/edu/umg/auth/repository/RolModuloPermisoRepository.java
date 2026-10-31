package gt.edu.umg.auth.repository;

import gt.edu.umg.auth.entity.RolModuloPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolModuloPermisoRepository extends JpaRepository<RolModuloPermiso, Long> {
    List<RolModuloPermiso> findByRolIdIn(Collection<Long> rolIds);
    List<RolModuloPermiso> findByRolId(Long rolId);
    Optional<RolModuloPermiso> findByRolIdAndModuloIdAndPermisoId(Long rolId, Long moduloId, Long permisoId);
    void deleteByRolId(Long rolId);
}
