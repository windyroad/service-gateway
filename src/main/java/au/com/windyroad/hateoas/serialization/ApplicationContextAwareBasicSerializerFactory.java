package au.com.windyroad.hateoas.serialization;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

public class ApplicationContextAwareBasicSerializerFactory
        extends BeanSerializerFactory {

    /**
     * 
     */
    private static final long serialVersionUID = 4318419621778758742L;
    private ApplicationContext applicationContext;

    public ApplicationContextAwareBasicSerializerFactory(
            SerializerFactoryConfig config,
            ApplicationContext applicationContext) {
        super(config);
        this.applicationContext = applicationContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fasterxml.jackson.databind.ser.BasicSerializerFactory#
     * findSerializerFromAnnotation(com.fasterxml.jackson.databind.
     * SerializerProvider, com.fasterxml.jackson.databind.introspect.Annotated)
     */
    @Override
    protected JsonSerializer<Object> findSerializerFromAnnotation(
            SerializerProvider prov, Annotated a) throws JsonMappingException {
        JsonSerialize serialiseAnnotation = a
                .getAnnotation(JsonSerialize.class);

        if (serialiseAnnotation != null) {
            JsonSerializer<?> jsonSerializer = applicationContext
                    .getBean(serialiseAnnotation.using());
            if (jsonSerializer != null)
                return (JsonSerializer<Object>) jsonSerializer;
        }
        return super.findSerializerFromAnnotation(prov, a);
    }

}