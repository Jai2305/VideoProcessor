package com.example.videoEditor.controllers;

import com.example.videoEditor.entitites.SharedVideoLink;
import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.SharedVideoLinkRepository;
import org.springframework.http.MediaType;
import com.example.videoEditor.services.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class SharedLinkController {
    private final SharedVideoLinkRepository sharedVideoLinkRepository;
    private final VideoService videoService;

    public SharedLinkController(SharedVideoLinkRepository sharedVideoLinkRepository, VideoService videoService) {
        this.sharedVideoLinkRepository = sharedVideoLinkRepository;
        this.videoService = videoService;
    }

    @PostMapping("/share/{videoId}")
    public ResponseEntity<String> shareVideo(@PathVariable Long videoId) {

        Video video = videoService.findVideo(videoId);
        String videoFilePath = video.getFilePath();

        // Generate a unique share token (could use UUID or any other logic)
        String shareToken = UUID.randomUUID().toString();

        // Set expiry time (e.g., 24 hours from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

        // Save the shareable link with expiry time
        SharedVideoLink sharedVideoLink = new SharedVideoLink();
        sharedVideoLink.setVideoFilePath(videoFilePath);
        sharedVideoLink.setShareToken(shareToken);
        sharedVideoLink.setExpiryTime(expiryTime);

        // Save to database
        sharedVideoLinkRepository.save(sharedVideoLink);

        // Return the generated link (assuming you serve from `/download/{token}`)
        String shareableLink = "http://localhost:8080/download/" + shareToken;
        return ResponseEntity.ok(shareableLink);
    }

    @GetMapping("/download/{shareToken}")
    public ResponseEntity<?> downloadVideo(@PathVariable String shareToken) throws IOException {
        // Find the link by token
        SharedVideoLink sharedVideoLink = sharedVideoLinkRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Check if the link has expired
        if (LocalDateTime.now().isAfter(sharedVideoLink.getExpiryTime())) {
            return ResponseEntity.status(HttpStatus.GONE).body(null); // Link expired
        }

        // Serve the video file
        File videoFile = new File(sharedVideoLink.getVideoFilePath());
        if (!videoFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + videoFile.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(videoFile.length())
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(new FileSystemResource(videoFile));
    }


}
