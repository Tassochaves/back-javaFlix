package com.dev.java_flix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.java_flix.dto.response.FileUploadResponse;
import com.dev.java_flix.service.FileUploadService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService){
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload/video")
    public ResponseEntity<FileUploadResponse> uploadVideo(@RequestParam("file") MultipartFile file){

        String uuid = fileUploadService.storeVideoFile(file);
        return ResponseEntity.ok(new FileUploadResponse(uuid, file.getOriginalFilename(), file.getSize()));
    }
}
