package school.sptech.lcsports.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.dto.DtoLogin;
import school.sptech.lcsports.dto.UsuarioCriacaoDto;
import school.sptech.lcsports.repository.CurtidaRepository;
import school.sptech.lcsports.repository.PostagemRepository;
import school.sptech.lcsports.repository.UsuarioRepository;
import school.sptech.lcsports.security.Token;
import school.sptech.lcsports.security.TokenUtil;
import school.sptech.lcsports.service.UsuarioService;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private PostagemRepository postagemRepository;
    private CurtidaRepository curtidaRepository;
    private TokenUtil jwtTokenUtil;
    @Autowired
    UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository, PostagemRepository postagemRepository,
                             CurtidaRepository curtidaRepository,UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.postagemRepository = postagemRepository;
        this.curtidaRepository = curtidaRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(usuarios);
    }
    @PostMapping
    public ResponseEntity<Usuario> adicionarUsuario( @RequestBody UsuarioCriacaoDto usuario) {
        System.out.println("tentando");
        return ResponseEntity.status(201).body(usuarioService.criarUsuario(usuario));
    }

    @PostMapping("/autenticar")
    public ResponseEntity<DtoLogin> logar(@Valid @RequestBody Usuario usuario) {
        System.out.println("tentando");
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioOpt.isEmpty()){
            return ResponseEntity.status(404).build();
        }

        Token token = usuarioService.gerarToken(usuario);
        if(token==null){
            return ResponseEntity.status(401).build();

        }

        DtoLogin logado = new DtoLogin(usuarioOpt.get().getIdUsuario(),usuarioOpt.get().getEmail(), token);
        return ResponseEntity.status(200).body(logado);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Integer id){
        return ResponseEntity.of(usuarioRepository.findById(id));
    }

    @PatchMapping("/premium/{idUsuario}")
    public ResponseEntity<Usuario> atualizarUsuarioPremium(@PathVariable int idUsuario, @RequestParam boolean isPremium) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setIsPremium(isPremium);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/premium/descubra")
    public ResponseEntity<List<Usuario>> usuariosPremium() {
        List<Usuario> usuariosPremium = usuarioRepository.findUsuariosByIsPremiumTrue();

        Collections.shuffle(usuariosPremium);

        int maxUsuarios = Math.min(5, usuariosPremium.size());
        List<Usuario> usuariosAleatorios = usuariosPremium.stream()
                .limit(maxUsuarios)
                .collect(Collectors.toList());

        if (usuariosAleatorios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuariosAleatorios);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Usuario> atualizarUsuario(
            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
            @RequestPart("usuario") String novoUserJson
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Usuario usuario = objectMapper.readValue(novoUserJson, Usuario.class);

            Optional<Usuario> usuarioExistente = usuarioService.buscarUsuarioPorId(usuario.getIdUsuario());
            if (!usuarioExistente.isPresent()) {
                return ResponseEntity.status(404).build();
            }

            Usuario usuarioAtualizado = usuarioExistente.get();

            if (imagem != null && !imagem.isEmpty()) {
                byte[] imagemBytes = imagem.getBytes();
                String containerName = "imagens";
                String blobName = generateUniqueBlobName(containerName, imagem.getOriginalFilename());

                uploadImage(imagemBytes, containerName, blobName);

                usuarioAtualizado.setFotoUsuario(blobName);
            }

            if (usuario.getNome() != null && !usuario.getNome().isEmpty()) {
                usuarioAtualizado.setNome(usuario.getNome());
            }

            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                usuarioAtualizado.setEmail(usuario.getEmail());
            }

            if (usuario.getCidade() != null && !usuario.getCidade().isEmpty()) {
                usuarioAtualizado.setCidade(usuario.getCidade());
            }

            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuarioAtualizado.setSenha(usuario.getSenha());
            }

            if (usuario.getCep() != null && !usuario.getCep().isEmpty()) {
                usuarioAtualizado.setCep(usuario.getCep());
            }

            if (usuario.getTelefone() != null && !usuario.getTelefone().isEmpty()) {
                usuarioAtualizado.setTelefone(usuario.getTelefone());
            }

            if (usuario.getUf() != null && !usuario.getUf().isEmpty()) {
                usuarioAtualizado.setUf(usuario.getUf());
            }

            if (usuario.getBiografia() != null && !usuario.getBiografia().isEmpty()) {
                usuarioAtualizado.setBiografia(usuario.getBiografia());
            }


            return ResponseEntity.status(201).body(usuarioService.editarUsuario(usuarioAtualizado));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(400).build();
    }
    private void uploadImage(byte[] imageBytes, String containerName, String blobName) {
        String connectionString ="DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=Kh2qYrgPJGdn6pmBug7+ooUsJSwhPz8tPho5qRKvG+1DRn7isbgRskMZeTXaaiJKrV/ocu8ByBq++AStFxzqpw==;EndpointSuffix=core.windows.net" +
                "\n";

        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            blobClient.upload(inputStream, imageBytes.length);
        } catch (Exception e) {
            System.out.println("Erro no upload da imagem");
        }
    }
    private String generateUniqueBlobName(String containerName, String originalFilename) {

        int counter = 1;
        String blobName = originalFilename;
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
            blobName = originalFilename.substring(0, dotIndex);
        }

        String newBlobName = blobName + extension;

        while (blobExists(containerName, newBlobName)) {
            newBlobName = blobName + counter + extension;
            counter++;
        }

        return newBlobName;
    }

    private boolean blobExists(String containerName, String blobName) {
        String connectionString ="DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=Kh2qYrgPJGdn6pmBug7+ooUsJSwhPz8tPho5qRKvG+1DRn7isbgRskMZeTXaaiJKrV/ocu8ByBq++AStFxzqpw==;EndpointSuffix=core.windows.net";

        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();

        BlobClient blobClient = containerClient.getBlobClient(blobName);
        return blobClient.exists();
    }

}
