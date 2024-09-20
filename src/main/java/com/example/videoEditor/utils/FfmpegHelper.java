package com.example.videoEditor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FfmpegHelper {

    public static String[] getTrimCommand(String inputFilePath, Long startTime, Long endTime, String trimmedFilePath){
        return new String[]{
                "ffmpeg",
                "-y", // to replace existing file // No trailing space after "ffmpeg"
                "-i", inputFilePath,        // Input video file path
                "-ss", String.valueOf(startTime), // Start time
                "-to", String.valueOf(endTime),   // End time
                "-c", "copy",              // Copy video and audio without re-encoding
                trimmedFilePath            // Ensure this is a clean string
        };
    }


    public static void logOutputForInputStream(InputStream inputStream) throws IOException {
        // Capture and print FFmpeg output
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Print the output for debugging
        }

    }

    public static void runCommand(String[] ffmpegCommand) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
        processBuilder.directory(new File("X:/"));
        processBuilder.redirectErrorStream(true); // Merge stderr and stdout for debugging

        // Start the process
        Process process = processBuilder.start();

        logOutputForInputStream(process.getInputStream());

        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("FFmpeg command executed successfully.");
        } else {
            System.out.println("FFmpeg command failed with exit code: " + exitCode);
        }
    }
}
