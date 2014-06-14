package co.wds.testingtools.annotations.mapperservlet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class TestingServer {

	ServletContextHandler handler;
	
	public TestingServer(int port) {
		server = new Server(port);
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/"); // technically not required, as "/" is the default
        server.setHandler(handler);
	}

	private Server server;

	private List<String> sessionStartRequests = new ArrayList<String>();

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}
	
	public List<String> getSessionStartRequests() {
		return sessionStartRequests ;
	}

	public ServletContextHandler getHandler() {
		return handler;
	}

}
