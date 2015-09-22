package au.com.windyroad.servicegateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class ServiceGatewayMvcConfig extends WebMvcConfigurationSupport {

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

    private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };

    private static final String[] RESOURCE_LOCATIONS;

    static {
        RESOURCE_LOCATIONS = new String[CLASSPATH_RESOURCE_LOCATIONS.length
                + SERVLET_RESOURCE_LOCATIONS.length];
        System.arraycopy(SERVLET_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS, 0,
                SERVLET_RESOURCE_LOCATIONS.length);
        System.arraycopy(CLASSPATH_RESOURCE_LOCATIONS, 0, RESOURCE_LOCATIONS,
                SERVLET_RESOURCE_LOCATIONS.length,
                CLASSPATH_RESOURCE_LOCATIONS.length);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**")
                    .addResourceLocations(RESOURCE_LOCATIONS);
        }

        // if (!registry.hasMappingForPattern("/**")) {
        // registry.addResourceHandler("/**")
        // .addResourceLocations(RESOURCE_LOCATIONS);
        // }
    }
}
