package com.dev.java_flix.service;

import com.dev.java_flix.dto.request.VideoRequest;
import com.dev.java_flix.dto.response.MessageResponse;

public interface VideoService {

    MessageResponse createVideoByAdmin(VideoRequest videoRequest);

}
