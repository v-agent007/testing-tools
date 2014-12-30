package co.wds.testingtools.annotations.mapperservlet;

import static co.wds.testingtools.annotations.MapperServletAnnotations.getRequests;
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
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.wds.testingtools.annotations.MapperServletAnnotations.RespondTo;
import co.wds.testingtools.annotations.MapperServletAnnotations.ResponseData;
import co.wds.testingtools.annotations.MapperServletAnnotations.TestServlet;

@TestServlet(port=54321, contentType="text/plain")
@RespondTo({
	@ResponseData(url="fake", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="samefake", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="fakeignoreparams", resourceFile="fake.json", contentType="application/json", ignoreParams=true),
	@ResponseData(url="samefakeignoreparams", resourceFile="fake.json", contentType="application/json", ignoreParams=true),
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
	public void getRequestsByUrlShouldStripParams() throws Exception {
		URL url = new URL("http://localhost:54321/fakeignoreparams?param1=one&param2=two");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		URL url2 = new URL("http://localhost:54321/samefakeignoreparams?animal=cow");
		connection = (HttpURLConnection)url2.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		
		url = new URL("http://localhost:54321/fakeignoreparams");
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		List<Request> requestsList = getRequests("/fakeignoreparams");
	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(2));
				
		assertThat(requestsList.get(0).url, is("/fakeignoreparams?param1=one&param2=two"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.GET));
		
		assertThat(requestsList.get(1).url, is("/fakeignoreparams"));
		assertThat(requestsList.get(1).body, is(""));
		assertThat(requestsList.get(1).type, is(Request.RequestType.GET));
		
		requestsList = getRequests("/samefakeignoreparams");	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(1));
				
		assertThat(requestsList.get(0).url, is("/samefakeignoreparams?animal=cow"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.GET));
	}
	
	@Test
	public void getRequestsByUrlShouldReturnAllRequests() throws Exception {
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		URL url2 = new URL("http://localhost:54321/samefake");
		connection = (HttpURLConnection)url2.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		
		url = new URL("http://localhost:54321/fake");
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		List<Request> requestsList = getRequests("/fake");
	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(2));
				
		assertThat(requestsList.get(0).url, is("/fake"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.POST));
		
		assertThat(requestsList.get(1).url, is("/fake"));
		assertThat(requestsList.get(1).body, is(""));
		assertThat(requestsList.get(1).type, is(Request.RequestType.POST));
		
		requestsList = getRequests("/samefake");	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(1));
				
		assertThat(requestsList.get(0).url, is("/samefake"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.POST));
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
