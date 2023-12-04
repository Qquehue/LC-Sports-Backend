package school.sptech.lcsports.dto;

import lombok.Getter;
import lombok.Setter;
import school.sptech.lcsports.domain.Usuario;

@Getter
@Setter
public class UsuarioCriacaoDto {
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private String cep;
    private String cidade;
    private String uf;

    public Usuario toUsuario(UsuarioCriacaoDto usuarioCriacaoDto){
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioCriacaoDto.getNome());
        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setSenha(usuarioCriacaoDto.getSenha());
        usuario.setCpf(usuarioCriacaoDto.getCpf());
        usuario.setTelefone(usuarioCriacaoDto.getTelefone());
        usuario.setCep(usuarioCriacaoDto.getCep());
        usuario.setCidade(usuarioCriacaoDto.getCidade());
        usuario.setUf(usuarioCriacaoDto.getUf());

        return usuario;
    }
}
