package com.example.videoEditor.controllers;


import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.services.VideoService;
import com.example.videoEditor.utils.FfmpegHelper;
import com.example.videoEditor.utils.VideoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService){
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        // Validate file size and content type
        long fileSizeMb = file.getSize()/1000000;
        if (fileSizeMb > VideoHelper.maxSize) {
            return ResponseEntity.badRequest().body("File size exceeds limit");
        }
        // Copy the file contents to new file with unique file name
        String filename = UUID.randomUUID() + "." + VideoHelper.getFileExtension(file);
        File  directory = VideoHelper.createDirectoryIfNotExists(VideoHelper.directoryPath);
        Path filePath = Paths.get(directory.getAbsolutePath(), filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create a Video entity and save it to the database
        Video video = videoService.saveVideo(filename,"Desc",filePath.toString());

        return ResponseEntity.ok("Video uploaded successfully");
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long videoId) {
        // Retrieve the video entity from the database
        Video video = videoService.findVideo(videoId);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }


        // Get the file path from the video entity
        String filePath = video.getFilePath();
        File file = new File(filePath);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(VideoHelper.MEDIA_TYPE_MP4));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }
}

