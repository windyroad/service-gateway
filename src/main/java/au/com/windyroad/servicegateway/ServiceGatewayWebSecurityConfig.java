package au.com.windyroad.servicegateway;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
//@EnableWebMvcSecurity
public class ServiceGatewayWebSecurityConfig extends
		WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/index").permitAll().and()
				.authorizeRequests().antMatchers("/admin/proxy")
				.authenticated().anyRequest().hasRole("USER");
	}
}
