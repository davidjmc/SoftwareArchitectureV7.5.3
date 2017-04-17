package applications.simple;

import java.util.Arrays;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CEchoClient extends CClient {

	public CEchoClient(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local) {
		Request request = new Request("echo",Arrays.asList("[ECHOED MESSAGE "+this.getIdentification().getName()+"]"));		

		try {
			local.getQueue().put((SAMessage) new SAMessage(request));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void i_PosTerR(Queue local) {
		Reply reply = new Reply();

		try {
			reply = (Reply) ((SAMessage) local.getQueue().take()).getContent();
			System.out.println(this.getClass() + " ["+this.getIdentification().getName()+"]  : " + reply.getR());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
