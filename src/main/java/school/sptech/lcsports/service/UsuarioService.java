package school.sptech.lcsports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.dto.UsuarioCriacaoDto;
import school.sptech.lcsports.repository.UsuarioRepository;
import school.sptech.lcsports.security.Token;
import school.sptech.lcsports.security.TokenUtil;

import java.util.Optional;

@Service
public class UsuarioService {

    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarUsuarioPorId(int idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Usuario editarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuario(UsuarioCriacaoDto usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com esse email");
        }
        String encoder = this.passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(encoder);
        Usuario usuarioCriado = usuario.toUsuario(usuario);
        return usuarioRepository.save(usuarioCriado);
    }

    public Token gerarToken(Usuario usuario) {
        Optional<Usuario> user = usuarioRepository.findByEmail(usuario.getEmail());
        if (user.isPresent()) {
            Boolean valid = passwordEncoder.matches(usuario.getSenha(), user.get().getSenha());
            if (valid) {
                return new Token(TokenUtil.createToken(user.get()));
            }
        }
        return null;
    }

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

}
