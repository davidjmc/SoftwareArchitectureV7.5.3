package container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import framework.basic.Element;
import utils.MyError;
import utils.Utils;

public class ExecutionManager {
	private List<ElementExecutor> elementExecutors = Collections.synchronizedList(new ArrayList<ElementExecutor>());

	public ExecutionManager() {
	}

	public List<ElementExecutor> getElementExecutors() {
		return this.elementExecutors;
	}

	public ExecutionManager(ArrayList<ElementExecutor> t) {
		this.elementExecutors = t;
	}

	public List<ElementExecutor> getThreads() {
		return this.elementExecutors;
	}

	public void execute(ExecutionEnvironment env) {

		createElementExecutors(env);
		for (ElementExecutor executor : elementExecutors) {
			// Executors.newFixedThreadPool(Utils.MAX_POOL_THREADS);
			// executor.execute(thread);
			executor.start();
		}
	}

	public Element findComponentByName(String elementName) {
		Element element = null;

		for (ElementExecutor elementExecutor : elementExecutors) {
			if (elementExecutor.getElement().getIdentification().getName().contains(elementName)) {
				element = elementExecutor.getElement();
				break;
			}
		}
		if (element == null)
			new MyError("Element " + elementName + " not found in " + elementExecutors, Utils.FATAL_ERROR).print();

		return element;
	}

	public boolean hasComponent(String elementName) {
		boolean foundComponent = false;

		for (ElementExecutor elementExecutor : elementExecutors) {
			if (elementExecutor.getElement().getIdentification().getName().contains(elementName)) {
				foundComponent = true;
				break;
			}
		}
		return foundComponent;
	}

	public void createElementExecutors(ExecutionEnvironment env) {
		ElementExecutor newElementExecutor;
		Iterator<Element> itVertices = env.getConf().getBehaviour().vertexSet().iterator();

		while (itVertices.hasNext()) {
			newElementExecutor = new ElementExecutor((Element) itVertices.next());
			elementExecutors.add(newElementExecutor);
		}
	}

	public void pause(String element) {
		for (ElementExecutor executor : elementExecutors) {
			if (executor.getElement().getIdentification().getName().contains(element) || element.contains("all"))
				executor.pause();
		}
	}

	public void resume(String element) {
		for (ElementExecutor executor : elementExecutors)
			if (executor.getElement().getIdentification().getName().contains(element) || element.contains("all"))
				executor.resume();
	}

	public void stop(String element) {
		ElementExecutor elementExecutorToBeRemoved = null;

		for (ElementExecutor executor : elementExecutors)
		{
			if (executor.getElement().getIdentification().getName().contains(element) || element.contains("all")) {
				elementExecutorToBeRemoved = executor;
				executor.stop();
			}
		}
		
		// remove the element executor
		elementExecutors.remove(elementExecutorToBeRemoved);
	}
}
