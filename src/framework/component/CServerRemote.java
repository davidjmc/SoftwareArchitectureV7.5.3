package framework.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
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
import utils.Utils;

public class CServerRemote extends Component {
	private String remoteHost = "";
	private int remotePort = 99999;
	private Queue fromInvPtoInvRQueue = new Queue();
	private Queue fromTerRtoTerPQueue = new Queue();

	public CServerRemote(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.semantics = new Semantics(new Behaviour(new String(
				"iPreInvP -> invP[1] -> iPosInvP"+ 
				"iPreInvR -> invR[2] -> iPosInvR"+ 
				"iPreTerR -> terR[2] -> iPosTerR"+ 
				"iPreTerP -> terP[1] -> iPosTerP" )));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
	}

	public void invP(String me) {
		byte[] msg;
		int port = this.getRuntimeInfo().getEnv().getPortExecutionEnvironment();
		ArrayList<Object> rcvInformation = new ArrayList<Object>();
		String partner = this.getConstraints().getInvPPartner().get(me);
		BlockingQueue<Object> invP = new ArrayBlockingQueue<Object>(Utils.MAX_NUMBER_OF_PORTS);

		// receive through execution environment
		rcvInformation = this.getRuntimeInfo().getEnv().getCommunicationManager().receive(port);
		this.remoteHost = (String) rcvInformation.get(0);
		this.remotePort = (int) rcvInformation.get(1);
		msg = (byte[]) rcvInformation.get(2);
		invP.add(new SAMessage((Object) msg));

		try {
			fromInvPtoInvRQueue.getQueue().put(invP.take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void invR(String me) {
		String partner = this.getConstraints().getInvRPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> invR = this.runtimeInfo.getInvR().get(me).getQueue();

		try {
			invR.put(fromInvPtoInvRQueue.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		out = matchQueue(me, "invR", partner);

		try {
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
			fromTerRtoTerPQueue.getQueue().put(terR.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void terP(String me) {
		byte[] msg = null;
		String partner = this.getConstraints().getTerPPartner().get(me);
		BlockingQueue<Object> terP = this.runtimeInfo.getTerP().get(me).getQueue();
		
		try {
			terP.put(fromTerRtoTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// send through execution environment
		try {
			msg = (byte[]) ((SAMessage) terP.take()).getContent();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.getRuntimeInfo().getEnv().getCommunicationManager().send(msg, this.remoteHost, this.remotePort);
		
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