package au.com.windyroad.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SubEntity<T> extends Entity<T> {

    private String[] rel;

    /**
     * @return the rel
     */
    public String[] getRel() {
        return rel;
    }

    /**
     * @param rel
     *            the rel to set
     */
    public void setRel(String[] rel) {
        this.rel = rel;
    }
}
