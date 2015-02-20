package co.wds.testingtools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.wds.testingtools.annotations.mapperservlet.AnnotationMapperServlet;
import co.wds.testingtools.annotations.mapperservlet.Request;
import co.wds.testingtools.annotations.mapperservlet.TestingServer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class MapperServlet {
	
	private static final Logger logger = LoggerFactory.getLogger(MapperServlet.class);
	private static MapperServlet mapperServletInstance;
	private TestingServer server;
	private AnnotationMapperServlet annotationMapperServlet;

	@SuppressWarnings("unused")
	private MapperServlet() {
	}

	public MapperServlet(Object testObject) {
		try {
			processAnnotations(Class.forName(testObject.getClass().getName()), testObject);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void startMapperServlet(Object testObject) {
		mapperServletInstance = new MapperServlet(testObject);
		mapperServletInstance.start();
	}

	public static void stopMapperServlet() {
		if (mapperServletInstance != null) {
			mapperServletInstance.stop();
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface TestServlet {
		int port() default 80;

		String contentType() default "application/json";

		boolean requiresAuthentication() default false;

		String userName() default "";

		String password() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RespondTo {
		ResponseData[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ResponseData {
		String url() default "/";

		String resourceFile();

		String contentType() default "";

		int status() default 200;

		boolean ignoreParams() default false;

		String locationHeader() default "";
	}

	public void start() {
		if (server != null) {
			try {
				server.start();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void stop() {
		if (server != null) {
			try {
				server.stop();
				server = null;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public static void clearRecentRequests() {
		if (mapperServletInstance != null) {
			mapperServletInstance.clearRequests();
		}
	}
	
	public static Request mostRecent() {
		if (mapperServletInstance != null) {
			return mapperServletInstance.mostRecentRequest();
		} else {
			return null;
		}
	}
	
	public static List<Request> allRequests(String forPath) {
		if (mapperServletInstance != null) {
			return mapperServletInstance.getRequests(forPath);
		} else {
			return Collections.emptyList();
		}
	}
	
	
	
	public void clearRequests(){
		annotationMapperServlet.getRequests().clear();
	}

	public Request mostRecentRequest() {
		ArrayList<Request> requests = annotationMapperServlet.getRequests();
		if(requests.isEmpty()) {
			return null;
		}
		return requests.get(requests.size() - 1);
	}

	public List<Request> getRequests(String forPath) {
		List<Request> result = Lists.newArrayList();
		String path;
		for (Request request : annotationMapperServlet.getRequests()) {
			path = request.path;
			if (!Strings.isNullOrEmpty(path) && path.equals(forPath)) {
				result.add(request);
			}
		}
		return result;
	}

	public void addMapping(String uri, String resourceFile, String contentType, int status, boolean ignoreParams, String locationHeader) {
		if (!uri.startsWith("/")) {
			uri = "/" + uri;
		}

		if (annotationMapperServlet != null) {
			annotationMapperServlet.bindReponse(uri, resourceFile, contentType, status, ignoreParams, locationHeader);
		}
	}

	private void processAnnotations(final Class<? extends Object> testClass, Object testObject) throws Exception {
		Class<? extends Object> localTestClass = findAnnotatedSuperClass(testClass);

		if (localTestClass == null) {
			throw new IllegalStateException("Annotation not found");
		}

		TestServlet testServlet = localTestClass.getAnnotation(TestServlet.class);
		createServer(testServlet);

		RespondTo respondTo = localTestClass.getAnnotation(RespondTo.class);
		addResponsesFrom(respondTo, testServlet);
	}

	private Class<? extends Object> findAnnotatedSuperClass(final Class<? extends Object> testClass) {
		Class<? extends Object> curClass = testClass;
		while (curClass.getAnnotation(TestServlet.class) == null) {
			curClass = curClass.getSuperclass();
			if (curClass == Object.class) {
				return null;
			}
		}

		Class<? extends Object> localTestClass = curClass;
		return localTestClass;
	}

	private void createServer(TestServlet testServlet) {
		if (testServlet != null) {
			server = new TestingServer(testServlet.port());
		}
	}

	private void addResponsesFrom(RespondTo respondTo, TestServlet testServlet) {
		annotationMapperServlet = new AnnotationMapperServlet();
		annotationMapperServlet.setRequiresAuthentication(testServlet.requiresAuthentication());
		annotationMapperServlet.setAuthenticationAllowedUserName(testServlet.userName());
		annotationMapperServlet.setAuthenticationAllowedPassword(testServlet.password());

		if (server != null && respondTo != null) {
			for (ResponseData response : respondTo.value()) {
				String contentType = response.contentType();
				String uri = response.url();
				String resourceFile = response.resourceFile();
				int status = response.status();
				boolean ignoreParams = response.ignoreParams();
				String locationHeader = response.locationHeader();
				if ("".equals(contentType)) {
					contentType = testServlet.contentType();
				}

				addMapping(uri, resourceFile, contentType, status, ignoreParams, locationHeader);
			}
		}

		ServletHolder holder = new ServletHolder(annotationMapperServlet);
		server.getHandler().addServlet(holder, "/");
	}

}
