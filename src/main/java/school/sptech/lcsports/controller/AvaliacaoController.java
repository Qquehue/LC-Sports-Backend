package school.sptech.lcsports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.lcsports.domain.Avaliacao;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.repository.AvaliacaoRepository;
import school.sptech.lcsports.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/avaliacoes")
@CrossOrigin("*")
public class AvaliacaoController {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Avaliacao>> buscaAvaliacaoPorId(@PathVariable Integer id) {

        if (!avaliacaoRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Avaliacao> avaliacao = avaliacaoRepository.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(avaliacao);
    }

    @PostMapping("/avaliar")
    public ResponseEntity<Void> criarAvaliacao(@RequestBody Avaliacao avaliacao) {
        try {

            avaliacaoRepository.save(avaliacao);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/avaliar/{id}")
    public ResponseEntity<Avaliacao> atualizarAvaliacao(@PathVariable Integer id, @RequestBody Avaliacao avaliacaoAtualizada) {
        try {
            if (!avaliacaoRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            avaliacaoAtualizada.setIdAvaliacao(id);

            return ResponseEntity.status(HttpStatus.OK).body(avaliacaoRepository.save(avaliacaoAtualizada));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAvaliacao(@PathVariable Integer id) {

        try {
            Optional<Avaliacao> avaliacao = avaliacaoRepository.findById(id);
            avaliacaoRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/avaliado")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesPorUsuarioAvaliado(@RequestParam(name = "usuarioId") Integer usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Avaliacao> avaliacoesDoUsuario = avaliacaoRepository.findByAvaliado(usuario);

        return ResponseEntity.status(HttpStatus.OK).body(avaliacoesDoUsuario);
    }

    @GetMapping("/avaliador")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesPorUsuarioAvaliador(@RequestParam(name = "usuarioId") Integer usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Avaliacao> avaliacoesDoUsuario = avaliacaoRepository.findByAvaliador(usuario);

        return ResponseEntity.status(HttpStatus.OK).body(avaliacoesDoUsuario);
    }

    @GetMapping("/media/{idAvaliado}")
    public ResponseEntity<Double> mediaNotasAvaliado(@PathVariable Integer idAvaliado) {
        Usuario avaliado = usuarioRepository.findById(idAvaliado).orElse(null);

        if (avaliado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Avaliacao> avaliacoesDoAvaliado = avaliacaoRepository.findByAvaliado(avaliado);

        if (avaliacoesDoAvaliado.isEmpty()) {
            return ResponseEntity.ok(0.0);
        }

        double somaDasNotas = 0.0;
        for (Avaliacao avaliacao : avaliacoesDoAvaliado) {
            somaDasNotas += avaliacao.getNota();
        }

        double mediaDasNotas = somaDasNotas / avaliacoesDoAvaliado.size();

        double mediaArredondada = Math.round(mediaDasNotas * 10.0) / 10.0;

        return ResponseEntity.ok(mediaArredondada);
    }

}