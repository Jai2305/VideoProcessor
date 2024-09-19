package com.example.videoEditor.controllers;


import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Value("${video.max.size}")
    private long maxSize;

    @Value("${video.min.duration}")
    private long minDuration;

    @Value("${video.max.duration}")
    private long maxDuration;

    private final VideoRepository videoRepository;
    private final String directoryPath = "videos";

    @Autowired
    public VideoController(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
    }
    @Autowired
    private ResourceLoader resourceLoader;

    public File createDirectoryIfNotExists(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created successfully: " + directoryPath);
            } else {
                System.out.println("Failed to create directory: " + directoryPath);
            }
        } else {
            System.out.println("Directory already exists: " + directoryPath);
        }
        return directory;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        // Validate file size and content type
        long fileSizeMb = file.getSize()/1000000;
        if (fileSizeMb > maxSize) {
            return ResponseEntity.badRequest().body("File size exceeds limit");
        }
        System.out.println(file.getOriginalFilename());
        // Generate a unique filename
        String filename = UUID.randomUUID() + "." + Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];


        File  directory = createDirectoryIfNotExists(directoryPath);

        System.out.println(directory.getAbsolutePath());
        Path filePath = Paths.get(directoryPath, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create a Video entity and save it to the database
        Video video = new Video();
        video.setTitle(filename);
        video.setDescription("Video description");
        video.setFilePath(filePath.toString());
        videoRepository.save(video);

        return ResponseEntity.ok("Video uploaded successfully");
    }
}

