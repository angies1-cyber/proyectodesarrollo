package gt.edu.umg.auth.repository;

import gt.edu.umg.auth.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    Optional<Modulo> findByCodigoAndSistemaId(String codigo, Long sistemaId);
    List<Modulo> findAllByOrderByOrdenAscNombreAsc();
}
