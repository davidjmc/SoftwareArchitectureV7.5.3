package applications.distributed;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.marshaller.CMarshaller;

public class ClientMarshallerConf {

	public Configuration configure() {
		Configuration conf = new Configuration("ClientMarshallerConf",false); 
		
		// components
		Component client = new CClientMarshaller("client");
		Component server = new CMarshaller("marshaller");

		// connectors
		Connector t0 = new TRequestReply("t0");
		
		// behaviors
		client.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]"));
		t0.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-marshaller]]"));
		server.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-marshaller]]"));

		// attachments
		conf.connect(client,t0,server);
		
		return conf;
	}
}