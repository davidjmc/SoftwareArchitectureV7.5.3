package middleware.distribution.invoker;

import java.util.ArrayList;
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

public class CInvoker extends Component {
	private static int numberOfRemoteObjects = Utils.MAX_NUMBER_OF_REMOTE_OBJECTS;
	private static Queue fromInvPToInvRQueue = new Queue();

	public CInvoker(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.semantics = new Semantics(new Behaviour(new String(
				    "i_PreInvP ; invP[1] ; i_PosInvP"+
					"i_PreInvR ; invR[2] ; i_PosInvR"+
					"i_PreTerR ; terR[2] ; i_PosTerR"+ 
					"i_PreInvR ; invR[3] ; i_PosInvR"+
					"i_PreTerR ; terR[3] ; i_PosTerR"+
					"i_PreTerP ; terP[1] ; i_PosTerP"))); // srh ->  // TODO
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();

		this.interfaces = new ArrayList<Object>();
		this.interfaces.add(new ProvidedInterface(Utils.INTERFACE_TWO_WAY)); // srh
		this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY)); // marshaller

		for(int idx=0; idx < CInvoker.numberOfRemoteObjects; idx++)
			this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY));
	}

	@Override
	public void i_PreInvP(String partner) {
		// TODO Auto-generated method stub
		
	}

	@Override
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

	@Override
	public void i_PosInvP(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerP(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terP(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerP(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreInvR(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invR(String me) {
		
	}

	@Override
	public void i_PosInvR(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerR(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terR(String me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerR(String me) {
		// TODO Auto-generated method stub
		
	}
}
