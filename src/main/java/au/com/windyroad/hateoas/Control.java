package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Control {

	private static final long serialVersionUID = 1L;

	@JsonProperty("rel")
	private String rel;

	@JsonProperty("href")
	private URI href;

	@JsonProperty("method")
	private RequestMethod[] method;

	@JsonProperty("params")
	private Map<String, Param> params = new HashMap<>();

	public Control(Method method, Object... pathParams)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		this.href = ControllerLinkBuilder.linkTo(method.getDeclaringClass(),
				method, pathParams).toUri();
		this.rel = getRelName(method);
		Parameter[] methodParams = method.getParameters();
		this.method = method.getAnnotation(RequestMapping.class).method();
		for (Parameter param : methodParams) {
			RequestParam requestParam = param.getAnnotation(RequestParam.class);
			if (requestParam != null) {
				Validation validation = param.getAnnotation(Validation.class);
				Class<?> type = param.getType();
				String validationMethodName = validation.value();
				String validator = (String) method.getDeclaringClass()
						.getMethod(validationMethodName).invoke(null);
				params.put(requestParam.value(), new Param(type, validator));
			}
		}
	}

	protected Control() {
	}

	private static String getRelName(Method method) {
		Rel rel = method.getAnnotation(Rel.class);
		return rel.value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rel == null) ? 0 : rel.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Control other = (Control) obj;
		if (rel == null) {
			if (other.rel != null)
				return false;
		} else if (!rel.equals(other.rel))
			return false;
		return true;
	}

	public String getRel() {
		return this.rel;
	}

	public URI getHref() {
		return this.href;
	}

	public Map<String, Param> getParams() {
		return this.params;
	}

}
