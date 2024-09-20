package com.example.videoEditor.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class VideoHelper {

    public static final String MEDIA_TYPE_MP4 = "video/mp4";

    @Value("${video.max.size}")
    public static long maxSize;

    @Value("${video.min.duration}")
    private static long minDuration;

    @Value("${video.max.duration}")
    private static long maxDuration;

    public static final String directoryPath = "videos";

    public static File createDirectoryIfNotExists(String directoryPath) throws IOException {
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

    public static String getFileExtension(MultipartFile file){
        return Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
    }

}
