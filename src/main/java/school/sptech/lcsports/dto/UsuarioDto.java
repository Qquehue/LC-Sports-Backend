package school.sptech.lcsports.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto {

    private Integer idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cep;
    private String cidade;
    private String uf;
    private String biografia;
    private Boolean isPremium;

}
