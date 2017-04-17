package middleware.distribution.requestor;

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
import middleware.basic.Reply;
import middleware.basic.Request;
import utils.Utils;

public class CRequestor extends Component {
	private int numberOfProxies = Utils.MAX_NUMBER_OF_PROXIES;
	private static Queue fromInvPToInvRQueue = new Queue();
	private static Queue fromTerRToInvRQueue = new Queue();
	private static int invRState = 0;
	private static int terRState = 0;

	public CRequestor(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.semantics = new Semantics(
				new Behaviour(new String("iPreInvP -> invP[1] -> iPosInvP" + "iPreInvR -> invR[2] -> iPosInvR"
						+ "iPreTerR -> terR[2] -> iPosTerR" + "iPreInvR -> invR[3] -> iPosInvR"
						+ "iPreTerR -> terR[3] -> iPosTerR" + "iPreTerP -> terP[1] -> iPosTerP")));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();

		this.interfaces = new ArrayList<Object>();
		for (int idx = 0; idx < this.numberOfProxies; idx++)
			this.interfaces.add(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));

		this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY)); // marshaller
		this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY)); // crh
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

	@Override
	public void invR(String me) {
		String partner = this.getConstraints().getInvRPartner().get(me);
		Queue out = new Queue();
		BlockingQueue<Object> invR = this.runtimeInfo.getInvR().get(me).getQueue();

		out = matchQueue(me, "invR", partner);

		if (invRState == 0) { // to marshaller
			invRState = 1;
			try {
				SAMessage inMessage = new SAMessage();
				Request requestMarshaller = new Request();
				ArrayList<Object> args = new ArrayList<Object>();

				inMessage = (SAMessage) fromInvPToInvRQueue.getQueue().take();
				args.add(inMessage.getContent());
				requestMarshaller.setOp("marshall");
				requestMarshaller.setArgs(args);
				invR.put(new SAMessage(requestMarshaller));
				out.getQueue().put(invR.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (invRState == 1) // to crh
		{
			invRState = 0;
			try {
				SAMessage inMessage = new SAMessage();
				ArrayList<Object> args = new ArrayList<Object>();
				SAMessage messageToCRH;

				inMessage = (SAMessage) fromTerRToInvRQueue.getQueue().take();
				args = new ArrayList<Object>();
				args.add("localhost"); //args.add(hostProxy); // TODO
				args.add(1501); //args.add(portProxy); // TODO
				args.add(((Reply) inMessage.getContent()).getR());
				messageToCRH = new SAMessage(new Request("send", args));

				invR.put(messageToCRH);
				out.getQueue().put(invR.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " " + partner);
	}

	public void terR(String me) {
		String partner = this.getConstraints().getTerRPartner().get(me);
		Queue in = new Queue();
		BlockingQueue<Object> terR = this.runtimeInfo.getTerR().get(me).getQueue();

		in = matchQueue(me, "terR", partner);

		if (terRState == 0) { // to crh
			terRState = 1;
			try {
				terR.put((SAMessage) in.getQueue().take());
				fromTerRToInvRQueue.getQueue().put(terR.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			else if (terRState == 1){
				// TODO
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
	public void i_PosInvR(String me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PreTerR(String me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PreInvP(String me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PreInvR(String me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i_PosTerR(String me) {
		// TODO Auto-generated method stub

	}
}
