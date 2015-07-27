package consumer.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import consumer.model.Product;
import consumer.service.ProductService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
	private final ProductListAssembler productAssembler;
	private final ProductService productService;

	@Autowired
	public ProductController(ProductListAssembler productAssembler, ProductService productService) {
		this.productAssembler = productAssembler;
		this.productService = productService;
	}

	@RequestMapping(method = RequestMethod.GET, value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	Resource<String> getApiProducts() {
		List<Link> links = new ArrayList<Link>();
		links.add(ControllerLinkBuilder.linkTo(methodOn(ProductController.class).getAllProducts()).withRel("awesome"));
		Resource<String> resource = new Resource<String>("This is the main entry for the product-api", links);
		return resource;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	Resources<Resource<Product>> getAllProducts() {
		List<Product> products = productService.retrieveAllProduct();
		return this.productAssembler.toResource(products);
	}
}
