package co.wds.testingtools.annotations.mapperservlet;

import static co.wds.testingtools.annotations.MapperServletAnnotations.mostRecentRequest;
import static co.wds.testingtools.annotations.MapperServletAnnotations.startMapperServlet;
import static co.wds.testingtools.annotations.MapperServletAnnotations.stopMapperServlet;
import static co.wds.testingtools.annotations.RandomAnnotation.randomise;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.wds.testingtools.annotations.MapperServletAnnotations.RespondTo;
import co.wds.testingtools.annotations.MapperServletAnnotations.ResponseData;
import co.wds.testingtools.annotations.MapperServletAnnotations.TestServlet;

@TestServlet(port=54321, contentType="text/plain")
@RespondTo({
	@ResponseData(url="fake", resourceFile="fake.json", contentType="application/json")
})
public class RequestCheckTest {
	@Before
	public void setupTest() {
		startMapperServlet(this);
	}

	@After
	public void teardownTest() {
		stopMapperServlet();
	}

	@Test
	public void shouldBeAbleToInterrogateTheRequest() throws Exception {
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		Request requestObject = mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.url, is("/fake"));
		assertThat(requestObject.body, is(""));
		assertThat(requestObject.type, is(Request.RequestType.GET));
	}
	
	@Test
	public void mostRecentRequestShouldTellUsTheRequestType() throws Exception {
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		Request requestObject = mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.url, is("/fake"));
		assertThat(requestObject.body, is(""));
		assertThat(requestObject.type, is(Request.RequestType.POST));
	}
	
	@Test
	public void shouldBeAbleToGetPostDataParameters() throws Exception {
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
		wr.writeBytes("json=blah");
		wr.flush ();
		wr.close ();
		
		assertThat(connection.getResponseCode(), is(200));
		
		Request requestObject = mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.url, is("/fake"));
		assertThat(requestObject.body, is("json=blah"));
		assertThat(requestObject.type, is(Request.RequestType.POST));
	}
	
	@Test
	public void shouldStoreHeadersInRequest() throws Exception {
		String customHeaderKey1 = randomise(String.class); 
		String customHeaderValue1 = randomise(String.class); 
		String customHeaderKey2 = randomise(String.class); 
		String customHeaderValue2 = randomise(String.class); 
		
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty(customHeaderKey1, customHeaderValue1);
		connection.setRequestProperty(customHeaderKey2, customHeaderValue2);
		
		assertThat(connection.getResponseCode(), is(200));
		
		Request requestObject = mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.headers.containsKey(customHeaderKey1), is(true));
		assertThat(requestObject.headers.containsKey(customHeaderKey2), is(true));
		assertThat(requestObject.headers.get(customHeaderKey1), is(customHeaderValue1));
		assertThat(requestObject.headers.get(customHeaderKey2), is(customHeaderValue2));
	}
}
