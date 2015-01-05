package co.wds.testingtools.annotations.mapperservlet;

import java.util.Arrays;
import java.util.Map;

import com.google.common.base.Joiner;

public class Request {
	
	public enum RequestType { PUT, GET, POST };
	
	public String url;
	public String path;
	public String body;
	public RequestType type;
	public Map<String, String> headers;
	public Map<String, String[]> parameters;
	
	@Override
	public String toString(){
		StringBuilder requestLog = new StringBuilder();
		requestLog.append("\n\t url:"+this.url+"\n");
		requestLog.append("\t path:"+this.path+"\n");
		requestLog.append("\t body:"+this.body+"\n");
		requestLog.append("\t type:"+this.type+"\n");
		logParameters(requestLog,this.parameters);
		requestLog.append("\t headers:"+ Joiner.on(',').withKeyValueSeparator("=").join(this.headers));	
		requestLog.append("\n");
		return requestLog.toString();
	}
	
	private void logParameters(StringBuilder requestStringBuilder, Map<String, String[]> parameters){
		requestStringBuilder.append("\t parameters:");
		for (String key : parameters.keySet()){
			requestStringBuilder.append(key+"=");
			requestStringBuilder.append(Arrays.toString(parameters.get(key)));
			requestStringBuilder.append(" ");
		}
		requestStringBuilder.append("\n");
	}
}
