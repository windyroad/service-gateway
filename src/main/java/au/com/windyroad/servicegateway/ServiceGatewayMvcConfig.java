package au.com.windyroad.servicegateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ServiceGatewayMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}

	// @Bean(name = "shiroFilter")
	// public ShiroFilterFactoryBean shiroFilter() {
	//
	// ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
	// shiroFilter.setLoginUrl("/sophia/*");
	// shiroFilter.setSecurityManager(securityManager());
	//
	// Map<String, Filter> filters = new HashMap<>();
	// filters.put("anon", new FormAuthenticationFilter());
	// filters.put("authc", new FormAuthenticationFilter());
	// shiroFilter.setFilters(filters);
	//
	// return shiroFilter;
	// }
}
