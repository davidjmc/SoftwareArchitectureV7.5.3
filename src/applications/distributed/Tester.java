package applications.distributed;

import applications.simple.CEchoSender;
import container.Queue;
import framework.component.Component;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.marshaller.CMarshaller;
import middleware.distribution.requestor.CRequestorNew;
import middleware.infrastructure.CClientRequestHandler;

class ThreadMiddlewareClientConf implements Runnable {
	Tester tester = new Tester();
	
	public void run() {
		try {
			Tester.testMiddlewareConfClient();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

public class Tester {

	public static void main(String[] args) throws InterruptedException {

		new Thread(new ThreadMiddlewareClientConf()).start();
	}

	public static void testMiddlewareConfClient() throws InterruptedException {
		Component client = new CEchoSender("client");
		Connector t0 = new TRequestReply("t0");
		Connector t1 = new TRequestReply("t0");
		Connector t2 = new TRequestReply("t0");
		CRequestorNew requestor = new CRequestorNew("requestor", 1, 2);
		CMarshaller marshaller = new CMarshaller("marshaller");
		CClientRequestHandler crh = new CClientRequestHandler("crh");

		Queue[] localClient = new Queue[10];
		Queue[] remote = new Queue[10];
		Queue[] localT0 = new Queue[10];
		Queue[] localT1 = new Queue[10];
		Queue[] localT2 = new Queue[10];
		Queue[] localRequestor = new Queue[10];
		Queue[] localMarshaller = new Queue[10];
		Queue[] localCRH = new Queue[10];

		for (int idx = 0; idx < localClient.length; idx++) {
			localClient[idx] = new Queue();
			remote[idx] = new Queue();
			localT0[idx] = new Queue();
			localT1[idx] = new Queue();
			localT2[idx] = new Queue();
			localRequestor[idx] = new Queue();
			localMarshaller[idx] = new Queue();
			localCRH[idx] = new Queue();
		}

		// behavior
		for (int idx = 0; idx < 10; idx++) {
			
			// client -> crh
			client.i_PreInvR(localClient[0]);
			client.invR(localClient[0], remote[0]);

			t0.invP(localT0[0], remote[0]);
			t0.i_PosInvP(localT0[0]);
			t0.i_PreInvR(localT0[1]);
			t0.invR(localT0[1], remote[1]);

			requestor.invP(localRequestor[0], remote[1]);
			requestor.i_PosInvP(localRequestor[0]);
			requestor.i_PreInvR1(localRequestor[1]);
			requestor.invR(localRequestor[1], remote[2]);

			t1.invP(localT1[0], remote[2]);
			t1.i_PosInvP(localT1[0]);
			t1.i_PreInvR(localT1[1]);
			t1.invR(localT1[1], remote[3]);
			
			marshaller.invP(localMarshaller[0], remote[3]);
			marshaller.i_PosInvP(localMarshaller[0]);
			marshaller.i_PreTerP(localMarshaller[1]);
			marshaller.terP(localMarshaller[1],remote[4]);

			t1.terR(localT1[2], remote[4]);
			t1.i_PosTerR(localT1[2]);
			t1.i_PreTerP(localT1[3]);
			t1.terP(localT1[3], remote[5]);
			
			requestor.terR(localRequestor[2], remote[5]);
			requestor.i_PosTerR1(localRequestor[2]);
			requestor.i_PreInvR2(localRequestor[3]);
			requestor.invR(localRequestor[3], remote[6]);
			//requestor.i_PosInvR(localRequestor[3]);

			t2.invP(localT0[0], remote[6]);
			t2.i_PosInvP(localT0[0]);
			t2.i_PreInvR(localT0[1]);
			t2.invR(localT0[1], remote[7]);
			
			crh.invP(localCRH[0], remote[7]);
			crh.i_PosInvP(localCRH[0]);
			System.out.println(localCRH[0].getQueue().take());
			crh.invR(localCRH[0], remote[8]);

			// crh -> client
			//crh.i_PreTerP(localCRH[1]);
			//crh.terP(localCRH[1],remote[8]);			
		}
	}
}
