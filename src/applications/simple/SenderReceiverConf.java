package applications.simple;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TOneWay;

public class SenderReceiverConf {

	public Configuration configure() {
		Configuration conf = new Configuration("SenderReceiverConf",false);

		// components
		Component sender = new CEchoSender("sender");
		Component receiver = new CEchoReceiver("receiver");

		// connectors
		Connector t0 = new TOneWay("t0");

		// behaviors
		sender.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-sender]]"));
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-sender]]", "[[e2<-receiver]]"));
		receiver.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-receiver]]"));

		// attachments
		conf.connect(sender, t0, receiver);

		return conf;
	}
}