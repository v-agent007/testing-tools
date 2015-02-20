package co.wds.testingtools.annotations.mapperservlet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.google.common.net.HttpHeaders;

import co.wds.testingtools.annotations.MapperServlet.RespondTo;
import co.wds.testingtools.annotations.MapperServlet.ResponseData;
import co.wds.testingtools.annotations.MapperServlet.TestServlet;


@TestServlet(port=54321, contentType="text/plain")
@RespondTo({
	@ResponseData(url="params-ignored", resourceFile="fake.json", contentType="application/json", ignoreParams=true),
	@ResponseData(url="params-used", resourceFile="fake.json", contentType="application/json", ignoreParams=false),
	@ResponseData(url="params-default", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="params-value?a=1", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="with-header", resourceFile = "fake.json", locationHeader="http://my-location-header")
	})
public class ParameterTest extends BaseMapperServletTest {
	@Test
	public void shouldLoadFakeJsonFileIgnoringAnyParams() throws Exception {
		
		testServlet("http://localhost:54321/params-ignored", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-ignored?test-param=one", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-ignored?test-param=two", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-ignored?test-param=one&another=two", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-ignored?a=-1", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
	}
	
	@Test
	public void shouldLoadFakeJsonFileWithoutParamsAnd404IfThereareParams() throws Exception {
		testServlet("http://localhost:54321/params-used", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-used?test-param=one", 404);
		testServlet("http://localhost:54321/params-used?test-param=two", 404);
		testServlet("http://localhost:54321/params-used?test-param=one&another=two", 404);
		testServlet("http://localhost:54321/params-used?a=-1", 404);
	}
	
	@Test
	public void defaultBehaviourShouldBeToIgnoreParams() throws Exception {
		testServlet("http://localhost:54321/params-default", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
		testServlet("http://localhost:54321/params-default?test-param=one", 404);
		testServlet("http://localhost:54321/params-default?test-param=two", 404);
		testServlet("http://localhost:54321/params-default?test-param=one&another=two", 404);
		testServlet("http://localhost:54321/params-default?a=-1", 404);
	}
	
	@Test
	public void shouldRememberTheParametersWhenIgnored() throws Exception {
		testServlet("http://localhost:54321/params-ignored?test-param=one&another=two", 200);
		Request mostRecent = unit.mostRecentRequest();
		assertThat(mostRecent.parameters.get("test-param")[0], is("one"));
		assertThat(mostRecent.parameters.get("another")[0], is("two"));
	}
	
	@Test
	public void shouldRememberTheParametersWhenNotIgnored() throws Exception {
		testServlet("http://localhost:54321/params-value?a=1", 200);
		Request mostRecent = unit.mostRecentRequest();
		assertThat(mostRecent.parameters.get("a")[0], is("1"));
	}
	
	@Test
	public void shouldRememberTheHeadersWhenPassed() throws Exception {
		URL url = new URL("http://localhost:54321/with-header");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		
		assertThat(connection.getHeaderField(HttpHeaders.LOCATION), is("http://my-location-header"));
	}
}
