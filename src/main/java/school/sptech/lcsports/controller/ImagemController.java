package school.sptech.lcsports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.lcsports.service.ImagemService;

import java.io.IOException;

@RestController
@RequestMapping("/v1/imagens")
@CrossOrigin(origins = "*")
public class ImagemController {

    @Autowired
    private ImagemService imagemServiceImpl;

    @PostMapping(value = "/config", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("imagem") MultipartFile file){

        try{
            String imageUrl = imagemServiceImpl.uploadImage(file);

            return ResponseEntity.created(null).body(imageUrl);

        }catch (IOException e){
            System.out.println(e.getCause());
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            System.out.println(e.getCause());
            return ResponseEntity.internalServerError().build();
        }
    }
}
