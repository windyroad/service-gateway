package au.com.windyroad.servicegateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

@Configuration
public class ServiceGatewaySerializationConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    @Primary
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.applicationContext(context);
        HandlerInstantiator handlerInstantiator = new SpringHandlerInstantiator(
                context.getAutowireCapableBeanFactory());
        builder.handlerInstantiator(handlerInstantiator);
        return builder;
    }

}
