package school.sptech.lcsports.dto;

import lombok.Getter;
import lombok.Setter;
import school.sptech.lcsports.domain.Postagem;

@Getter
@Setter
public class PostagemFeedDto {

    private int idUsuario;
    private String nomeUsuario;
    private String imagemPerfil;

    public PostagemFeedDto(Postagem postagem) {
        this.idUsuario = postagem.getIdPostagem();
        this.nomeUsuario = postagem.getUsuario().getNome();
        this.imagemPerfil = null;
    }
}
