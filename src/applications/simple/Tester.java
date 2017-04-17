package applications.simple;

import container.Queue;
import framework.component.Component;
import framework.connector.Connector;
import framework.connector.TOneWay;
import framework.connector.TRequestReply;

public class Tester {

	public static void main(String[] args) throws InterruptedException {

		//testSenderReceiverConf();
		testClientServerConf();
	}

	public static void testSenderReceiverConf() {
		Component sender = new CEchoSender("sender");
		Connector t0 = new TOneWay("t0");
		Component receiver = new CEchoReceiver("receiver");

		Queue[] localClient = new Queue[10];
		Queue[] remote = new Queue[10];
		Queue[] localT0 = new Queue[10];
		Queue[] localServer = new Queue[10];

		for (int idx = 0; idx < localClient.length; idx++)
			localClient[idx] = new Queue();

		for (int idx = 0; idx < remote.length; idx++)
			remote[idx] = new Queue();

		for (int idx = 0; idx < localT0.length; idx++)
			localT0[idx] = new Queue();

		for (int idx = 0; idx < localServer.length; idx++)
			localServer[idx] = new Queue();

		for (int idx = 0; idx < 10; idx++) {
			// behavior
			sender.i_PreInvR(localClient[0]);
			sender.invR(localClient[0], remote[0]);

			t0.invP(localT0[0], remote[0]);
			t0.i_PosInvP(localT0[0]);
			t0.i_PreInvR(localT0[1]);
			t0.invR(localT0[1], remote[1]);

			receiver.invP(localServer[0], remote[1]);
			receiver.i_PosInvP(localServer[0]);
		}
	}

	public static void testClientServerConf() {
		Component client = new CEchoClient("client");
		Connector t0 = new TRequestReply("t0");
		Component server = new CEchoServer("server");

		Queue[] localClient = new Queue[10];
		Queue[] remote = new Queue[10];
		Queue[] localT0 = new Queue[10];
		Queue[] localServer = new Queue[10];

		for (int idx = 0; idx < localClient.length; idx++)
			localClient[idx] = new Queue();

		for (int idx = 0; idx < remote.length; idx++)
			remote[idx] = new Queue();

		for (int idx = 0; idx < localT0.length; idx++)
			localT0[idx] = new Queue();

		for (int idx = 0; idx < localServer.length; idx++)
			localServer[idx] = new Queue();

		for (int idx = 0; idx < 10; idx++) {
			// behavior
			client.i_PreInvR(localClient[0]);
			client.invR(localClient[0], remote[0]);

			t0.invP(localT0[0], remote[0]);
			t0.i_PosInvP(localT0[0]);
			t0.i_PreInvR(localT0[1]);
			t0.invR(localT0[1], remote[1]);

			server.invP(localServer[0], remote[1]);
			server.i_PosInvP(localServer[0]);
			server.i_PreTerP(localServer[1]);
			server.terP(localServer[1], remote[2]);
			server.i_PosTerP(localServer[1]);

			t0.terR(localT0[2], remote[2]);
			t0.i_PosTerR(localT0[2]);
			t0.i_PreTerP(localT0[3]);
			t0.terP(localT0[3], remote[3]);

			client.terR(localClient[1], remote[3]);
			client.i_PosTerR(localClient[1]);
		}
	}
}
