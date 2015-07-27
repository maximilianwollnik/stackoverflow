package provider.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import provider.model.Product;

@RepositoryRestResource
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
	List<Product> findByName(@Param("name") String name);

	List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
}
