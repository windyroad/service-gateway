package au.com.windyroad.hateoas.client;

import au.com.windyroad.hateoas.EmbeddedEntityHttpLink;
import au.com.windyroad.hateoas.EmbeddedEntityJavaLink;
import au.com.windyroad.hateoas.HttpLink;
import au.com.windyroad.hateoas.JavaLink;

public interface LinkVisitor {

    void visit(EmbeddedEntityJavaLink<?> embeddedEntityJavaLink);

    void visit(HttpLink httpLink);

    void visit(JavaLink javaLink);

    void visit(EmbeddedEntityHttpLink embeddedEntityHttpLink);

}
