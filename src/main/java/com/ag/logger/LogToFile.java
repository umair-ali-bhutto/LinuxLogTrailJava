package com.ag.logger;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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
	private Process tailProcess = null;
	private Timer timer;
//	private static final long TIMEOUT = 5 * 60 * 1000;
	private static final long TIMEOUT = 10000;
	
	// Method to start the tail process
	public void startTailing() throws IOException {
		if(tailProcess == null) {
			ProcessBuilder processBuilder = new ProcessBuilder("tail", "-f",
					"/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/temp.log");
			processBuilder.redirectOutput(ProcessBuilder.Redirect
					.to(new java.io.File("/mnt/8EFED7B1FED79037/UBUNTU-BACKUP/Desktop/ag-mw-logs/test.log")));
			tailProcess = processBuilder.start();
			System.out.println("Tail process started.");
			startTimer();
		}else {
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
	private void startTimer() {
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
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
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
