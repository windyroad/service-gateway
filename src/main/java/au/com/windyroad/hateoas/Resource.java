package au.com.windyroad.hateoas;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {

	@JsonProperty("_controls")
	private Set<Control> controls;

	public Resource() {
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

}
