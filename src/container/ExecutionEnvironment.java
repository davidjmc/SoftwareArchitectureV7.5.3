package container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import adaptation.AdaptationManager;
import container.csp.CSPSpecification;
import framework.basic.Element;
import framework.configuration.BehaviourEdge;
import framework.configuration.Configuration;
import utils.MyError;
import utils.Utils;

public class ExecutionEnvironment {
	private Configuration conf;
	private AdaptationManager adapterManager;
	private ExecutionManager executionManager;
	private CommunicationManager communicationManager;
	private int portExecutionEnvironment;

	public ExecutionEnvironment(Object conf) {
		this.conf = (Configuration) conf;
		this.adapterManager = new AdaptationManager();
		this.executionManager = new ExecutionManager();
		this.communicationManager = new CommunicationManager();
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);
	}

	public ExecutionEnvironment() {
		this.adapterManager = new AdaptationManager();
		this.executionManager = new ExecutionManager();
		this.communicationManager = new CommunicationManager();
	}

	public void deploy(Object conf) {
		this.conf = (Configuration) conf;
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);

		// generate and configure graph
		this.getConf().configure(this);

		// behavioral check
		CSPSpecification csp = new CSPSpecification();
		csp.create(this.getConf());
		csp.save(this.getConf());
		csp.check(this.getConf());

		// structural check
		this.conf.check();

		// initialize managers
		this.adapterManager = new AdaptationManager();
		this.executionManager = new ExecutionManager();
		this.communicationManager = new CommunicationManager();
		this.portExecutionEnvironment = communicationManager.executionEnvironmentPort(this.conf);

		// execute managers
		executionManager.execute(this);
		if (this.conf.isAdaptive()) {
			adapterManager.execute(this);
		}
	}

	public void updateLabels() {
		Element eFrom, eTo;
		String partnerFrom, partnerTo;
		String portFrom, portTo;
		String[] actionsFrom, actionsTo;
		Iterator<BehaviourEdge> itEdges = this.getConf().getBehaviour().edgeSet().iterator();
		BehaviourEdge edge;
		HashMap<String, HashMap<String, String>> newLabels = new HashMap<String, HashMap<String, String>>();

		itEdges = this.getConf().getBehaviour().edgeSet().iterator();
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
		Object[] elements = this.getConf().getBehaviour().vertexSet().toArray();
		for (Object e : elements) {
			Element eTemp = (Element) e;
			eTemp.getSemantics().getRuntimeBehaviour().setLabelsMap(newLabels.get(eTemp.getIdentification().getName()));
		}
	}

	public void updateBehaviour() {
		Element eFrom, eTo;
		String partnerFrom, partnerTo;
		String portFrom, portTo;
		String[] actionsFrom, actionsTo;
		HashMap<String, String> relabellingFrom = new HashMap<String, String>();
		HashMap<String, String> relabellingTo = new HashMap<String, String>();
		Iterator<Element> itVertices = this.getConf().getBehaviour().vertexSet().iterator();
		Iterator<BehaviourEdge> itEdges = this.getConf().getBehaviour().edgeSet().iterator();
		Element element;
		BehaviourEdge edge;

		itEdges = this.getConf().getBehaviour().edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			eFrom = (Element) edge.getS();
			eTo = (Element) edge.getT();
			portFrom = edge.getPortFrom().trim();
			portTo = edge.getPortTo().trim();

			eFrom.getSemantics().getRuntimeBehaviour()
					.setActions(eFrom.getSemantics().getRuntimeBehaviour().getActions()); // NEW
			eTo.getSemantics().getRuntimeBehaviour().setActions(eTo.getSemantics().getRuntimeBehaviour().getActions()); // NEW

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
								relabellingFrom.put(actionFrom,
										actionFrom.substring(0, actionFrom.indexOf('.')) + "." + portFrom);
								relabellingTo.put(actionTo,
										actionTo.substring(0, actionTo.indexOf('.')) + "." + portTo);
							}
						}
					}
				}
			}
		}

		// update behaviours
		Set<String> keySetFrom = relabellingFrom.keySet();
		Set<String> keySetTo = relabellingTo.keySet();
		String newBehaviour;

		itEdges = this.getConf().getBehaviour().edgeSet().iterator();
		while (itEdges.hasNext()) {
			edge = itEdges.next();
			newBehaviour = ((Element) edge.getS()).getSemantics().getRuntimeBehaviour().getActions();

			for (String key : keySetFrom)
				newBehaviour = newBehaviour.replace(key, relabellingFrom.get(key));
			((Element) edge.getS()).getSemantics().getRuntimeBehaviour().setActions(newBehaviour);

			newBehaviour = ((Element) edge.getT()).getSemantics().getRuntimeBehaviour().getActions();
			for (String key : keySetTo)
				newBehaviour = newBehaviour.replace(key, relabellingTo.get(key));
			((Element) edge.getT()).getSemantics().getRuntimeBehaviour().setActions(newBehaviour);
		}

		// update internal actions
		String[] actions;
		itVertices = this.getConf().getBehaviour().vertexSet().iterator();
		while (itVertices.hasNext()) {
			element = itVertices.next();
			actions = element.getSemantics().getRuntimeBehaviour().getActions().split(Utils.PREFIX_ACTION);

			for (int idx = 0; idx < actions.length; idx++) {
				if (actions[idx].contains("i_PreI") || actions[idx].contains("i_PreT")) {
					String newPort = actions[idx + 1].substring(actions[idx + 1].indexOf('.') + 1,
							actions[idx + 1].length());
					actions[idx] = actions[idx] + '.' + newPort;
				}

				if (actions[idx].contains("i_PosI") || actions[idx].contains("i_PosT")) {
					String newPort = actions[idx - 1].substring(actions[idx - 1].indexOf('.') + 1,
							actions[idx - 1].length());
					actions[idx] = actions[idx] + '.' + newPort;
				}
			}
			newBehaviour = "";
			for (int idx = 0; idx < actions.length - 1; idx++) {
				newBehaviour = newBehaviour + actions[idx] + Utils.PREFIX_ACTION;
			}
			newBehaviour = newBehaviour + actions[actions.length - 1];
			element.getSemantics().getRuntimeBehaviour().setActions(newBehaviour);
		}
	}

	public Element findStructureVertexByName(String elementName) {
		Iterator<Element> itVertices = this.getConf().getStructure().vertexSet().iterator();
		Element element = null;
		boolean foundElement = false;

		while (itVertices.hasNext() && !foundElement) {
			element = itVertices.next();
			if (element.getIdentification().getName().contains(elementName)) {
				foundElement = true;
			}
		}

		if (!foundElement)
			new MyError("Element " + elementName + "not Found in Structure", Utils.FATAL_ERROR);

		return element;
	}

	public Element findBehaviourVertexByName(String elementName) {
		Iterator<Element> itVertices = this.getConf().getBehaviour().vertexSet().iterator();
		Element element = null;
		boolean foundElement = false;

		while (itVertices.hasNext() && !foundElement) {
			element = itVertices.next();
			if (element.getIdentification().getName().contains(elementName)) {
				foundElement = true;
			}
		}

		if (!foundElement)
			new MyError("Element " + elementName + "not Found in Behaviour", Utils.FATAL_ERROR);

		return element;
	}

	public CommunicationManager getCommunicationManager() {
		return this.communicationManager;
	}

	public Configuration getConf() {
		return this.conf;
	}

	public int getPortExecutionEnvironment() {
		return this.portExecutionEnvironment;
	}

	public ExecutionManager getExecutionManager() {
		return this.executionManager;
	}
}
