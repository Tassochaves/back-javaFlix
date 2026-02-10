package com.dev.java_flix.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dev.java_flix.service.FileUploadService;
import com.dev.java_flix.util.FileHandlerUtil;

import jakarta.annotation.PostConstruct;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    private Path videoStorageLocation;
    private Path imageStorageLocation;

    

    @Value("${file.upload.video-dir:uploads/videos}")
    private String videoDir;
 
    @Value("${file.upload.image-dir:uploads/images}")
    private String imageDir;

    //Garante os caminhos de armazenamento, logo após a injeção de dependências, antes de qualquer upload.
    @PostConstruct
    public void init(){
        this.videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();

        try {
            // Cria as pastas fisicamente no disco caso elas não existam
            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Override
    public String storeVideoFile(MultipartFile file) {
        return storeFile(file, videoStorageLocation);
    }

    /**
     * Salva arquivos no sistema.
     * Cuida da segurança (renomeação) e da persistência física.
     * @param storageLocation caminho onde o arquivo deve ser salvo.
     */
    private String storeFile(MultipartFile file, Path storageLocation) {
        
        String fileExtension = FileHandlerUtil.extractFileExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + fileExtension;

        try {

            //Impede o processamento de arquivos vazios ou corrompidos
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store empty file "+filename);
            }

            Path targetLocation = storageLocation.resolve(filename);

            // Copia o fluxo de dados (input stream) do upload para o destino final
            // REPLACE_EXISTING garante que, em caso de erro de duplicidade, o arquivo seja sobrescrito
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uuid;
            
        } catch (IOException ex) {
            throw new RuntimeException("Filed to store file "+filename, ex);
        }
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file, imageStorageLocation);
    }

    /**
     * Orquestra a entrega do vídeo. Decide entre entrega total (200 OK) 
     * ou parcial (206 Partial Content) com base no cabeçalho Range.
     */
    @Override
    public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {
        
        try {
           Path filePath = FileHandlerUtil.findFileByUuid(videoStorageLocation, uuid);
           Resource resource = FileHandlerUtil.createFullResource(filePath);
           
           String filename = resource.getFilename();
           String contentType = FileHandlerUtil.detectVideoContentType(filename);
           long fileLength = resource.contentLength();

           if (isFullContentRequest(rangeHeader)){
                return buildFullVideoResponse(resource, contentType, filename, fileLength);
           }
           
           return buildPartialVideoResponse(filePath, contentType, rangeHeader, filename, fileLength);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader == null || rangeHeader.isEmpty();
    }

    private ResponseEntity<Resource> buildFullVideoResponse(
            Resource resource, 
            String contentType, 
            String filename,
            long fileLength) {
        
        return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                    .body(resource);
    }

    private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String contentType, String rangeHeader, String filename,
            long fileLength) throws IOException{
        
        long[] range = FileHandlerUtil.parseRangeHeader(rangeHeader, fileLength);
        long rangeStart = range[0];
        long rangeEnd = range[1];

        if(!isValidRange(rangeStart, rangeEnd, fileLength)){
            return buildRangeNotSatisfiableResponse(fileLength);
        }

        long contentLength = rangeEnd - rangeStart + 1;

        Resource rangResource = FileHandlerUtil.createRangeResource(filePath, rangeStart, contentLength);

        return ResponseEntity.status(206)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .body(rangResource);
    }

    private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
        return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLength;
    }

    private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileLength) {
        
        return ResponseEntity.status(416)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                    .build();
    }

}
