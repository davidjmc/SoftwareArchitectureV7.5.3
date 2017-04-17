package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;

public class ClientServerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("ClientServerConf",true); // server1 -> server2

		// components
		Component echoClient = new CEchoClient("client");
		Component echoServer = new CEchoServer("server");

		// connectors
		Connector t0 = new TRequestReply("t0");

		// behaviors
		echoClient.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]"));
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-server]]"));
		echoServer.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-server]]"));

		// attachments
		conf.connect(echoClient, t0, echoServer);
		
		return conf;
	}
}