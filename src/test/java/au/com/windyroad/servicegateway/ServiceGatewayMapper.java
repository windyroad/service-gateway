package au.com.windyroad.servicegateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.windyroad.hateoas.client.RestTemplateResolver;

@Configuration
@Profile(value = "integration")
public class ServiceGatewayMapper {

    @Autowired
    RestTemplateResolver restTemplateResolver;

    // @Autowired
    // Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
    //
    //
    // @Bean
    // @Profile({ "integration", "ui-integration" })
    // public ObjectMapper objectMapper() {
    // ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();
    // objectMapper.setInjectableValues(new InjectableValues.Std()
    // .addValue(RestTemplateResolver.class, restTemplateResolver));
    // return objectMapper;
    // }

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void fixOm() {
        ObjectMapper om = context.getBean(ObjectMapper.class);
        om.setInjectableValues(new InjectableValues.Std()
                .addValue(RestTemplateResolver.class, restTemplateResolver));
    }
}
