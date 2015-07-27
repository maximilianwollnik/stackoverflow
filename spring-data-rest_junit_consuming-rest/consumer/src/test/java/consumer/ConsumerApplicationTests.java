package consumer;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import consumer.model.Product;
import consumer.service.ProductServiceImpl;
import consumer.service.util.CustomRestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConsumerApplication.class)
@WebAppConfiguration
@IntegrationTest
public class ConsumerApplicationTests {
	private MockRestServiceServer mockServer;
	private RestTemplate restTemplate;
	
	@Mock
	private CustomRestTemplate customRestTemplate;

	@InjectMocks
	private ProductServiceImpl productServiceImpl;
	
	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    this.restTemplate = new RestTemplate();
		this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
		String responseBody = "\"_links\" : {    \"self\" : {      \"href\" : \"http://localhost:1234/products/list\"    }  },  \"_embedded\" : {    \"products\" : [ {      \"name\" : \"Product 1\",      \"price\" : 0.99,      \"_links\" : {        \"self\" : {          \"href\" : \"http://localhost:1234/v1/products/list/product/1\"        }      }    }]  }}";
		this.mockServer.expect(requestTo("http://localhost:1234/products?size=" + Integer.MAX_VALUE)).andExpect(method(HttpMethod.GET))
		.andRespond(withSuccess(responseBody, MediaType.parseMediaTypes("application/hal+json").get(0)));
	}

	@Test
	public void contextLoads() {
		when(customRestTemplate.getRestTemplateJackson2HttpMessageConverter()).thenReturn(restTemplate);
		
		List<Product> productList = productServiceImpl.retrieveAllProduct();
		assertEquals("Expected one product", 1, productList.size());
		mockServer.verify();
	}

}
