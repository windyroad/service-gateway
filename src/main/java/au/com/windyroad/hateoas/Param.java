package au.com.windyroad.hateoas;

public class Param {

	private Class<?> type;
	private String validation;

	protected Param() {
	}

	public Param(Class<?> type, String validation) {
		this.type = type;
		this.validation = validation;
	}

	public Class<?> getType() {
		return type;
	}

	public String getValidation() {
		return validation;
	}

}
