package com.ag.logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.ag.property.LoggerProperties;

/**
 * @author umair.ali
 * @version 1.0
 * @since 05-July-2024
 * 
 *        This class manages a process that tails a log file and writes the
 *        output to another file. It includes methods to start and stop the
 *        tailing process and to automatically stop it after a timeout period.
 * 
 * @apiNote Only Works On Linux
 * 
 */

public class LogToFile {
	public Process tailProcess = null;
	public Timer timer;
	public static final long TIMEOUT = Long.parseLong(LoggerProperties.getProperty("timeout")) * 60 * 1000;

	// Method to start the tail process
	public void startTailing() throws IOException {
		if (tailProcess == null) {
			rollLogFile();

			ProcessBuilder processBuilder = new ProcessBuilder("tail", "-f",
					LoggerProperties.getProperty("log.from.path"));
			processBuilder.redirectOutput(
					ProcessBuilder.Redirect.to(new java.io.File(LoggerProperties.getProperty("log.to.path"))));
			tailProcess = processBuilder.start();
			System.out.println("Tail process started.");
			startTimer();
		} else {
			System.out.println("Tail Process Already Running.");
		}

	}

	// Method to stop the tail process
	public void stopTailing() {
		if (tailProcess != null) {
			tailProcess.destroy();
			tailProcess = null;
			System.out.println("Tail process stopped.");
			stopTimer();
		} else {
			System.out.println("No tail process to stop.");
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
		File logFile = new File(LoggerProperties.getProperty("log.to.path"));
		if (logFile.exists()) {
			String date = new SimpleDateFormat(LoggerProperties.getProperty("file.rolling.log.to.path.date.format"))
					.format(new Date());
			File newLogFile = new File(
					LoggerProperties.getProperty("file.rolling.log.to.path").replaceAll("@DATE", date));
			if (logFile.renameTo(newLogFile)) {
				System.out.println("Log file rolled to: " + newLogFile.getName());
			} else {
				System.out.println("Failed to roll the log file.");
			}
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
