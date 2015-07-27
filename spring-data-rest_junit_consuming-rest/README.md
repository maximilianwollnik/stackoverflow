# spring-data-rest_junit_consuming-rest

How do I write a JUnit test to consume a PagedResources<T> in Spring Boot?

In this example I face a problem to write a junit test, which consumes a HAL-formatted rest-service. As I understand I can use MockRestServiceServer to fake a communication. You can find [here](https://github.com/maximilianwollnik/stackoverflow/spring-data-rest_junit_consuming-rest) two simple spring applications; one which provides a HAL-formatted rest-service and one which consumes it. Everything works fine, when both services are started up.

So, when you start both services with the command mvn spring-boot:run and you navigate to http://localhost:8080/products/list, then you can see the consumed rest-service.

The consumer itself uses a modified RestTemplate to request a response entity from the type PagedResources<Product>. My consumer-test defines a response body, which looks exactly similar to a normal request from the "provider" service. 

**My Test**

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


When I execute the test I receive that error:

    org.springframework.http.converter.HttpMessageNotReadableException: Could not read document: Can not instantiate value of type [simple type, class org.springframework.hateoas.PagedResources<consumer.model.Product>] from String value ('_links'); no single-String constructor/factory method
    at [Source: java.io.ByteArrayInputStream@1966492; line: 1, column: 1]; nested exception is com.fasterxml.jackson.databind.JsonMappingException: Can not instantiate value of type [simple type, class org.springframework.hateoas.PagedResources<consumer.model.Product>] from String value ('_links'); no single-String constructor/factory method
    at [Source: java.io.ByteArrayInputStream@1966492; line: 1, column: 1]
    at org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.readJavaType(AbstractJackson2HttpMessageConverter.java:208)
    at org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.read(AbstractJackson2HttpMessageConverter.java:200)
    at org.springframework.web.client.HttpMessageConverterExtractor.extractData(HttpMessageConverterExtractor.java:97)
    at org.springframework.web.client.RestTemplate$ResponseEntityResponseExtractor.extractData(RestTemplate.java:809)
    at org.springframework.web.client.RestTemplate$ResponseEntityResponseExtractor.extractData(RestTemplate.java:793)
    at org.springframework.web.client.RestTemplate.doExecute(RestTemplate.java:572)
    at org.springframework.web.client.RestTemplate.execute(RestTemplate.java:530)
    at org.springframework.web.client.RestTemplate.exchange(RestTemplate.java:476)
    at consumer.service.ProductServiceImpl.retrieveAllProduct(ProductServiceImpl.java:35)
    at consumer.ConsumerApplicationTests.contextLoads(ConsumerApplicationTests.java:58)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:497)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
    at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:73)
    at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:82)
    at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:73)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:224)
    at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:83)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
    at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:68)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:163)
    at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:86)
    at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)
    Caused by: com.fasterxml.jackson.databind.JsonMappingException: Can not instantiate value of type [simple type, class org.springframework.hateoas.PagedResources<consumer.model.Product>] from String value ('_links'); no single-String constructor/factory method
    at [Source: java.io.ByteArrayInputStream@1966492; line: 1, column: 1]
    at com.fasterxml.jackson.databind.JsonMappingException.from(JsonMappingException.java:148)
    at com.fasterxml.jackson.databind.DeserializationContext.mappingException(DeserializationContext.java:770)
    at com.fasterxml.jackson.databind.deser.ValueInstantiator._createFromStringFallbacks(ValueInstantiator.java:277)
    at com.fasterxml.jackson.databind.deser.std.StdValueInstantiator.createFromString(StdValueInstantiator.java:289)
    at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.deserializeFromString(BeanDeserializerBase.java:1141)
    at com.fasterxml.jackson.databind.deser.BeanDeserializer._deserializeOther(BeanDeserializer.java:135)
    at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:126)
    at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:3066)
    at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:2221)
    at org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.readJavaType(AbstractJackson2HttpMessageConverter.java:205)
    ... 39 more

If I am not completely wrong, the error occurrs because PagedResources<T> does not have a String constructor. So what would be the best solution to test that code? 

Thank you in advance!
