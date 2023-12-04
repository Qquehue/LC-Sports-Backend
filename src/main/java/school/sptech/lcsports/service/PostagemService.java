package school.sptech.lcsports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.sptech.lcsports.domain.Imagem;
import school.sptech.lcsports.domain.Postagem;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.repository.ImagemRepository;
import school.sptech.lcsports.repository.PostagemRepository;
import school.sptech.lcsports.repository.UsuarioRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PostagemService {

    private PostagemRepository postagemRepository;
    private UsuarioRepository usuarioRepository;
    private ImagemRepository imagemRepository;

   @Autowired
    public PostagemService(PostagemRepository postagemRepository,
                           UsuarioRepository usuarioRepository,
                           ImagemRepository imagemRepository) {
        this.postagemRepository = postagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.imagemRepository = imagemRepository;
    }

    public Postagem adicionarPostagem(int idUsuario, Postagem novaPostagem) {

        Optional<Usuario> usuario = usuarioRepository.findByIdUsuario(idUsuario);

        if (usuario.isEmpty()) throw new NoSuchElementException();

        Postagem postagem = new Postagem(novaPostagem.getTitulo(), novaPostagem.getDescricao(), usuario.get());
        postagem.setCompetencias(novaPostagem.getCompetencias());

        postagemRepository.save(postagem);

        Usuario u = usuario.get();
        u.getPostagens().add(postagem);

        usuarioRepository.save(u);

        for (Imagem urlImagem : novaPostagem.getImagens()) {
            Imagem imagem = new Imagem(urlImagem.getCodigoImagem(), postagem);

            imagemRepository.save(imagem);

            postagem.getImagens().add(imagem);
        }

        return postagem;
    }

    public List<Postagem> listarPostagensFeed(){
        List<Postagem> postagensFeed = postagemRepository.findTop10ByOrderByDataPostagem();

        return postagensFeed;
    }
}
