package com.example.videoEditor.controllers;

import com.example.videoEditor.entitites.Video;
import com.example.videoEditor.services.VideoService;
import com.example.videoEditor.utils.FfmpegHelper;
import com.example.videoEditor.utils.VideoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/edit")
public class VideoTrimController {

    private final VideoService videoService;

    @Autowired
    public VideoTrimController(VideoService videoService){
        this.videoService = videoService;
    }

    @GetMapping("/{videoId}/trim/{startTime}/{endTime}")
    public ResponseEntity<?> getTrimmedVideo(@PathVariable Long videoId, @PathVariable Long startTime,
                                             @PathVariable Long endTime) throws IOException, InterruptedException {
        // Retrieve video from database
        Video video = videoService.findVideo(videoId);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        // Validate request
        String validationMessage  = videoService.validate(startTime, endTime, video.getFilePath());System.out.println(validationMessage);
        if(!validationMessage.equalsIgnoreCase("success")){
            return ResponseEntity.badRequest().body(validationMessage);
        }

        String inputFilePath = video.getFilePath();
        String trimmedFilename = UUID.randomUUID()+".mp4";
        File  directory = VideoHelper.createDirectoryIfNotExists(VideoHelper.directoryPath);
        Path trimmedFilePath = Paths.get(directory.getAbsolutePath(), trimmedFilename);

        // Get and Run Ffmpeg command to trim the video
        String[] ffmpegCommand = FfmpegHelper.getTrimCommand(inputFilePath, startTime, endTime, trimmedFilePath.toString());
        FfmpegHelper.executeFfmpegCommand(ffmpegCommand);

        // Create a Resource object for the trimmed video
        File file = new File(trimmedFilePath.toString());

        // Send Response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }
}