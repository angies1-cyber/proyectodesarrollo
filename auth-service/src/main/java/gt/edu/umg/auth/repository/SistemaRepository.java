package gt.edu.umg.auth.repository;

import gt.edu.umg.auth.entity.Sistema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SistemaRepository extends JpaRepository<Sistema, Long> {
    Optional<Sistema> findByCodigo(String codigo);
}
