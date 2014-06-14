package co.wds.testingtools.annotations.mapperservlet;

public class Request {
	
	public enum RequestType { PUT, GET, POST };
	
	public String url;	
	public String body;
	public RequestType type;
}
