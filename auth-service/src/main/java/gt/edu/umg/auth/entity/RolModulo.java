package gt.edu.umg.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles_modulos", uniqueConstraints = @UniqueConstraint(columnNames = {"rol_id", "modulo_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolModulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;
}
