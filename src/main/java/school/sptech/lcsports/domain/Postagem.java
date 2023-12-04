package school.sptech.lcsports.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "postagem")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idPostagem")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_postagem")
    private Integer idPostagem;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_postagem")
    private LocalDateTime dataPostagem = LocalDateTime.now();

    @Column(name = "visualizacoes")
    private Integer visualizacoes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", referencedColumnName = "id_usuario")
    @JsonIdentityReference(alwaysAsId = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "postagem")
    private List<CurtidaPostagem> curtidas;

    @OneToMany(mappedBy = "postagem")
    private List<Imagem> imagens;

    @ElementCollection
    private List<String> competencias;

    @ElementCollection
    private List<Integer> pessoasAtingidas;

    public Postagem(String titulo, String descricao, Usuario usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.usuario = usuario;
        this.imagens = new ArrayList<>();
        this.competencias = new ArrayList<>();
    }

    public Postagem(Postagem publicacao) {
    }
}

//private Integer visualizacoes = 0;

//private Integer pessoasAtingidas;
