package au.com.windyroad.servicegateway.model;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.annotation.JsonInclude;

import au.com.windyroad.hateoas.EmbeddedEntity;
import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.annotations.Rel;

public class Proxy extends Entity<Proxy.Properties> {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class Properties implements Serializable {
        private String name;
        private String target;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the target
         */
        public String getTarget() {
            return target;
        }

        /**
         * @param target
         *            the target to set
         */
        public void setTarget(String target) {
            this.target = target;
        }
    }

    protected Proxy() {
        super(new Properties());
    }

    public Proxy(String name, String target) {
        super(new Properties());
        getProperties().name = name;
        getProperties().target = target;
    }

    public Proxy(String name) {
        super(new Properties());
        this.getProperties().name = name;
    }

    public String getTarget() {
        return this.getProperties().target;
    }

    public void setEndpoint(String target, boolean available) {
        for (EmbeddedEntity entity : super.getEntities()) {
            Endpoint endpoint = entity.toEntity(Endpoint.class);
            if (target != null
                    && target.equals(endpoint.getProperties().getTarget())) {
                endpoint.getProperties().setAvailable(available);
                return;
            }
        }
        super.addEmbeddedEntity(new Endpoint(target, available), Rel.ITEM);
    }

    public Endpoint getEndpoint(String target) {
        for (EmbeddedEntity entity : super.getEntities()) {
            Endpoint endpoint = entity.toEntity(Endpoint.class);
            if (target != null
                    && target.equals(endpoint.getProperties().getTarget())) {
                return endpoint;
            }
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }

    /**
     * @return the name
     */
    public String getName() {
        return getProperties().name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.getProperties().name = name;
    }

}
