package co.wds.testingtools.annotations.mapperservlet;

import org.junit.Test;

import co.wds.testingtools.annotations.MapperServlet.RespondTo;
import co.wds.testingtools.annotations.MapperServlet.ResponseData;
import co.wds.testingtools.annotations.MapperServlet.TestServlet;


@TestServlet(port=9999)
@RespondTo({
	@ResponseData(url="testing/1234", resourceFile="test1234.json"),
	@ResponseData(url="fake", resourceFile="fake.json"),
	@ResponseData(resourceFile="default.json"),
	@ResponseData(url="teapot", resourceFile="fake.json", status=418),
	@ResponseData(url="filenotfound", resourceFile="does_not_exist")
	})
public class MapperServletAnnotationsTest extends BaseMapperServletTest {
	
	@Test
	public void fakeTestToMakeSureServletHasStarted() throws Exception {
		testServlet("http://localhost:9999/testing/1234", 200, "application/json", "{\r\t\"value\":\"5678\"\r}");
	}
	
	@Test
	public void shouldLoadDifferentDataForDifferentUrl() throws Exception {
		testServlet("http://localhost:9999/fake", 200, "application/json", "{\r\t\"fake\":\"true\"\r}");
	}
	
	@Test
	public void shouldLoadDataOnBaseUrl() throws Exception {
		testServlet("http://localhost:9999/", 200, "application/json", "{\r\t\"default\":\"file\"\r}");
	}
	
	@Test
	public void shouldReturnCustomStatus() throws Exception {
		testServlet("http://localhost:9999/teapot", 418);
	}
	
	@Test
	public void shouldReturn404IfUrlNotFound() throws Exception {
		testServlet("http://localhost:9999/notFound", 404);
	}
	
	@Test
	public void shouldReturn404IfFileIsNotFoundForKnownUrl() throws Exception {
		testServlet("http://localhost:9999/filenotfound", 404);
	}
}