package co.wds.testingtools.annotations.mapperservlet;

import java.util.Map;

public class Request {
	
	public enum RequestType { PUT, GET, POST };
	
	public String url;
	public String path;
	public String body;
	public RequestType type;
	public Map<String, String> headers;
	public Map<String, String[]> parameters;
}
