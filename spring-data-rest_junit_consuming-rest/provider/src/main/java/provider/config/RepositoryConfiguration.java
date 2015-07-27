package provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import provider.model.Product;

@Configuration
public class RepositoryConfiguration extends RepositoryRestMvcConfiguration {
	
	@Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    	super.configureRepositoryRestConfiguration(config);
        config.exposeIdsFor(Product.class);
    }
}
