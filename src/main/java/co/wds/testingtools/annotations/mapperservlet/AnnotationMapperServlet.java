package co.wds.testingtools.annotations.mapperservlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class AnnotationMapperServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 570675440210200736L;

	private class MapperBinding {
		private final String resourceFile;
		private final String contentType;
		private final int status;
		private final boolean ignoreParams;

		public MapperBinding(String resourceFile, String contentType, int status, boolean ignoreParams) {
			this.resourceFile = resourceFile;
			this.contentType = contentType;
			this.status = status;
			this.ignoreParams = ignoreParams;
		}
	}
	
	
	private final Map<String, MapperBinding> bindings;
	private final ArrayList<Request> requests;
	private boolean requiresAuthentication;
	private String authenticationUserName;
	private String authenticationPassword;
	
	public ArrayList<Request> getRequests() {
		return requests;
	}

	public AnnotationMapperServlet() {
		this.bindings = new HashMap<String, MapperBinding>();
		this.requests = new ArrayList<Request>();
	}

	public void bindReponse(String url, String resourceFile, String contentType, int status, boolean ignoreParams) {
		if (ignoreParams && url.contains("?")) {
			String urlWithoutParams = url.split("\\?")[0];
			bindings.put(urlWithoutParams, new MapperBinding(resourceFile, contentType, status, ignoreParams));
		} else {
			bindings.put(url, new MapperBinding(resourceFile, contentType, status, ignoreParams));
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (bindings) {
			process(request, response, Request.RequestType.PUT);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (bindings) {
			process(request, response, Request.RequestType.GET);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (bindings) {
			process(request, response, Request.RequestType.POST);
		}
	}
	
	private boolean isAuthorized(HttpServletRequest request) {
		if (this.requiresAuthentication) {
			if (this.authenticationUserName != null && this.authenticationPassword != null) {
				String authHeader = request.getHeader("Authorization");
				try {
					String base64Auth = authHeader.split(" ")[1];
					byte[] decoded = Base64.decodeBase64(base64Auth);
					String decodedString = new String(decoded, "UTF-8");
					return decodedString.equals(this.authenticationUserName + ":" + this.authenticationPassword);
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}

	private void process(HttpServletRequest request, HttpServletResponse response, Request.RequestType type) throws IOException {
		if (!isAuthorized(request)) {
			response.sendError(401);
		}
		
		String urlString = getUrlString(request);
		Request requestObject = getRequestObject(request, urlString, type);

		requests.add(requestObject);

		MapperBinding binding = getBindingFor(urlString);
		
		if (binding != null) {
			response.setStatus(binding.status);
			
			response.setContentType(binding.contentType);
			String fileName = binding.resourceFile;
			
			if (!fileName.equals("")) {
				if (fileName.matches("[0-9]*")) {
					int status = Integer.valueOf(fileName);
					response.sendError(status);
				} else {
					String fileData = loadFileData("/data/" + fileName);
					if (fileData == null) {
						System.out.println(String.format("Could not find file in test/resources/data/%s", fileName));
						response.sendError(404);
					} else {
						response.getWriter().write(fileData);
					}
				}
				
			} else {
				response.setStatus(204);
				response.setContentType("text/html");
			}
			
		} else {
			response.sendError(404);
		}

		response.flushBuffer();
	}

	private MapperBinding getBindingFor(String url) {
		String paramlessUrl = url.split("\\?")[0];
		
		MapperBinding binding = bindings.get(paramlessUrl);
		if (binding == null || !binding.ignoreParams) {
			binding = bindings.get(url); 
		}
		
		return binding;
	}

	private Request getRequestObject(HttpServletRequest request, String urlString, Request.RequestType type) throws IOException {
		Request requestObject = new Request();
		requestObject.url = urlString;
		requestObject.path = request.getRequestURI();
		requestObject.body = IOUtils.toString(request.getInputStream(), "utf-8");
		requestObject.type = type;
		requestObject.headers = new HashMap<String, String>();
		requestObject.parameters = request.getParameterMap();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			
			requestObject.headers.put(headerName, headerValue);
		}
		
		return requestObject;
	}

	@SuppressWarnings("deprecation")
	private String getUrlString(HttpServletRequest request) {
		String urlString = request.getRequestURI();
		
		if (request.getQueryString() != null) {
			urlString = urlString + "?" + URLDecoder.decode(request.getQueryString());
		}
		return urlString;
	}

	protected String loadFileData(String fileName) {
		String result = null;
		InputStream resourceStream = this.getClass().getResourceAsStream(fileName);
		if (resourceStream != null) {
			try {
				result = IOUtils.toString(resourceStream, "utf-8");
			} catch (Exception e) {
				throw new RuntimeException("Could not read file: " + fileName);
			}
		}
		return result;
	}

	public void setRequiresAuthentication(boolean requiresAuthentication) {
		this.requiresAuthentication = requiresAuthentication;
	}

	public void setAuthenticationAllowedUserName(String userName) {
		this.authenticationUserName = userName;
	}

	public void setAuthenticationAllowedPassword(String password) {
		this.authenticationPassword = password;
	}

}