package framework.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import container.ExecutionEnvironment;
import framework.basic.Element;
import framework.basic.Port;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.component.Component;
import framework.connector.Connector;
import middleware.services.naming.CNamingServer;
import utils.Utils;

public class Configuration {
	private String name;
	private UndirectedGraph<Element, StructureEdge> s;
	private DirectedGraph<Element, BehaviourEdge> b;
	private boolean isAdaptive;

	public Configuration(String n, boolean isAdaptive) {
		this.name = n;
		this.s = new SimpleGraph<>(StructureEdge.class);
		this.b = new DefaultDirectedGraph<>(BehaviourEdge.class);
		this.isAdaptive = isAdaptive;
	}

	public void check() {
		structuralChecking(); // TODO
	}

	public void structuralChecking() {
		// TODO
		return;
	}

	public void connect(Component c1, Connector t, Component c2) {
		connectStructure(c1, t);
		connectStructure(t, c2);
	}

	public void connectStructure(Element from, Element to) {

		this.s.addVertex(from);
		this.s.addVertex(to);
		this.s.addEdge(from, to);
	}

	public void updateRuntimeInfo(ExecutionEnvironment env) {
		Iterator<Element> itVertices = this.getBehaviour().vertexSet().iterator();
		Element vertex;

		// update vertices runtime info
		while (itVertices.hasNext()) {
			vertex = itVertices.next();
			vertex.getRuntimeInfo().setEnv(env);
		}
	}

	public void configure(ExecutionEnvironment env) {
		Element from, to;
		DirectedGraph<Element, BehaviourEdge> tempBehaviourGraph = new DefaultDirectedGraph<>(BehaviourEdge.class);

		for (StructureEdge edge : this.getStructure().edgeSet()) {
			from = this.getStructure().getEdgeSource(edge);
			to = this.getStructure().getEdgeTarget(edge);

			// if the vertices are already in graph, do nothing
			if (tempBehaviourGraph.addVertex(from) || tempBehaviourGraph.addVertex(to)) {
				tempBehaviourGraph.addVertex(from);
				tempBehaviourGraph.addVertex(to);
				tempBehaviourGraph.addEdge(from, to);
			}
		}

		// configure elements according to current graph
		for (Element element : tempBehaviourGraph.vertexSet())
			element.configure(tempBehaviourGraph.inDegreeOf(element), tempBehaviourGraph.outDegreeOf(element));

		// empty behavior graph - useful in adaptations
		this.setBehaviour(new DefaultDirectedGraph<>(BehaviourEdge.class));

		// add vertices to behavior graph
		Iterator<Element> itVertices = tempBehaviourGraph.vertexSet().iterator();
		while (itVertices.hasNext())
			this.getBehaviour().addVertex(itVertices.next());

		// add edges to behavior graph
		for (StructureEdge edgeStructure : this.getStructure().edgeSet())
			for (BehaviourEdge edgeBehaviour : this.createEdges((Element) edgeStructure.getS(),
					(Element) edgeStructure.getT()))
				this.getBehaviour().addEdge((Element) edgeBehaviour.getS(), (Element) edgeBehaviour.getT());
		
		// apply relabeling to element's expression
		for (Element element : this.getBehaviour().vertexSet())
			element.getSemantics().getRuntimeBehaviour().relabelBehaviourActions();
				
		// apply relabeling to element's graph
		for (Element element : this.getBehaviour().vertexSet())
			element.getSemantics().relabelGraphActions(element);
		
		// configure runtime
		updateRuntimeInfo(env);

		// update labels
		updateLabels();
	}

	public void updateLabels() {
		Element eFrom, eTo;
		String partnerFrom, partnerTo;
		String portFrom, portTo;
		String[] actionsFrom, actionsTo;
		Iterator<BehaviourEdge> itEdges = this.getBehaviour().edgeSet().iterator();
		BehaviourEdge edge;
		HashMap<String, HashMap<String, String>> newLabels = new HashMap<String, HashMap<String, String>>();

		itEdges = this.getBehaviour().edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			eFrom = (Element) edge.getS();
			eTo = (Element) edge.getT();
			portFrom = edge.getPortFrom().trim();
			portTo = edge.getPortTo().trim();

			eFrom.getSemantics().getRuntimeBehaviour()
					.setActions(eFrom.getSemantics().getRuntimeBehaviour().getActions());
			eTo.getSemantics().getRuntimeBehaviour().setActions(eTo.getSemantics().getRuntimeBehaviour().getActions());

			actionsFrom = eFrom.getSemantics().getRuntimeBehaviour().getActions().split(Utils.PREFIX_ACTION);
			actionsTo = eTo.getSemantics().getRuntimeBehaviour().getActions().split(Utils.PREFIX_ACTION);

			// match
			for (String actionFrom : actionsFrom) {
				if (actionFrom.contains(portFrom.substring(0, 4))) {
					for (String actionTo : actionsTo) {
						if (actionTo.contains(portTo.substring(0, 4))) {
							partnerFrom = actionFrom.substring(actionFrom.indexOf('.') + 1, actionFrom.length());
							partnerTo = actionTo.substring(actionTo.indexOf('.') + 1, actionTo.length());
							if (partnerFrom.equals(partnerTo)) {
								// from
								if (!newLabels.containsKey(eFrom.getIdentification().getName())) {
									HashMap<String, String> tempMapFrom = new HashMap<String, String>();
									tempMapFrom.put(actionFrom,
											actionFrom.substring(0, actionFrom.indexOf(".") + 1) + portFrom);
									newLabels.put(eFrom.getIdentification().getName(), tempMapFrom);
								} else {
									HashMap<String, String> tempMapFrom = new HashMap<String, String>();
									tempMapFrom = newLabels.get(eFrom.getIdentification().getName());
									tempMapFrom.put(actionFrom,
											actionFrom.substring(0, actionFrom.indexOf(".") + 1) + portFrom);
									newLabels.put(eFrom.getIdentification().getName(), tempMapFrom);
								}
								// to
								if (!newLabels.containsKey(eTo.getIdentification().getName())) {
									HashMap<String, String> tempMapTo = new HashMap<String, String>();
									tempMapTo.put(actionTo, actionTo.substring(0, actionTo.indexOf(".") + 1) + portTo);
									newLabels.put(eTo.getIdentification().getName(), tempMapTo);
								} else {
									HashMap<String, String> tempMapTo = new HashMap<String, String>();
									tempMapTo = newLabels.get(eTo.getIdentification().getName());
									tempMapTo.put(actionTo, actionTo.substring(0, actionTo.indexOf(".") + 1) + portTo);
									newLabels.put(eTo.getIdentification().getName(), tempMapTo);
								}
							}
						}
					}
				}
			}
		}

		// update relabels
		Object[] elements = this.getBehaviour().vertexSet().toArray();
		for (Object e : elements) {
			Element eTemp = (Element) e;
			eTemp.getSemantics().getRuntimeBehaviour().setLabelsMap(newLabels.get(eTemp.getIdentification().getName()));
		}
	}

	public Collection<BehaviourEdge> createEdges(Element from, Element to) {
		DirectedGraph<Element, BehaviourEdge> graph = new DefaultDirectedGraph<>(BehaviourEdge.class);

		// use a temporary graph
		graph = this.getBehaviour();

		// interfaces to be connected
		RequiredInterface interfaceFrom = new RequiredInterface();
		ProvidedInterface interfaceTo = new ProvidedInterface();
		interfaceFrom = (RequiredInterface) from.findAvailabeInterfaceByType(RequiredInterface.class);
		interfaceTo = (ProvidedInterface) to.findAvailabeInterfaceByType(ProvidedInterface.class);

		for (int idx1 = 0; idx1 < interfaceFrom.getPorts().size(); idx1++) {
			Port portFrom = interfaceFrom.getPorts().get(idx1);
			for (int idx2 = 0; idx2 < interfaceTo.getPorts().size(); idx2++) {
				Port portTo = interfaceTo.getPorts().get(idx2);
				if (portFrom.isAvailable() && portTo.isAvailable()) {
					if (portFrom.getName().contains("invR") && portTo.getName().contains("invP")) {
						portFrom.setAvailable(false);
						portTo.setAvailable(false);
						from.getConstraints().getInvRPartner().put(portFrom.getName(), portTo.getName());
						to.getConstraints().getInvPPartner().put(portTo.getName(), portFrom.getName());
						BehaviourEdge edge = new BehaviourEdge();
						edge.setS(from);
						edge.setT(to);
						edge.setPortFrom(portFrom.getName());
						edge.setPortTo(portTo.getName());
						graph.addEdge(from, to, edge);
					}
					if (portFrom.getName().contains("terR") && portTo.getName().contains("terP")) {
						portFrom.setAvailable(false);
						portTo.setAvailable(false);
						from.getConstraints().getTerRPartner().put(portFrom.getName(), portTo.getName());
						to.getConstraints().getTerPPartner().put(portTo.getName(), portFrom.getName());
						BehaviourEdge edge = new BehaviourEdge();
						edge.setS(to);
						edge.setT(from);
						edge.setPortFrom(portTo.getName());
						edge.setPortTo(portFrom.getName());
						graph.addEdge(to, from, edge);
					}
				}
			}
		}

		return graph.edgeSet();
	}

	public void printStructure() {
		Iterator<Element> itElements;
		Iterator<StructureEdge> itEdges;
		Element element;
		StructureEdge edge;

		// print configuration information
		System.out.println("Configuration: " + this.name);

		// print components
		// elements = this.s.vertexSet();

		System.out.print("Components: ");
		itElements = this.s.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Component)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print connectors
		System.out.print("\nConnectors: ");
		itElements = this.s.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Connector)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print attachments
		System.out.println("\nAttachments: ");
		itEdges = this.s.edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			System.out.print("(" + ((Element) edge.getS()).getIdentification().getName() + ","
					+ ((Element) edge.getT()).getIdentification().getName() + ")" + "\n");
		}
	}

	public void printBehaviour() {
		Iterator<Element> itElements;
		Iterator<BehaviourEdge> itEdges;
		Element element;
		BehaviourEdge edge;

		// print configuration information
		System.out.println("Configuration: " + this.name);

		// print components

		System.out.print("Components: ");
		itElements = this.b.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Component)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print connectors
		System.out.print("\nConnectors: ");
		itElements = this.b.vertexSet().iterator();
		while (itElements.hasNext()) {
			element = itElements.next();
			if (element instanceof Connector)
				System.out.print(element.getIdentification().getName() + ",");
		}

		// print attachments
		System.out.println("\nAttachments: ");
		itEdges = this.b.edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			System.out.print("(" + ((Element) edge.getS()).getIdentification().getName() + "." + edge.getPortFrom()
					+ " -> " + ((Element) edge.getT()).getIdentification().getName() + "." + edge.getPortTo() + ")"
					+ "\n");
		}
	}

	public String getConfName() {
		return this.name;
	}

	public boolean hasNamingService() {
		boolean foundComponent = false;
		Iterator<Element> itVertices = this.s.vertexSet().iterator();
		Element componentTemp = null;

		while (!foundComponent && itVertices.hasNext()) {
			componentTemp = itVertices.next();
			if (componentTemp instanceof CNamingServer)
				foundComponent = true;
		}

		return foundComponent;
	}

	public DirectedGraph<Element, BehaviourEdge> getBehaviour() {
		return this.b;
	}

	public void setBehaviour(DirectedGraph<Element, BehaviourEdge> behaviour) {
		this.b = behaviour;
	}

	public UndirectedGraph<Element, StructureEdge> getStructure() {
		return this.s;
	}

	public void setStructure(UndirectedGraph<Element, StructureEdge> structure) {
		this.s = structure;
	}

	public boolean isAdaptive() {
		return isAdaptive;
	}

	public void setAdaptive(boolean isAdaptive) {
		this.isAdaptive = isAdaptive;
	}
}
