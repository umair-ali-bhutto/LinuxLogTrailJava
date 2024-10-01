package com.ag.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TrailLogger {
    private boolean tailingActive = false;
    private Thread tailThread;
    private Timer timer;
    
    // You may replace these properties with your actual values
    private static final String LOG_FROM_PATH = "/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/temp.log";
    private static final String LOG_TO_PATH = "/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/TrailLogs/temp.log";
    private static final String FILE_ROLLING_DATE_FORMAT = "yyyy-MM-dd"; // Adjust as per your format
    private static final long TIMEOUT = 1 * 60 * 1000; // Example: 10 minutes timeout
    private static final String DOWNLOAD_PATH = "/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/TrailLogs/";

    public static void main(String[] args) {
        TrailLogger tailProcessManager = new TrailLogger();
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.println("Enter command (start/stop/exit): ");
            command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "start":
                    try {
                        tailProcessManager.startTailing();
                    } catch (IOException e) {
                        System.out.println("Failed to start tail process: " + e.getMessage());
                    }
                    break;
                case "stop":
                    tailProcessManager.stopTailing();
                    break;
                case "exit":
                    tailProcessManager.stopTailing();
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Unknown command. Please enter 'start', 'stop', or 'exit'.");
            }
        }
    }

    // Method to start tailing the log file
    public void startTailing() throws IOException {
        if (!tailingActive) {
            rollLogFile();
            tailingActive = true;

            tailThread = new Thread(() -> {
                try (RandomAccessFile reader = new RandomAccessFile(LOG_FROM_PATH, "r")) {
                    long filePointer = reader.length(); // Start at the end of the file

                    while (tailingActive) {
                        long fileLength = new File(LOG_FROM_PATH).length();
                        if (fileLength < filePointer) {
                            // Log file was truncated, reset the pointer
                            filePointer = 0;
                        }
                        if (fileLength > filePointer) {
                            reader.seek(filePointer);
                            String line;
                            while ((line = reader.readLine()) != null) {
                                appendLogToFile(line);
                            }
                            filePointer = reader.getFilePointer();
                        }
                        Thread.sleep(1000); // Wait before checking for new lines
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error while tailing file: " + e.getMessage());
                }
            });
            tailThread.start();
            System.out.println("Tail process started.");
            startTimer();
        } else {
            System.out.println("Tail Process Already Running.");
        }
    }

    // Method to stop tailing the log file
    public void stopTailing() {
        if (tailingActive) {
            tailingActive = false;
            try {
                if (tailThread != null) {
                    tailThread.join(); // Wait for the thread to finish
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted while stopping the tail process.");
            }
            System.out.println("Tail process stopped.");
            stopTimer();
            downloadLogFile(); // Trigger the download when stopping
        } else {
            System.out.println("No tail process to stop.");
        }
    }

    // Append new log entries to the destination log file
    private void appendLogToFile(String logLine) throws IOException {
        File targetFile = new File(LOG_TO_PATH);
        try (RandomAccessFile writer = new RandomAccessFile(targetFile, "rw")) {
            writer.seek(targetFile.length()); // Move to the end of the file
            writer.writeBytes(logLine + System.lineSeparator());
        }
    }

    // Method to start the timeout timer
    public void startTimer() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timeout reached. Stopping the tail process automatically.");
                stopTailing();
            }
        }, TIMEOUT);
    }

    // Method to stop the timeout timer
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // Method to roll the log file if it exists
    public void rollLogFile() {
        File logFile = new File(LOG_TO_PATH);
        if (logFile.exists()) {
            String date = new SimpleDateFormat(FILE_ROLLING_DATE_FORMAT).format(new Date());
            File newLogFile = new File(LOG_TO_PATH.replaceAll("\\.log", "_" + date + ".log"));
            if (logFile.renameTo(newLogFile)) {
                System.out.println("Log file rolled to: " + newLogFile.getName());
            } else {
                System.out.println("Failed to roll the log file.");
            }
        }
    }

    // Method to download the log file
    private void downloadLogFile() {
        File sourceFile = new File(LOG_TO_PATH);
        if (sourceFile.exists()) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File destinationFile = new File(DOWNLOAD_PATH + "DownloadedLog_" + timestamp + ".log");

            try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
                 FileChannel destinationChannel = new FileOutputStream(destinationFile).getChannel()) {
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                System.out.println("Log file downloaded to: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to download the log file: " + e.getMessage());
            }
        } else {
            System.out.println("Log file not found for download.");
        }
    }
}
