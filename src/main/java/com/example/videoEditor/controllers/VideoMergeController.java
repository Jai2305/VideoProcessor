package com.example.videoEditor.controllers;


import com.example.videoEditor.utils.FfmpegHelper;
import com.example.videoEditor.utils.VideoHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/edit")
public class VideoMergeController {

    @PostMapping("/merge")
    public ResponseEntity<?> mergeVideos(@RequestParam("clips") List<MultipartFile> clips) throws IOException {
        if (clips.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            // Temporary directory to store uploaded files
            File dir = VideoHelper.createDirectoryIfNotExists(VideoHelper.directoryPath);
            File listFile = VideoHelper.writeClipsToFile(dir, clips);

            // Output file for the merged video
            String outputFileName = "merged_output.mp4";
            Path outputPath = Paths.get(dir.getAbsolutePath(), outputFileName);

            // Run the FFmpeg process
            String[] mergeCommand = FfmpegHelper.getMergeCommand(listFile.getAbsolutePath(), outputPath.toString());
            FfmpegHelper.executeFfmpegCommand(mergeCommand);

            // Read the merged video into a byte array to return as the response
            File file = new File(outputPath.toString());
            // Clean up temporary files
            VideoHelper.cleanUpTemporaryFiles(dir.getAbsolutePath(), listFile, clips);

            // Return the merged video as a response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

