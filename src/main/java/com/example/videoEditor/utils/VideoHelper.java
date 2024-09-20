package com.example.videoEditor.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class VideoHelper {

    public static final String MEDIA_TYPE_MP4 = "video/mp4";
    public static final String LIST_TXT = "fileList.txt";

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

    // Helper method to write the list of clips to a file
    public static File writeClipsToFile(File dir, List<MultipartFile> clips) throws IOException {
        File listFile = new File(dir, VideoHelper.LIST_TXT);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
            for (MultipartFile clip : clips) {
                File tempClipFile = new File(dir, Objects.requireNonNull(clip.getOriginalFilename()));
                Files.copy(clip.getInputStream(), tempClipFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                writer.write("file '" + tempClipFile.getAbsolutePath() + "'");
                writer.newLine();
            }
        }
        return listFile;
    }

    // Helper method to clean up temporary files
    public static void cleanUpTemporaryFiles(String dirPath, File listFile, List<MultipartFile> clips) throws IOException {
        Files.deleteIfExists(listFile.toPath());
        for (MultipartFile clip : clips) {
            Path tempClipPath = Paths.get(dirPath, Objects.requireNonNull(clip.getOriginalFilename()));
            Files.deleteIfExists(tempClipPath);
        }
    }

}
