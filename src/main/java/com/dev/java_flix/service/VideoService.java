package com.dev.java_flix.service;

import com.dev.java_flix.dto.request.VideoRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.VideoResponse;

public interface VideoService {

    MessageResponse createVideoByAdmin(VideoRequest videoRequest);

    PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);

    MessageResponse updateVideoByAdmin(Long id, VideoRequest videoRequest);

}
