package applications.simple;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CServer;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CEchoServer2 extends CServer {
	private static Queue invPToTerPQueue = new Queue();
	
	public CEchoServer2(String name) {
		super(name);
	}

	@Override
	public void i_PosInvP(Queue local) {
		SAMessage inMessage = new SAMessage();
		SAMessage outMessage;
		Request inRequest = new Request();
		Reply outReply;

		try {
			inMessage = (SAMessage) local.getQueue().take();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		inRequest = (Request) inMessage.getContent();

		switch (inRequest.getOp()) {
		case "echo":
			outReply = new Reply(inRequest.getArgs().get(0)+" >>> "+this.getIdentification().getName());
			outMessage = new SAMessage(outReply);
			try {
				invPToTerPQueue.getQueue().put(outMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local) {

		try {
			local.getQueue().put(invPToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName() + " ");
	}
}
