package gt.edu.umg.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sistemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sistema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 150)
    private String dominio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
