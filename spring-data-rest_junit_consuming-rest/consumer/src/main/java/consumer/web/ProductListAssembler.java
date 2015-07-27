package consumer.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import consumer.model.Product;

@Component
public class ProductListAssembler implements ResourceAssembler<List<Product>, Resources<Resource<Product>>>{

	@Override
	public Resources<Resource<Product>> toResource(List<Product> entity) {
		List<Resource<Product>> products = entity.stream().map(food -> toResource(food)).collect(Collectors.toList());
		Resources<Resource<Product>> foodResources = new Resources<Resource<Product>>(products);
		foodResources.add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
		return foodResources;
	}

	private Resource<Product> toResource(Product food) {
		Resource<Product> resource = new Resource<Product>(food);
		resource.add(
				linkTo(methodOn(ProductController.class).getAllProducts()).slash("product").slash(food.getId()).withSelfRel());
		return resource;
	}
}
