package middleware.distribution.clientproxy;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import container.Queue;
import framework.basic.Behaviour;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.SAMessage;
import framework.basic.Semantics;
import framework.basic.Type;
import framework.component.Component;
import utils.Utils;

public class CClientProxy extends Component {
	private static Queue fromInvPToInvRQueue = new Queue();
	private static Queue fromTerRToTerPQueue = new Queue();
	
	public CClientProxy(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.semantics = new Semantics(new Behaviour(new String("invP[1] -> invR [2] -> terR[2] -> terP[1]")));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
	}

	public void invP(String me) {
		String partner = this.getConstraints().getInvPPartner().get(me);
		Queue in = new Queue();
		BlockingQueue<Object> invP = this.runtimeInfo.getInvP().get(me).getQueue();

		in = matchQueue(me, "invP", partner);

		try {
			invP.put((SAMessage) in.getQueue().take());
			fromInvPToInvRQueue.getQueue().put(invP.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void terP(String me) {
		String partner = this.getConstraints().getTerPPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> terP = this.runtimeInfo.getTerP().get(me).getQueue();

		try {
			terP.put(fromTerRToTerPQueue.getQueue().take());
			out = matchQueue(me, "terP", partner);
			out.getQueue().put(terP.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void invR(String me) {
		String partner = this.getConstraints().getInvRPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> invR = this.runtimeInfo.getInvR().get(me).getQueue();

		out = matchQueue(me, "invR", partner);

		try {
			invR.put(fromInvPToInvRQueue.getQueue().take());
			out.getQueue().put(invR.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void terR(String me) {
		String partner = this.getConstraints().getTerRPartner().get(me);
		Queue in = new Queue();
		BlockingQueue<Object> terR = this.runtimeInfo.getTerR().get(me).getQueue();

		in = matchQueue(me, "terR", partner);

		try {
			terR.put((SAMessage) in.getQueue().take());
			fromTerRToTerPQueue.getQueue().put(terR.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	@Override
	public void i_PreInvP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreInvR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerR(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerR(String partner) {
		// TODO Auto-generated method stub
		
	}
}