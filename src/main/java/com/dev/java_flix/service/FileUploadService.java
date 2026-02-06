package com.dev.java_flix.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

}
