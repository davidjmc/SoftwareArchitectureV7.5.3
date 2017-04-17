package applications.simple;

import java.util.Arrays;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CSender;
import middleware.basic.Request;

public class CEchoSender extends CSender {

	public CEchoSender(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local) {
		Request request = new Request("echo",
				Arrays.asList("[ECHOED MESSAGE " + this.getIdentification().getName() + "]"));

		try {
			local.getQueue().put((SAMessage) new SAMessage(request));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
