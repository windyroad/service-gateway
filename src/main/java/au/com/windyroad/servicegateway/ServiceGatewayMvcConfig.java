package au.com.windyroad.servicegateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class ServiceGatewayMvcConfig extends WebMvcConfigurerAdapter {

    // @Override
    // public void configureContentNegotiation(
    // ContentNegotiationConfigurer configurer) {
    // configurer.favorPathExtension(true).favorParameter(true)
    // .parameterName("mediaType").ignoreAcceptHeader(false)
    // .useJaf(false).defaultContentType(MediaType.APPLICATION_JSON)
    // .mediaType("xml", MediaType.APPLICATION_XML)
    // .mediaType("json", MediaType.APPLICATION_JSON);
    // }

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
