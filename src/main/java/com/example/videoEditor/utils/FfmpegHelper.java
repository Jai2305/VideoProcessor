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

    // Generate FFmpeg trim command
    public static String[] getTrimCommand(String inputFilePath, Long startTime, Long endTime, String trimmedFilePath) {
        return new String[] {
                "ffmpeg",
                "-y", // Overwrite existing file
                "-i", inputFilePath,
                "-ss", String.valueOf(startTime),
                "-to", String.valueOf(endTime),
                "-c", "copy",
                trimmedFilePath
        };
    }

    // Generate FFmpeg command to get duration
    public static String[] getDurationCommand(String videoFilePath) {
        return new String[] { "ffmpeg", "-i", videoFilePath };
    }

    // Central method to run any FFmpeg command and return process output
    private static Process runFfmpegCommand(String[] ffmpegCommand, File workingDirectory) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory);
        }
        processBuilder.redirectErrorStream(true); // Combine stdout and stderr
        return processBuilder.start();
    }

    // Common method to log output from InputStream
    public static void logOutputForInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Debugging purpose
            }
        }
    }

    // Run any FFmpeg command and log the output
    public static void executeFfmpegCommand(String[] ffmpegCommand) throws IOException, InterruptedException {
        Process process = runFfmpegCommand(ffmpegCommand, new File("X:/")); // Change directory as needed

        logOutputForInputStream(process.getInputStream());

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("FFmpeg command executed successfully.");
        } else {
            System.err.println("FFmpeg command failed with exit code: " + exitCode);
        }
    }

    // Extract video duration in seconds
    public static long getVideoDuration(String videoFilePath) throws IOException, InterruptedException {
        Process process = runFfmpegCommand(getDurationCommand(videoFilePath), null);

        // Extract video duration from process output
        return extractDurationFromOutput(process.getInputStream());
    }

    // Extract duration from InputStream using the provided regex
    private static long extractDurationFromOutput(InputStream inputStream) throws IOException {
        long totalSeconds = 0;
        Pattern pattern = Pattern.compile(DURATION_REGEX);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Debugging purpose
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    totalSeconds = getTimeSecondsFromMatcher(matcher);
                }
            }
        }

        return totalSeconds;
    }

    // central method to extract time given bufferReader pattern matcher
    public static long getTimeSecondsFromMatcher(Matcher matcher){
        long totalSeconds;
        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));
        int milliseconds = Integer.parseInt(matcher.group(4));

        // Convert hours, minutes, seconds to total seconds
        totalSeconds = hours * 3600L + minutes * 60L + seconds;
        totalSeconds += milliseconds / 100;
        return totalSeconds;
    }
}
