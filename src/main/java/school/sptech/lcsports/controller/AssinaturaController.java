package school.sptech.lcsports.controller;

import br.com.efi.efisdk.exceptions.EfiPayException;
import io.swagger.v3.core.util.Json;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.domain.efi.Assinatura;
import school.sptech.lcsports.repository.AssinaturaRepository;
import school.sptech.lcsports.repository.UsuarioRepository;
import school.sptech.lcsports.service.AssinaturaService;
import school.sptech.lcsports.service.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/assinatura")
@CrossOrigin(origins = "*")
public class AssinaturaController {
    @Autowired
    AssinaturaRepository assinaturaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AssinaturaService assinaturaService;

    @PostMapping("/plano")
    public ResponseEntity<String> criarPlano() throws Exception {
            String response = assinaturaService.criarPlano();
            return ResponseEntity.status(201).body(response.trim());
    }

    @GetMapping("/plano")
    public ResponseEntity<String> listarPlanos() {
        try {
            String response = assinaturaService.listarPlanos();
            if(response.isEmpty()){
                return ResponseEntity.noContent().build();
            }else {
                return ResponseEntity.ok(response);
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Assinatura>> listarAssinaturas(){
        List<Assinatura> assinaturas = assinaturaRepository.findAll();
        if(assinaturas.isEmpty()){
            return ResponseEntity.status(204).build();
        }else{
            return ResponseEntity.status(200).body(assinaturas);
        }
    }

//    @GetMapping("/ultima")
//    public ResponseEntity<String> listaUltimaAssinatura() throws Exception {
//        Integer ultimoId = assinaturaRepository.getUltimoId();
//        if (assinaturaRepository.existsById(ultimoId)){
//            Optional<Assinatura> a = assinaturaRepository.findById(ultimoId);
//            Assinatura assinatura = a.get();
//
//            return ResponseEntity.ok().body(assinaturaService.listarAssinaturaPorId(assinatura.getIdAssinatura()));
//        }else{
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<String> criarAssinaturaOneStep(@PathVariable Integer idUsuario) throws Exception {

            Optional<Usuario> u = usuarioRepository.findById(idUsuario);

            if (u.isPresent()) {
                Usuario usuario = u.get();
                String response = assinaturaService.criarAssinatura(usuario);
                usuario.setIsPremium(true);
                usuarioRepository.save(usuario);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }

    }

    @DeleteMapping("/{idAssinatura}")
    public ResponseEntity<Void> deletarAssinatura(@PathVariable Integer idAssinatura){
        assinaturaService.cancelarAssinatura(idAssinatura);
        return ResponseEntity.ok().build();
    }

}
