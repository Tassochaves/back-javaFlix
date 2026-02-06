package com.dev.java_flix.dto.response;

public class FileUploadResponse {

    private String uuid;
    private String filename;
    private long size;

    public FileUploadResponse() {
    }

    public FileUploadResponse(String uuid, String filename, long size) {
        this.uuid = uuid;
        this.filename = filename;
        this.size = size;
    }

    // Getters e Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
