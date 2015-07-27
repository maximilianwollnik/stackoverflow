package consumer.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import consumer.model.Product;
import consumer.service.util.CustomRestTemplate;

@Service
public class ProductServiceImpl implements ProductService {
	private final CustomRestTemplate customRestTemplate;
	
	@Autowired
	public ProductServiceImpl(CustomRestTemplate customRestTemplate) {
		this.customRestTemplate = customRestTemplate;
	}

	@Override
	public List<Product> retrieveAllProduct() {
		List<Product> productList = new ArrayList<Product>();
		try {
			URI uri = new URI("http://localhost:1234/products?size=" + Integer.MAX_VALUE); 

			RestTemplate restTemplate = customRestTemplate.getRestTemplateJackson2HttpMessageConverter();
			ResponseEntity<PagedResources<Product>> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.GET,
					null, new ParameterizedTypeReference<PagedResources<Product>>() {
					});
			PagedResources<Product> resources = responseEntity.getBody();
			productList = new ArrayList<Product>(resources.getContent());
		} catch (URISyntaxException use) {
		}
		return productList;
	}
}
