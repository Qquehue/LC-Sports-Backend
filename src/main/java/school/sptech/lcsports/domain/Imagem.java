package school.sptech.lcsports.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "imagem")
@NoArgsConstructor
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagem")
    private Integer idImagem;

    @Column(name = "codigo_imagem")
    private String codigoImagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_postagem", referencedColumnName = "id_postagem")
    @JsonIdentityReference(alwaysAsId = true)
    private Postagem postagem;

    public Imagem(String codigoImagem, Postagem postagem) {
        this.codigoImagem = codigoImagem;
        this.postagem = postagem;
    }
}
