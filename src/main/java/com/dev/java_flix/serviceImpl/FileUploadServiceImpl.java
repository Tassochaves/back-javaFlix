package com.dev.java_flix.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
     * @param storageLocation O Path (caminho) onde o arquivo deve ser salvo.
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

}
