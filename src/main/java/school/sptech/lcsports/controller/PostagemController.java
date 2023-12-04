package school.sptech.lcsports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.lcsports.domain.Postagem;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.dto.PostagemFeedDto;
import school.sptech.lcsports.dto.UsuarioDto;
import school.sptech.lcsports.repository.CurtidaRepository;
import school.sptech.lcsports.repository.ImagemRepository;
import school.sptech.lcsports.repository.PostagemRepository;
import school.sptech.lcsports.repository.UsuarioRepository;
import school.sptech.lcsports.service.PostagemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/postagens")
@CrossOrigin(origins = "*")
public class PostagemController {

    private ImagemRepository imagemRepository;
    private PostagemService postagemService;
    private CurtidaRepository curtidaRepository;
    private PostagemRepository postagemRepository;

    private UsuarioRepository usuarioRepository;

    @Autowired
    public PostagemController(ImagemRepository imagemRepository,
                              PostagemService postagemService,
                              PostagemRepository postagemRepository,
                              ResourceLoader resourceLoader,
                              UsuarioRepository usuarioRepository) {
        this.imagemRepository = imagemRepository;
        this.postagemService = postagemService;
        this.postagemRepository = postagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<Postagem> adicionarPostagem(@PathVariable int idUsuario,
                                                         @RequestBody Postagem postagem) {

        Postagem publicacao = postagemService.adicionarPostagem(idUsuario, postagem);

        return ResponseEntity.created(null).body(new Postagem(publicacao));
    }

    @GetMapping()
    public ResponseEntity<List<Postagem>> listarPostagens() {

        List<Postagem> post = postagemRepository.findAll();

        if (post.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(post);
    }

    @GetMapping("/premium/{idPostagem}/{idUsuario}")
    public ResponseEntity<Postagem> listarPostagemPorId(@PathVariable Integer idPostagem, @PathVariable Integer idUsuario) {

        Optional<Postagem> post = postagemRepository.findById(idPostagem);
        if( post.isEmpty() ){
            return ResponseEntity.status(400).build();
        }
        Postagem postagem = post.get();

        Optional<Usuario> vizualizador = usuarioRepository.findById(idUsuario);
        Usuario usuario = vizualizador.get();

        Integer idVerificar = postagem.getUsuario().getIdUsuario();

        postagem.setVisualizacoes(postagem.getVisualizacoes()+1);

        if(!postagem.getPessoasAtingidas().contains(idUsuario)){
            postagem.getPessoasAtingidas().add(idUsuario);
        }
        postagemRepository.save(postagem);

        return ResponseEntity.status(200).body(postagem);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostagemFeedDto>> listarPostagensFeed() {
        List<Postagem> postagensFeed = postagemService.listarPostagensFeed();

        List<PostagemFeedDto> listaDTO = new ArrayList<>();

        if (postagensFeed.isEmpty()) return ResponseEntity.noContent().build();

        for (Postagem p : postagensFeed) {
            PostagemFeedDto postagem = new PostagemFeedDto(p);

            listaDTO.add(postagem);
        }

        return ResponseEntity.ok().body(listaDTO);
    }

    @GetMapping("/publicado/{id}")
    public ResponseEntity<List<Postagem>> mostraPostagensDeUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = this.usuarioRepository.findByIdUsuario(id);

        if (usuario.isPresent()) {
            List<Postagem> postagens = usuario.get().getPostagens();

            if (postagens.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(postagens);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/competencias/{competencias}")
    public ResponseEntity<List<Postagem>> getPostagemPorCompetencias(@PathVariable String competencias){

        List<Postagem> postagens = postagemRepository.findAll()
                .stream()
                .filter(postagem -> postagem.getCompetencias().contains(competencias))
                .collect(Collectors.toList());

        return ResponseEntity.ok(postagens);
    }

    @GetMapping("/{postId}/responsavel")
    public ResponseEntity<UsuarioDto> getResponsavelPublicacao(@PathVariable Integer postId) {
        Optional<Postagem> optionalPostagem = postagemRepository.findById(postId);
        if (optionalPostagem.isPresent()) {
            Postagem postagem = optionalPostagem.get();
            Usuario usuario = postagem.getUsuario();
            if (usuario != null) {
                UsuarioDto usuarioDTO = new UsuarioDto();
                usuarioDTO.setIdUsuario(usuario.getIdUsuario());
                usuarioDTO.setIsPremium(usuario.getIsPremium());
                usuarioDTO.setCep(usuario.getCep());
                usuarioDTO.setCidade(usuario.getCidade());
                usuarioDTO.setEmail(usuario.getEmail());
                usuarioDTO.setNome(usuario.getNome());
                usuarioDTO.setTelefone(usuario.getTelefone());
                usuarioDTO.setUf(usuario.getUf());
                usuarioDTO.setBiografia(usuario.getBiografia());
                return ResponseEntity.ok(usuarioDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
