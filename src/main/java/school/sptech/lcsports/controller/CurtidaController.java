package school.sptech.lcsports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.lcsports.domain.CurtidaPostagem;
import school.sptech.lcsports.domain.Postagem;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.repository.CurtidaRepository;
import school.sptech.lcsports.repository.PostagemRepository;
import school.sptech.lcsports.repository.UsuarioRepository;
import school.sptech.lcsports.service.UsuarioService;

import java.util.Optional;

@RestController
@RequestMapping("/v1/curtidas")
@CrossOrigin(origins = "*")
public class CurtidaController {
    private UsuarioRepository usuarioRepository;
    private PostagemRepository postagemRepository;
    private CurtidaRepository curtidaRepository;

    @Autowired
    private UsuarioController costureiraController;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    public CurtidaController(UsuarioRepository _usuarioRepository, PostagemRepository _postagemRepository,
                             CurtidaRepository _curtidaRepository) {
        usuarioRepository = _usuarioRepository;
        postagemRepository = _postagemRepository;
        curtidaRepository = _curtidaRepository;
    }

    @PostMapping("/curtida-postagem")
    public ResponseEntity<CurtidaPostagem> curtirPostagem(@RequestParam Integer idUsuario,
                                                          @RequestParam Integer idPostagem) {

        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Postagem> postagem = postagemRepository.findById(idPostagem);

        if (postagem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CurtidaPostagem cp = curtidaRepository.save(new CurtidaPostagem(usuario.get(), postagem.get()));

        return ResponseEntity.created(null).body(cp);

    }

    @DeleteMapping("/remover-curtida/{idUsuario}/postagem/{idPostagem}")
    public ResponseEntity<Void> removerCurtida(@PathVariable int idUsuario, @PathVariable int idPostagem) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Postagem> postagem = postagemRepository.findById(idPostagem);

        if (postagem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<CurtidaPostagem> curtidaUsuario = curtidaRepository.findByUsuarioIdUsuarioAndPostagemIdPostagem(idUsuario,idPostagem);
        if (curtidaUsuario.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        curtidaRepository.delete(curtidaUsuario.get());
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/verifica-curtida/{idUsuario}/postagem/{idPostagem}")
    public ResponseEntity<Boolean> verificarCurtida(@PathVariable int idUsuario,
                                                    @PathVariable int idPostagem){
        return ResponseEntity.ok(curtidaRepository.findByUsuarioIdUsuarioAndPostagemIdPostagem
                (idUsuario,idPostagem).isPresent());
    }
}
