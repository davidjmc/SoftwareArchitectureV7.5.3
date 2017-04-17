package container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import framework.configuration.*;
import framework.basic.Element;
import utils.MyError;
import utils.Utils;

public class ElementExecutor implements Runnable {
	private Element element;
	private volatile boolean running = true;
	private volatile boolean paused = false;
	private volatile boolean processing = true;
	private Thread thread;

	public Element getElement() {
		return this.element;
	}

	public void setElement(Element e) {
		this.element = e;
	}

	public ElementExecutor() {
	}

	public ElementExecutor(Element e) {
		this.element = e;
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public void pause() {
		processing = false;
		paused = true;
	}

	public void resume() {
		processing = true;
		paused = false;
	}

	public void run() {
		Set<ActionEdge> nextEdges;
		int nextVertex = 0;
		ActionEdge nextEdge = null;
		DirectedGraph<Integer, ActionEdge> graph = new DefaultDirectedGraph<>(ActionEdge.class);

		// check if behavior is empty
		graph = element.getSemantics().getGraph();
		if (graph.vertexSet().isEmpty())
			new MyError("Behaviour graph of " + element.getIdentification().getName() + " is empty", Utils.FATAL_ERROR)
					.print();

		while (running) {
			while (processing) {
				nextEdges = graph.outgoingEdgesOf(nextVertex);
				if (nextEdges.size() == 1) {
					nextEdge = nextEdges.iterator().next();
				} else if (nextEdges.size() == 0 || nextEdges.size() > 1)
					new MyError("Behaviour of " + element.getIdentification().getName() + " is wrong ",
							Utils.FATAL_ERROR).print();
				executeAction(relabel(nextEdge.getAction()));
				nextVertex = graph.getEdgeTarget(nextEdge);
			}
			while (paused) {
			}
		}
	}

	public void runOld() {
		String[] actions;
		String behaviour;

		// check if behavior is empty
		behaviour = element.getSemantics().getRuntimeBehaviour().getActions();
		if (behaviour.isEmpty())
			new MyError("Behaviour of " + element.getIdentification().getName() + " is empty", Utils.FATAL_ERROR)
					.print();

		while (running) {
			while (processing) {
				actions = element.getSemantics().getRuntimeBehaviour().getActions().split(Utils.PREFIX_ACTION);
				for (int idx = 0; idx < actions.length; idx++) {
					executeAction(relabelOld(actions, idx));
				}
				while (paused) {
				}
			}
		}
	}

	public String relabel(String action) {
		String newActionName = new String();

		if (action.contains("i_")) {
			String oldLabel = action.substring(5, 6).toLowerCase() + action.substring(6,9).trim()+action.substring(action.indexOf("."),action.length()).trim();
			String newLabel = this.element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
			newActionName = action.substring(0,action.indexOf(".")) + newLabel.substring(newLabel.indexOf("."), newLabel.length());
		} else {
			String oldLabel = action;
			newActionName = this.element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
		}
		return newActionName;
	}

	public String relabelOld(String[] actions, int idx) {
		String newActionName = new String();

		if (actions[idx].contains("i_Pre")) {
			String oldLabel = actions[idx + 1];
			String newLabel = this.element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
			newActionName = actions[idx] + newLabel.substring(newLabel.indexOf("."), newLabel.length());
		} else if (actions[idx].contains("i_Pos")) {
			String oldLabel = actions[idx - 1];
			String newLabel = this.element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
			newActionName = actions[idx] + newLabel.substring(newLabel.indexOf("."), newLabel.length());
		} else {
			String oldLabel = actions[idx];
			newActionName = this.element.getSemantics().getRuntimeBehaviour().getLabelsMap().get(oldLabel);
		}
		return newActionName;
	}

	public Method defineMethod(String action) {
		Class<?>[] paramTypesExternal = { Queue.class, Queue.class };
		Class<?>[] paramTypesInternal = { Queue.class };
		Class<?> c = null;
		Method method = null;

		try {
			c = Class.forName(element.getType().getObj().getClass().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (action.contains("i_"))
			try {
				method = c.getMethod(action, paramTypesInternal);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				method = c.getMethod(action, paramTypesExternal);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return method;
	}

	public String defineRemoteElement(String action, String localElement) {
		Object obj = element.getType().getObj();
		String remoteElement = new String();

		switch (action) {
		case "invR":
			remoteElement = ((Element) obj).getConstraints().getInvRPartner().get(localElement);
			break;
		case "terR":
			remoteElement = ((Element) obj).getConstraints().getTerRPartner().get(localElement);
			break;
		case "invP":
			remoteElement = ((Element) obj).getConstraints().getInvPPartner().get(localElement);
			break;
		case "terP":
			remoteElement = ((Element) obj).getConstraints().getTerPPartner().get(localElement);
			break;
		}
		return remoteElement;
	}

	public void executeAction(String actionFullName) {
		Method method;
		String localElement, remoteElement;
		Queue localQueue, remoteQueue;
		Object obj = element.getType().getObj();

		String action = (actionFullName.substring(0, actionFullName.indexOf('.'))).trim();

		method = defineMethod(action);
		localElement = actionFullName.substring(actionFullName.indexOf('.') + 1, actionFullName.length());
		localQueue = ((Element) obj).findLocalQueueByName(localElement);

		try {
			if (action.contains("i_")) {
				method.invoke(obj, localQueue);
			} else {
				remoteElement = defineRemoteElement(action, localElement);
				remoteQueue = ((Element) obj).matchQueue(localElement, action, remoteElement);
				method.invoke(obj, localQueue, remoteQueue);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
