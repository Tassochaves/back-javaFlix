package com.dev.java_flix.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping("/upload/image")
    public ResponseEntity<FileUploadResponse> uploadImage(@RequestParam("file") MultipartFile file){

        String uuid = fileUploadService.storeImageFile(file);
        return ResponseEntity.ok(new FileUploadResponse(uuid, file.getOriginalFilename(), file.getSize()));
    }

    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> serverVideo(
            @PathVariable String uuid,
            @RequestHeader(value = "range", required = false) String rangeHeader,
            @RequestHeader(value = "token", required = false) String tokenParam
    ){

        return fileUploadService.serveVideo(uuid, rangeHeader);
    }

     @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource> serverImage(@PathVariable String uuid){
        return fileUploadService.serveImage(uuid);
    }
}
