package adaptation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;

import container.ElementExecutor;
import container.ExecutionEnvironment;
import container.ExecutionManager;
import framework.basic.Element;
import framework.configuration.BehaviourEdge;
import framework.configuration.Configuration;
import framework.configuration.StructureEdge;
import framework.basic.Type;
import utils.MyError;
import utils.Utils;
import utils.graph.Edge;
import utils.graph.Graph;
import utils.graph.Vertex;

public class Executor {
	private ExecutionEnvironment env;

	public Executor() {
	}

	public void execute(ExecutionEnvironment env, AdaptationPlan plan) {
		String p1, p2, p3, p4;

		this.env = env;
		for (String operation : plan.getActions())
			switch (operation) {
			case "replace":
				p1 = (String) plan.getElements().get(0);
				p2 = (String) plan.getElements().get(1);
				p3 = (String) plan.getElements().get(2);
				replaceComponent(env.getExecutionManager(), p1, p2, p3);
				break;
			case "updateBehaviour":
				p1 = (String) plan.getElements().get(0);
				updateBehaviour(env, p1); // TODO
				break;
			case "add":
				p1 = (String) plan.getElements().get(0); // from
				p2 = (String) plan.getElements().get(1); // connector
				p3 = (String) plan.getElements().get(2); // to
				p4 = (String) plan.getElements().get(3); // 'to'type
				add(env, p1, p2, p3, p4);
				break;
			case "remove":

				p1 = (String) plan.getElements().get(0); // component name
				remove(env, p1);
				break;
			}
	}

	public void remove(ExecutionEnvironment env, String elementName) {
		Element element;
		UndirectedGraph<Element, StructureEdge> structure = env.getConf().getStructure();
		DirectedGraph<Element, BehaviourEdge> behaviour = env.getConf().getBehaviour();

		Collection<StructureEdge> structureEdgesToBeRemoved = new ArrayList<StructureEdge>();
		Collection<StructureEdge> touchingEdges = new ArrayList<StructureEdge>();
		Collection<BehaviourEdge> incomingEdges = new ArrayList<BehaviourEdge>();
		Collection<BehaviourEdge> outgoingEdges = new ArrayList<BehaviourEdge>();
		Collection<BehaviourEdge> behaviourEdgesToBeRemoved = new ArrayList<BehaviourEdge>();

		// pause
		env.getExecutionManager().pause("all");

		
		// remove edges - Structure
		element = env.findStructureVertexByName(elementName);
		touchingEdges = structure.edgesOf(element);
		structureEdgesToBeRemoved.addAll(touchingEdges);
		if (!env.getConf().getStructure().removeAllEdges(structureEdgesToBeRemoved))
			new MyError("Edge not removed from Structure", Utils.FATAL_ERROR);

		// remove vertex - Structure
		if (!env.getConf().getStructure().removeVertex(element))
			new MyError("Element not removed from Structure", Utils.FATAL_ERROR);

		// remove edges - Behavior
		element = env.findBehaviourVertexByName(elementName);
		incomingEdges = behaviour.incomingEdgesOf(element);
		outgoingEdges = behaviour.outgoingEdgesOf(element);
		behaviourEdgesToBeRemoved.addAll(incomingEdges);
		behaviourEdgesToBeRemoved.addAll(outgoingEdges);
		if (!env.getConf().getBehaviour().removeAllEdges(behaviourEdgesToBeRemoved))
			new MyError("Edge not removed from Behaviour", Utils.FATAL_ERROR);

		// remove vertex - Behavior
		if (!env.getConf().getBehaviour().removeVertex(element))
			new MyError("Element not removed from Behaviour", Utils.FATAL_ERROR);

		this.env.getConf().configure(this.env);

		//this.env.getConf().printBehaviour();

		env.getExecutionManager().resume("all");
	}

	public void removeOld(ExecutionEnvironment env, String elementName) {
		Element element;
		UndirectedGraph<Element, StructureEdge> structure = env.getConf().getStructure();
		DirectedGraph<Element, BehaviourEdge> behaviour = env.getConf().getBehaviour();

		Collection<StructureEdge> structureEdgesToBeRemoved = new ArrayList<StructureEdge>();
		Collection<StructureEdge> touchingEdges = new ArrayList<StructureEdge>();
		Collection<BehaviourEdge> incomingEdges = new ArrayList<BehaviourEdge>();
		Collection<BehaviourEdge> outgoingEdges = new ArrayList<BehaviourEdge>();
		Collection<BehaviourEdge> behaviourEdgesToBeRemoved = new ArrayList<BehaviourEdge>();

		// pause
		env.getExecutionManager().pause("all");

		System.out.println(this.getClass()
				+ env.findBehaviourVertexByName("t0").getSemantics().getDynamicBehaviour().getActions());

		// remove edges - Structure
		element = env.findStructureVertexByName(elementName);
		touchingEdges = structure.edgesOf(element);
		structureEdgesToBeRemoved.addAll(touchingEdges);
		if (!env.getConf().getStructure().removeAllEdges(structureEdgesToBeRemoved))
			new MyError("Edge not removed from Structure", Utils.FATAL_ERROR);

		// remove vertex - Structure
		if (!env.getConf().getStructure().removeVertex(element))
			new MyError("Element not removed from Structure", Utils.FATAL_ERROR);

		// remove edges - Behavior
		element = env.findBehaviourVertexByName(elementName);
		incomingEdges = behaviour.incomingEdgesOf(element);
		outgoingEdges = behaviour.outgoingEdgesOf(element);
		behaviourEdgesToBeRemoved.addAll(incomingEdges);
		behaviourEdgesToBeRemoved.addAll(outgoingEdges);
		if (!env.getConf().getBehaviour().removeAllEdges(behaviourEdgesToBeRemoved))
			new MyError("Edge not removed from Behaviour", Utils.FATAL_ERROR);

		// remove vertex - Behavior
		if (!env.getConf().getBehaviour().removeVertex(element))
			new MyError("Element not removed from Behaviour", Utils.FATAL_ERROR);

		// update behavior
		env.updateRuntimeInfo();
		env.updateBehaviour();

		// remove actions relative to the removed element
		Iterator<Element> itElements = env.getConf().getBehaviour().vertexSet().iterator();
		while (itElements.hasNext()) {
			Element elementTemp = itElements.next();
			String oldBehaviour = elementTemp.getSemantics().getDynamicBehaviour().getActions();
			String newBehaviour = "";
			String[] actions;
			String action;

			actions = oldBehaviour.split(Utils.PREFIX_ACTION);
			for (String actionTemp : actions) {
				action = actionTemp.trim();
				if (action.substring(action.indexOf("[") + 1, action.indexOf("]")).contains("inv"))
					if (newBehaviour.isEmpty())
						newBehaviour = action;
					else
						newBehaviour = newBehaviour + Utils.PREFIX_ACTION + action;
			}
			env.findBehaviourVertexByName(elementTemp.getIdentification().getName()).setSemantics(newBehaviour);
			System.out.println(
					this.getClass() + " old " + elementTemp.getIdentification().getName() + " " + oldBehaviour);
			System.out.println(
					this.getClass() + " new " + elementTemp.getIdentification().getName() + " " + newBehaviour);
		}

		// stop removed component
		env.getExecutionManager().stop(elementName);

		// resume execution
		env.getExecutionManager().resume("all");
	}

	public void add(ExecutionEnvironment env, String fromName, String tName, String toName, String toType) {
		Vertex<Element> toVertex;
		Vertex<Element> fromVertex;
		Edge<Element> newEdge = null;
		Element fromElement = null, tElement = null, toElement = null;
		Class<?> toElementClass = null;
		String toElementClassName = Utils.CLASS_PACKAGE + "." + toType;
		DirectedGraph graph = env.getConf().getBehaviour();

		// pause
		env.getExecutionManager().pause("all");

		try {
			toElementClass = Class.forName(toElementClassName);
			toElement = (Element) toElementClass.newInstance();
			toElement.getIdentification().setName(toName);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		tElement = env.findBehaviourVertexByName(tName);
		tElement.addRequiredInterface(Utils.INTERFACE_TWO_WAY);
		env.getConf().connect(tElement, toElement);
	}

	public void replaceComponent(ExecutionManager executionManager, String oldElementName, String newElementName,
			String newElementType) {
		boolean foundElement = false;
		Element oldElement = null, newElement = null;
		ElementExecutor elementExecutor = null;
		Class<?> newElementClass = null;

		// pause component
		executionManager.pause(oldElementName);

		for (ElementExecutor executor : executionManager.getElementExecutors()) {
			elementExecutor = executor;
			if (elementExecutor.getElement().getIdentification().getName().contains(oldElementName)) {
				oldElement = executor.getElement();
				foundElement = true;
				break;
			}
		}

		if (foundElement) {
			try {
				String oldElementClassName = oldElement.getClass().getName();
				String newElementClassName = Utils.CLASS_PACKAGE + "." + newElementType;

				newElementClass = Class.forName(newElementClassName);
				newElement = (Element) newElementClass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			newElement.setType(new Type(newElement));
			newElement.setEvolution(oldElement.getEvolution());
			newElement.setInterfaces(oldElement.getInterfaces());
			newElement.setIdentification(oldElement.getIdentification());
			newElement.setSemantics(oldElement.getSemantics()); // TODO
			newElement.setRuntimeInfo(oldElement.getRuntimeInfo());
			newElement.setConstraints(oldElement.getConstraints());
			newElement.getIdentification().setName(newElementName);
			elementExecutor.setElement(newElement);
		} else
			new MyError("Element " + oldElementName + " not Found!!", Utils.FATAL_ERROR).print();

		// resume new component
		executionManager.resume(newElement.getIdentification().getName());
	}

	public void updateBehaviour(ExecutionEnvironment env, String elementName) {
		Element element = env.findBehaviourVertexByName(elementName);

		element.getSemantics().relabelling(Arrays.asList("[/]"));

		// element.getSemantics().relabelling(Arrays.asList("[invR1311053135/TT]"));
		// element.setSemantics("not;ok");
		return;
	}
}
