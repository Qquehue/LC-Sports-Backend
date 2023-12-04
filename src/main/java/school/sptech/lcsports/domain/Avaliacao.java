package school.sptech.lcsports.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Integer idAvaliacao;

    @Column(name = "nota")
    private Integer nota;

    @ManyToOne
    @JoinColumn(name = "fk_avaliador", referencedColumnName = "id_usuario")
    private Usuario avaliador;

    @ManyToOne
    @JoinColumn(name = "fk_avaliado", referencedColumnName = "id_usuario")
    private Usuario avaliado;

}
