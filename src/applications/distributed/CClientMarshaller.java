package applications.distributed;

import java.util.ArrayList;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CClient;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CClientMarshaller extends CClient {

	public CClientMarshaller(String name) {
		super(name);
	}

	public void i_PreInvR(Queue local) {
		ArrayList<Object> args = new ArrayList<Object>();
		Request request = new Request();

		try {
			request.setOp("marshall");
			args.add("test" );
			request.setArgs(args);
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

	@Override
	public void i_PreInvP(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvP(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerP(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosTerP(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PosInvR(Queue local) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void i_PreTerR(Queue local) {
		// TODO Auto-generated method stub
		
	}
}
