package com.ag.logger;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author umair.ali
 * Servlet implementation class LogControlServlet
 */
@WebServlet("/test")
public class LogControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private LogToFile logToFile;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogControlServlet() {
		super();
		logToFile = new LogToFile();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String command = request.getParameter("command");

		if (command.equals("start")) {
			try {
				logToFile.startTailing();
				response.getWriter().write("Tail process started.");
			} catch (IOException e) {
				response.getWriter().write("Failed to start tail process: " + e.getMessage());
			}
		} else if (command.equals("stop")) {
			logToFile.stopTailing();
			response.getWriter().write("Tail process stopped.");
		} else {
			response.getWriter().write("Unknown command.");
		}
	}

}
