package school.sptech.lcsports.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "usuario")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idUsuario")
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "cpf")
    private String cpf = "91426723849";

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "cep")
    private String cep;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "uf")
    private String uf;

    @Column(name = "biografia")
    private String biografia;

    @Column(name = "isPremium")
    private Boolean isPremium = false;

    @OneToMany(mappedBy = "usuario")
    private List<CurtidaPostagem> curtidasPostagens;

    @Column(name = "imagens")
    private String fotoUsuario;

    @OneToMany(mappedBy = "usuario")
    private List<Postagem> postagens;

    public Usuario(String nome, String email, String senha, String cpf,String telefone, String cep, String cidade, String uf, Boolean isPremium) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.telefone = telefone;
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
        this.isPremium = isPremium;
    }
}