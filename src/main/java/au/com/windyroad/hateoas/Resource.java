package au.com.windyroad.hateoas;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource<T> {

	@JsonProperty("_content")
	private T content;

	@JsonProperty("_controls")
	private Set<Control> controls;

	public Resource(T content) {
		this.content = content;
		this.controls = new HashSet<>();
	}

	public Resource() {
		this.content = null;
		this.controls = new HashSet<>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addControl(Control control) {
		this.controls.add(control);
	}

	public Control getControl(String rel) {
		for (Control control : controls) {
			if (control.getRel().equals(rel)) {
				return control;
			}
		}
		return null;
	}

	public T getContent() {
		return this.content;
	}

}
