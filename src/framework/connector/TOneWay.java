package framework.connector;

import java.util.Arrays;

import container.Queue;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public class TOneWay extends Connector {
	private Queue invPToInvRQueue = new Queue();

	public TOneWay(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new ProvidedInterface(Utils.INTERFACE_ONE_WAY),
				new RequiredInterface(Utils.INTERFACE_ONE_WAY));
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.semantics = new Semantics("i_PreInvP -> invP.e1 -> i_PosInvP -> i_PreInvR -> invR.e2");
	}

	@Override
	public void i_PosInvP(Queue local) {
		try {
			invPToInvRQueue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}

	@Override
	public void i_PreInvR(Queue local) {
		try {
			local.getQueue().put(invPToInvRQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}