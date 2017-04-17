package framework.basic;

import container.Queue;
import utils.Utils;

public class Port {
	private String name;
	private int id;
	private int type;
	private boolean isAvailable;
	private Queue queue;

	public Port() {
	}

	public Port(String name, int type) {
		this.name = name;
		this.id = this.hashCode();
		this.setType(type);
		this.isAvailable = true;
		this.queue = new Queue();
	}
	
	public String getPortTypeName() {
		if (this.getType() == Utils.PORT_IN)
			return "IN";
		else
			return "OUT";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

}
