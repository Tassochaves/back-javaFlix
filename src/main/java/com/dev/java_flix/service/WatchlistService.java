package com.dev.java_flix.service;

import com.dev.java_flix.dto.response.MessageResponse;

public interface WatchlistService {

    MessageResponse addToWatchlist(String email, Long videoId);

}
