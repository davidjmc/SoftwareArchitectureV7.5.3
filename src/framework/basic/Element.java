package framework.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import container.Queue;
import framework.configuration.BehaviourEdge;
import utils.Utils;

public abstract class Element implements IElement {
	protected Identification id;
	protected List<Object> interfaces;
	protected Type type;
	protected Semantics semantics;
	protected Constraints constraints;
	protected NonFunctionalProperties nfr;
	protected Evolution evolution;
	protected RuntimeInfo runtimeInfo;

	public Element() {
		this.id = new Identification();
		this.interfaces = new ArrayList<Object>();
		this.type = new Type(this);
		this.semantics = new Semantics();
		this.constraints = new Constraints();
		this.nfr = new NonFunctionalProperties();
		this.evolution = new Evolution();
		this.runtimeInfo = new RuntimeInfo();
	}

	public void configure(int inDegree, int outDegree){
		
	}
	
	public Queue findLocalQueueByName(String portName) {
		Queue localQueue = null;

		for (Object tempInterface : this.getInterfaces()) {
			for (Port tempPort : ((Interface) tempInterface).getPorts()) {
				if (tempPort.getName().contains(portName)) {
					localQueue = tempPort.getQueue();
					return localQueue;
				}
			}
		}
		return localQueue;
	}

	public void addProvidedInterface(int type) {
		this.interfaces.add(new ProvidedInterface(type));
	}

	public void addRequiredInterface(int type) {
		this.interfaces.add(new ProvidedInterface(type));
	}

	public Queue matchQueue(String p1, String port, String p2) {
		Queue queueTemp = null;
		boolean foundQueue = false;
		Iterator<BehaviourEdge> itEdges = this.getRuntimeInfo().getEnv().getConf().getBehaviour().edgeSet().iterator();
		BehaviourEdge edge = new BehaviourEdge();

		while (!foundQueue && itEdges.hasNext()) {
			{
				edge = itEdges.next();
				if ((edge.getPortFrom().contains(p1) && edge.getPortTo().contains(p2))
						|| (edge.getPortFrom().contains(p2) && edge.getPortTo().contains(p1))) {
					queueTemp = new Queue();
					queueTemp = edge.getQueue();
					foundQueue = true;
					break;
				}
			}
		}
		if (!foundQueue) {
			System.out.println(this.getClass() + " ERROR: OUT OF SYNC " + p1 + "." + port + " " + p2);
			System.exit(0);
		}
		return queueTemp;
	}

	public Object findAvailabeInterfaceByType(Class<?> interfaceClass) {
		Interface tempInterface = null;
		Object returnObj = null;

		for (Object obj : this.getInterfaces()) {
			tempInterface = (Interface) obj;
			if (obj.getClass() == interfaceClass && tempInterface.isAvailable) {
				tempInterface.setAvailable(false);
				returnObj = obj;
				break;
			}
		}
		if (returnObj == null) {
			System.out.println(this.getClass() + " ERROR: " + interfaceClass.getName() + " Interface NOT AVAILABLE!!");
			System.exit(0);
		}
		return returnObj;
	}

	public Identification getIdentification() {
		return id;
	}

	public void setIdentification(Identification id) {
		this.id = id;
	}

	public List<Object> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Object> interfaces) {
		this.interfaces = interfaces;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Semantics getSemantics() {
		return semantics;
	}

	public void setSemantics(Semantics semantics) {
		this.semantics = semantics;
	}

	public Constraints getConstraints() {
		return constraints;
	}

	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}

	public NonFunctionalProperties getNfr() {
		return nfr;
	}

	public void setNfr(NonFunctionalProperties nfr) {
		this.nfr = nfr;
	}

	public Evolution getEvolution() {
		return evolution;
	}

	public void setEvolution(Evolution evolution) {
		this.evolution = evolution;
	}

	public RuntimeInfo getRuntimeInfo() {
		return runtimeInfo;
	}

	public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}

	public void here(String info) {
		String filter = new String(Utils.STRING_FILTER);
		String line;

		line = this.getIdentification().getName() + "." + info;
		if (line.contains(filter))
			System.out.println(line);
	}

	public void invR(Queue local, Queue remote) {

		try {
			// System.out.println(this.getClass()+": "+local.getQueue().size());
			remote.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void terR(Queue local, Queue remote) {

		try {
			local.getQueue().put(remote.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void invP(Queue local, Queue remote) {
		try {
			local.getQueue().put(remote.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	public void terP(Queue local, Queue remote) {
		try {
			remote.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
	
	@Override
	public void i_PosInvP(Queue local) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void i_PreInvR(Queue local) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void i_PosTerR(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerP(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerP(Queue local) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PosInvR(Queue local) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PreTerR(Queue local) {
		// TODO Auto-generated method stub
	}

	@Override
	public void i_PreInvP(Queue local) {
		// TODO Auto-generated method stub
	}

}