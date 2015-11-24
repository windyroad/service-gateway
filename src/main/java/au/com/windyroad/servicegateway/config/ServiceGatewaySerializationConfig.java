package au.com.windyroad.servicegateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

import au.com.windyroad.hateoas.serialization.AutowiringDeserializer;
import au.com.windyroad.hateoas.serialization.SpringBeanHandlerInstantiator;

@Configuration
public class ServiceGatewaySerializationConfig {

    @Autowired
    private ApplicationContext context;

    @Bean(name = "customObjectMapperBuilder")
    @Primary
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.applicationContext(context);
        HandlerInstantiator handlerInstantiator = new SpringBeanHandlerInstantiator(
                context);
        // context.getAutowireCapableBeanFactory());
        builder.handlerInstantiator(handlerInstantiator);

        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config, BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {
                return new AutowiringDeserializer(context, deserializer);
            }
        });

        builder.modules(module);
        return builder;
    }

}
