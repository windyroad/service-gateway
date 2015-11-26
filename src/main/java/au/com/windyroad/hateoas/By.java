package au.com.windyroad.hateoas;

public abstract class By {

    public static By relationship(String relationship,
            String... additionalRelationships) {
        return new ByRelationship(relationship, additionalRelationships);
    }

    // public static By nature(String nature)
}
