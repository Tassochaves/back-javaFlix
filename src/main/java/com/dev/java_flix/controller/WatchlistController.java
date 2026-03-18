package com.dev.java_flix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService){
        this.watchlistService = watchlistService;
    }

    @PostMapping("/{videoId}")
    public ResponseEntity<MessageResponse> addToWatchlist(@PathVariable Long videoId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(watchlistService.addToWatchlist(email, videoId));
    }

}
