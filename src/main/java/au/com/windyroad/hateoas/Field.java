package au.com.windyroad.hateoas;

public class Field {

	private Class<?> type;
	private String validation;

	protected Field() {
	}

	public Field(Class<?> type, String validation) {
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
