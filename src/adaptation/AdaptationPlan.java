package adaptation;

import java.util.ArrayList;

public class AdaptationPlan {
	private String id;
	private ArrayList<String> actions = new ArrayList<String>();
	private ArrayList<Object> elements = new ArrayList<Object>();

	public ArrayList<String> getActions() {
		return actions;
	}

	public void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}

	public ArrayList<Object> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Object> elements) {
		this.elements = elements;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
