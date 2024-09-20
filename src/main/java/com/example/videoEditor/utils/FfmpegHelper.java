package com.example.videoEditor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FfmpegHelper {
    static final String DURATION_REGEX = "Duration: (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})";

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

    public static String[] getDurationCommand(String videoFilePath){
        return new String[] {
                "ffmpeg",
                "-i", videoFilePath
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

    public static Long getVideoDuration(String videoFilePath) throws InterruptedException, IOException {

        long totalSeconds = 0;

        // Command to run FFmpeg and get the video duration
        String[] durationCommand = getDurationCommand(videoFilePath);

        // Build and start the process
        ProcessBuilder processBuilder = new ProcessBuilder(durationCommand);
        processBuilder.redirectErrorStream(true); // Combine stdout and stderr
        Process process = processBuilder.start();

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        Pattern pattern = Pattern.compile(DURATION_REGEX);


        // Search for the line containing the duration
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                // Extract hours, minutes, seconds, and milliseconds from the matched groups
                int hours = Integer.parseInt(matcher.group(1));
                int minutes = Integer.parseInt(matcher.group(2));
                int seconds = Integer.parseInt(matcher.group(3));
                int milliseconds = Integer.parseInt(matcher.group(4));

                // Convert hours, minutes, and milliseconds to seconds
                totalSeconds = hours * 3600L + minutes * 60L + seconds;
                totalSeconds += (milliseconds / 100);
            }
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("FFmpeg command failed with exit code: " + exitCode);
        }

        return totalSeconds;
    }

}
