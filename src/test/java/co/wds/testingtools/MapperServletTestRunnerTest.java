package co.wds.testingtools;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.wds.testingtools.annotations.MapperServlet.RespondTo;
import co.wds.testingtools.annotations.MapperServlet.ResponseData;
import co.wds.testingtools.annotations.MapperServlet.TestServlet;

@RunWith(MapperServletTestRunner.class)
@TestServlet(port=54321)
@RespondTo({
	@ResponseData(url="test", resourceFile="test.html")
})
public class MapperServletTestRunnerTest {
	@Test
	public void shouldResponseToBaseData() throws Exception {
		testServlet("http://localhost:54321/test", 200);
	}
	
	@Test
	public void shouldNotRespondToSomethingNotDefinedInTheHeader() throws Exception {
		testServlet("http://localhost:54321/hamlet", 404); 
	}
	
	@Test
	@ResponseData(url="hamlet", resourceFile="hamlet.txt")
	public void shouldRespondToResponseDefinedInTheTestMethodAnnotation() throws Exception {
		testServlet("http://localhost:54321/hamlet", 200);
	}
	
	protected void testServlet(String theUrl, int expectedHttpStatusCode) throws Exception {
		URL url = new URL(theUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(expectedHttpStatusCode));
	}
}
