package au.com.windyroad.hateoas;

import java.net.URI;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.http.MediaType;

public interface Resolver {

    public <T> T resolve(URI address, @Nullable MediaType representationFormat,
            Class<T> responseType);

}
