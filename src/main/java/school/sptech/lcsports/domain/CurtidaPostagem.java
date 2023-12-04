package school.sptech.lcsports.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "curtida_postagem")
@NoArgsConstructor
public class CurtidaPostagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curtida")
    private Integer idCurtida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id_usuario")
    @JsonIdentityReference(alwaysAsId = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_postagem", referencedColumnName = "id_postagem")
    @JsonIdentityReference(alwaysAsId = true)
    private Postagem postagem;

    public CurtidaPostagem(Usuario usuario, Postagem postagem) {
        this.usuario = usuario;
        this.postagem = postagem;
    }
}
