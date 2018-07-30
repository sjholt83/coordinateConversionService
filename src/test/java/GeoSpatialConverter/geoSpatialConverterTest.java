package GeoSpatialConverter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import mil.nga.ods.geotrans.GeoTransMaster;

@RunWith(SpringRunner.class)
@WebMvcTest(geoSpatialConverter.class)
public class geoSpatialConverterTest {

	private static final String API_PREFIX = "/api";
	String exampleHealthCheck = "I'm doing science and I'm still alive.";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Mock
	GeoTransMaster geoTransMaster;
	
	
	@Before
	public void init() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

	}

	@Test
	public void doTranslationPost() throws Exception {
		String fileName = "translationJson.txt";

		String returnJsonString = getTextFile("expectedTranslationJson.txt");
        JSONObject jsonObj = new JSONObject(returnJsonString);

		String contentJson = getTextFile(fileName);
		System.out.println("Content Body: " + contentJson);

		Mockito.when(geoTransMaster.doCoordinateTranslation(contentJson)).thenReturn(jsonObj);
	            
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(API_PREFIX + "/doTranslation")
				.accept(MediaType.APPLICATION_JSON).content(contentJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println("Response: " + result.getResponse().getContentAsString());
		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		// compare returned json
		System.out.println("expected: " + returnJsonString);
		assertThat(result.getResponse().getContentAsString()).isEqualTo(returnJsonString);
	}


	
	@Test
	public void doConversionPostBadRequest() throws Exception {
		// no content sent for post
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(API_PREFIX + "/doTranslation").accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println("doTranslation result: " + result.getResponse().getContentAsString());
		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
	}

	@Test
	public void doConversionPost() throws Exception {
		String fileName = "conversionJson.txt";

		String returnJsonString = getTextFile("returnJson.txt");
        JSONObject jsonObj = new JSONObject(returnJsonString);

		String contentJson = getTextFile(fileName);
		System.out.println("Content Body: " + contentJson);

		Mockito.when(geoTransMaster.doConversion(contentJson)).thenReturn(jsonObj);
	            
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(API_PREFIX + "/doConversion")
				.accept(MediaType.APPLICATION_JSON).content(contentJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println("Response: " + result.getResponse().getContentAsString());
		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

	}

	

	@Test
	public void getEllipsoids() throws Exception {

		String fileName = "ellipsoids.txt";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "/ellipsoids").accept(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println("Ellipsoids result: " + result.getResponse().getContentAsString());

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.APPLICATION_JSON.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(getTextFile(fileName));
	}

	@Test
	public void getDatums() throws Exception {

		String fileName = "datums.txt";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "/datums").accept(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println("Datums result: " + result.getResponse().getContentAsString());

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.APPLICATION_JSON.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(getTextFile(fileName));
	}

	@Test
	public void getCoordinateTypes() throws Exception {

		String fileName = "coordinateTypes.txt";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "/coordinateTypes")
				.accept(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println("result: " + result.getResponse().getContentAsString());

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.APPLICATION_JSON.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(getTextFile(fileName));
	}

	@Test
	public void getSourceCoordinateInputByType() throws Exception {

		String fileName = "sourceCoordinateInputByType.txt";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "/sourceCoordinateInputByType")
				.accept(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println("result1: " + result.getResponse().getContentAsString());

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.APPLICATION_JSON.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(getTextFile(fileName));
	}

	@Test
	public void getEndpoints() throws Exception {

		String resultStr = "Endpoints:\n/coordinateTypes\n/datums\n/doTranslation\n/doConversion\n/ellipsoids\n/sourceCoordinateInputByType\n/health";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "").accept(MediaType.TEXT_PLAIN);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println("result: " + result.getResponse().getContentAsString());

		System.out.println("expected result: " + resultStr);
		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.TEXT_PLAIN.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(resultStr);

	}

	@Test
	public void doHealthCheck() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(API_PREFIX + "/health").accept(MediaType.TEXT_PLAIN);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getResponse().getContentType().toString()).isEqualTo(MediaType.TEXT_PLAIN.toString());
		assertThat(result.getResponse().getContentAsString()).isEqualTo(exampleHealthCheck);

	}

	private String getTextFile(String fileName) throws IOException {

		Path path = null;
		try {
			path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuilder data = new StringBuilder();
		Stream<String> lines = Files.lines(path);
		lines.forEach(line -> data.append(line).append("\n"));
		lines.close();

		return (data.toString().trim());
	}

	
}
