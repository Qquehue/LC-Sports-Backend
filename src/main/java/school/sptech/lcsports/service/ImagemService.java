package school.sptech.lcsports.service;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobRequestConditions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class ImagemService {

    private String connectionString = "DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=YoYMFCQHxqWv76Mau+T1LtiqMKnuiKTh0pIuhuR5t233v0g1QxxkIGaQwNSlgfvyzgKc+m4VNPFH+AStA3OGjA==;EndpointSuffix=core.windows.net";

    private BlobServiceClient blobServiceClient;

    private BlobHttpHeaders blobServiceHeaders;

    @PostConstruct
    public void initialize() {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public String uploadImage(MultipartFile file) throws IOException {

        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Extrai a extensão do arquivo
            String fileExtension = "";

            if (fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf("."));
            }

            String fileNameHash = UUID.randomUUID().toString().replaceAll("-", "") + fileExtension;


            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("imagens");
            containerClient.createIfNotExists();


            BlobClient blobClient = containerClient.getBlobClient(fileNameHash);

            BlobHttpHeaders jsonHeaders = new BlobHttpHeaders()
                    .setContentType(file.getContentType());

            InputStream imageStream = file.getInputStream();
            BinaryData data = BinaryData.fromStream(imageStream, file.getSize());
            BlobParallelUploadOptions options = new BlobParallelUploadOptions(data)
                    .setRequestConditions(new BlobRequestConditions()).setHeaders(jsonHeaders);

            blobClient.uploadWithResponse(options, null, Context.NONE);
            imageStream.close();

            return blobClient.getBlobUrl().toString();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Erro ao fazer upload da imagem");
        }
    }

    public void showImage(byte[] imageBytes, String containerName, String blobName) {
        String connectionString = "DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=YoYMFCQHxqWv76Mau+T1LtiqMKnuiKTh0pIuhuR5t233v0g1QxxkIGaQwNSlgfvyzgKc+m4VNPFH+AStA3OGjA==;EndpointSuffix=core.windows.net";

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

    public boolean blobExists(String containerName, String blobName) {
        String connectionString = "DefaultEndpointsProtocol=https;AccountName=lcsportsimg;AccountKey=YoYMFCQHxqWv76Mau+T1LtiqMKnuiKTh0pIuhuR5t233v0g1QxxkIGaQwNSlgfvyzgKc+m4VNPFH+AStA3OGjA==;EndpointSuffix=core.windows.net";


        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();

        BlobClient blobClient = containerClient.getBlobClient(blobName);
        return blobClient.exists();
    }

    public String generateUniqueBlobName(String containerName, String originalFilename) {

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
}
