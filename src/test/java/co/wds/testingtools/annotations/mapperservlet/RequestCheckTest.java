package co.wds.testingtools.annotations.mapperservlet;

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

import co.wds.testingtools.annotations.MapperServlet;
import co.wds.testingtools.annotations.MapperServlet.RespondTo;
import co.wds.testingtools.annotations.MapperServlet.ResponseData;
import co.wds.testingtools.annotations.MapperServlet.TestServlet;

@TestServlet(port=54321, contentType="text/plain")
@RespondTo({
	@ResponseData(url="/fake", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="/samefake", resourceFile="fake.json", contentType="application/json"),
	@ResponseData(url="/fake/ignoreparams", resourceFile="fake.json", contentType="application/json", ignoreParams=true),
	@ResponseData(url="/samefake/ignoreparams", resourceFile="fake.json", contentType="application/json", ignoreParams=true),
})
public class RequestCheckTest {
		
	MapperServlet mapperServlet = new MapperServlet(this);
	
	@Before
	public void setupTest() {
		mapperServlet.start();
	}

	@After
	public void teardownTest() {
		mapperServlet.stop();
	}

	@Test
	public void shouldBeAbleToInterrogateTheRequest() throws Exception {
		URL url = new URL("http://localhost:54321/fake");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		Request requestObject = mapperServlet.mostRecentRequest();
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
		
		Request requestObject = mapperServlet.mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.url, is("/fake"));
		assertThat(requestObject.body, is(""));
		assertThat(requestObject.type, is(Request.RequestType.POST));
	}
	
	@Test
	public void getRequestsByUrlShouldStripParams() throws Exception {
		URL url = new URL("http://localhost:54321/fake/ignoreparams?param1=one&param2=two");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		URL url2 = new URL("http://localhost:54321/samefake/ignoreparams?animal=cow");
		connection = (HttpURLConnection)url2.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		
		url = new URL("http://localhost:54321/fake/ignoreparams");
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		assertThat(connection.getResponseCode(), is(200));
		
		List<Request> requestsList = mapperServlet.getRequests("/fake/ignoreparams");
	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(2));
				
		assertThat(requestsList.get(0).url, is("/fake/ignoreparams?param1=one&param2=two"));
		assertThat(requestsList.get(0).path, is("/fake/ignoreparams"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.GET));
		
		assertThat(requestsList.get(1).url, is("/fake/ignoreparams"));
		assertThat(requestsList.get(1).path, is("/fake/ignoreparams"));
		assertThat(requestsList.get(1).body, is(""));
		assertThat(requestsList.get(1).type, is(Request.RequestType.GET));
		
		requestsList = mapperServlet.getRequests("/samefake/ignoreparams");	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(1));
				
		assertThat(requestsList.get(0).url, is("/samefake/ignoreparams?animal=cow"));
		assertThat(requestsList.get(0).path, is("/samefake/ignoreparams"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.GET));
	}
	
	@Test
	public void shoudHaveUrlEqualToPathWithNoParams() throws Exception {
		URL url = new URL("http://localhost:54321/fake/ignoreparams");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		Request unit = mapperServlet.mostRecentRequest();
		assertThat(unit.url, is("/fake/ignoreparams"));
		assertThat(unit.path, is(unit.url));
	}

	@Test
	public void shoudHaveUrlDifferentToPathWithParams() throws Exception {
		URL url = new URL("http://localhost:54321/fake/ignoreparams?param1=one&param2=two");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		assertThat(connection.getResponseCode(), is(200));
		
		Request unit = mapperServlet.mostRecentRequest();
		assertThat(unit.url, is("/fake/ignoreparams?param1=one&param2=two"));
		assertThat(unit.path, is("/fake/ignoreparams"));
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
		
		List<Request> requestsList = mapperServlet.getRequests("/fake");
	
		assertThat(requestsList, is(not(nullValue())));
		assertThat(requestsList.size(), is(2));
				
		assertThat(requestsList.get(0).url, is("/fake"));
		assertThat(requestsList.get(0).body, is(""));
		assertThat(requestsList.get(0).type, is(Request.RequestType.POST));
		
		assertThat(requestsList.get(1).url, is("/fake"));
		assertThat(requestsList.get(1).body, is(""));
		assertThat(requestsList.get(1).type, is(Request.RequestType.POST));
		
		requestsList = mapperServlet.getRequests("/samefake");	
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
		
		Request requestObject = mapperServlet.mostRecentRequest();
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
		
		Request requestObject = mapperServlet.mostRecentRequest();
		assertThat(requestObject, is(not(nullValue())));
		assertThat(requestObject.headers.containsKey(customHeaderKey1), is(true));
		assertThat(requestObject.headers.containsKey(customHeaderKey2), is(true));
		assertThat(requestObject.headers.get(customHeaderKey1), is(customHeaderValue1));
		assertThat(requestObject.headers.get(customHeaderKey2), is(customHeaderValue2));
	}
}
