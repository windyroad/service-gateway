package au.com.windyroad.servicegateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.windyroad.hateoas.client.RestTemplateResolver;
import au.com.windyroad.hateoas.client.mixins.ActionMixin;
import au.com.windyroad.hateoas.client.mixins.EntityRelationshipMixin;
import au.com.windyroad.hateoas.client.mixins.LinkMixin;
import au.com.windyroad.hateoas.client.mixins.NavigationalRelationshipMixin;
import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.NavigationalRelationship;
import au.com.windyroad.hateoas.core.Resolver;

@Configuration
@Profile(value = "integration")
public class ServiceGatewayMapper {

    @Autowired
    RestTemplateResolver restTemplateResolver;

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void fixOm() {
        ObjectMapper om = context.getBean(ObjectMapper.class);
        om.addMixIn(Action.class, ActionMixin.class);
        om.addMixIn(Link.class, LinkMixin.class);
        om.addMixIn(EntityRelationship.class, EntityRelationshipMixin.class);
        om.addMixIn(NavigationalRelationship.class,
                NavigationalRelationshipMixin.class);
        om.setInjectableValues(new InjectableValues.Std()
                .addValue(Resolver.class, restTemplateResolver));
    }
}
