package com.dev.java_flix.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.java_flix.service.FileUploadService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService){
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file){

        String uuid = fileUploadService.storeVideoFile(file);
        return ResponseEntity.ok(buildUploadResponse(uuid, file));
    }

    private Map<String, String> buildUploadResponse(String uuid, MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();

        response.put("uuid", uuid);
        response.put("filename", file.getOriginalFilename());
        response.put("size", String.valueOf(file.getSize()));
        
        return response;
    }
}
