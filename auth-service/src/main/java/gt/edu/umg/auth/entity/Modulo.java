package gt.edu.umg.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modulos", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "sistema_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 180)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sistema_id", nullable = false)
    private Sistema sistema;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modulo_padre_id")
    private Modulo moduloPadre;

    @Column(length = 150)
    private String ruta;

    @Column(length = 50)
    private String icono;

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
