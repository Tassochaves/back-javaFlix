package com.dev.java_flix.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 4000, message = "Description must not exceed 4000 characters")
    private String description;

    private Integer year;
    private String rating;
    private Integer duration;
    private String src;
    private String poster;
    private boolean published;
    private List<String> categories;
}
