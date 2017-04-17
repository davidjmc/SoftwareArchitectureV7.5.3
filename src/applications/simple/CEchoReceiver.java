package applications.simple;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CReceiver;
import middleware.basic.Request;

public class CEchoReceiver extends CReceiver {

	public CEchoReceiver(String name) {
		super(name);
	}

	@Override
	public void i_PosInvP(Queue local) {
		SAMessage inMessage = new SAMessage();
		Request inRequest = new Request();

		try {
			inMessage = (SAMessage) local.getQueue().take();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		inRequest = (Request) inMessage.getContent();

		switch (inRequest.getOp()) {
		case "echo":
			System.out.println(this.getClass() + " : " + inRequest.getArgs().get(0));
			break;
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
