package gt.edu.umg.auth.repository;

import gt.edu.umg.auth.entity.RolModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolModuloRepository extends JpaRepository<RolModulo, Long> {
    List<RolModulo> findByRolIdIn(Collection<Long> rolIds);
    List<RolModulo> findByRolId(Long rolId);
    Optional<RolModulo> findByRolIdAndModuloId(Long rolId, Long moduloId);
    void deleteByRolId(Long rolId);
}
