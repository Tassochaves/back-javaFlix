package com.dev.java_flix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatsResponse {

    private long totalVideos;
    private long publishedVideos;
    private long totalDuration;
}
