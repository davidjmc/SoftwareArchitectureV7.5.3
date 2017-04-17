package middleware.infrastructure;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClientRemote;
import middleware.basic.Request;
import utils.MyError;
import utils.Utils;

public class CClientRequestHandler extends CClientRemote {

	public CClientRequestHandler(String name) {
		super(name);
	}

	public void i_PosInvP(Queue local) {
		Request inRequest = new Request();
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;

		try {
			inMessage = (SAMessage) local.getQueue().take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inRequest = (Request) inMessage.getContent();

		switch (inRequest.getOp()) {
		case "send":
			outMessage = new SAMessage(inRequest.getArgs());
			try {
				invPToInvRQueue.getQueue().put(outMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			new MyError("Operation '" + inRequest.getOp() + "' is not implemented by CRH", Utils.FATAL_ERROR).print();
			break;

		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreTerP(Queue local) {

		try {
			local.getQueue().put(terRToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
