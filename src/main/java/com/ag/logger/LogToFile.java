package com.ag.logger;

import java.io.IOException;
import java.util.Scanner;

public class LogToFile {
    private Process tailProcess;

    // Method to start the tail process
    public void startTailing() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("tail", "-f", "/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/temp.log");
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new java.io.File("/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/test.log")));
        tailProcess = processBuilder.start();
        System.out.println("Tail process started.");
    }

    // Method to stop the tail process
    public void stopTailing() {
        if (tailProcess != null) {
            tailProcess.destroy();
            System.out.println("Tail process stopped.");
        } else {
            System.out.println("No tail process to stop.");
        }
    }

    public static void main(String[] args) {
        LogToFile tailProcessManager = new LogToFile();
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
}
