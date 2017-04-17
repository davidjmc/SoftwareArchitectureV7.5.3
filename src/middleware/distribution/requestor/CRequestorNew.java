package middleware.distribution.requestor;

import java.util.ArrayList;

import container.Queue;
import framework.basic.SAMessage;
import framework.component.CNToMSeq;
import middleware.basic.Reply;
import middleware.basic.Request;

public class CRequestorNew extends CNToMSeq {
	private Queue invPToInvRS1Queue = new Queue();
	private Queue terRS1ToInvRS2Queue = new Queue();
	private Queue terRS2ToTerPQueue = new Queue();

	public CRequestorNew(String name, int N, int M) {
		super(name, N, M);
	}

	@Override
	public void i_PosInvP(Queue local) {
		SAMessage inMessage;
		Request outRequest = new Request();
		ArrayList<Object> args = new ArrayList<Object>();

		try {
			inMessage = (SAMessage) local.getQueue().take();
			outRequest.setOp("marshall");
			args.add(inMessage);
			outRequest.setArgs(args);
			invPToInvRS1Queue.getQueue().put(new SAMessage(outRequest));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR1(Queue local) {
		try {
			local.getQueue().put(invPToInvRS1Queue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR1(Queue local) {
		try {
			terRS1ToInvRS2Queue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PreInvR2(Queue local) {
		ArrayList<Object> args = new ArrayList<Object>();
		Request request = new Request();
		Reply reply = new Reply();
		SAMessage in;

		try {
			in = (SAMessage) terRS1ToInvRS2Queue.getQueue().take();
			reply = (Reply) in.getContent();
			request.setOp("send");
			args.add(reply.getR());
			request.setArgs(args);
			local.getQueue().put((SAMessage) new SAMessage(request));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void i_PosTerR2(Queue local) {
		try {
			terRS2ToTerPQueue.getQueue().put(local.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	@Override
	public void i_PreTerP(Queue local) {
		try {
			local.getQueue().put(terRS2ToTerPQueue.getQueue().take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		here(Thread.currentThread().getStackTrace()[1].getMethodName());
	}
}
