package au.com.windyroad.servicegateway.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;

@HateoasController(ProxyController.class)
public class Proxy {

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    private String target;
    private String name;

    protected Proxy() {
    }

    public Proxy(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Autowired
    ApplicationContext context;

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    // public ResolvedEntity<Endpoint> getEndpoint(String target)
    // throws UnsupportedEncodingException {
    // String path = Endpoint.getUrl(target);
    // return (ResolvedEntity<Endpoint>) repository.get(path);
    // }

}
