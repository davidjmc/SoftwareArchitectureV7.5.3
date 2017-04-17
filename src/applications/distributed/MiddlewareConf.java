package applications.distributed;

import java.util.Arrays;

import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import framework.connector.TRequestReply;
import middleware.distribution.invoker.CInvoker;
import middleware.distribution.marshaller.CMarshaller;
import middleware.distribution.requestor.CRequestor;
import middleware.infrastructure.CClientRequestHandler;
import middleware.infrastructure.CServerRequestHandler;

public class MiddlewareConf {

	public Configuration configure() {
		Configuration conf = new Configuration("MiddlewareConf",false); // server1 -> server2
		
		// components
		Component invoker = new CInvoker("invoker");    
		Component marshaller1 = new CMarshaller("marshaller1");
		Component srh = new CServerRequestHandler("srh");
		Component crh = new CClientRequestHandler("crh");
		Component requestor = new CRequestor("requestor");  
		Component marshaller2 = new CMarshaller("marshaller2");

		// connectors
		Connector t1 = new TRequestReply("t1");
		Connector t2 = new TRequestReply("t2");
		Connector t3 = new TRequestReply("t3");
		Connector t4 = new TRequestReply("t4");

		// behaviors
		requestor.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]"));
		t1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-marshaller]]"));
		t2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-marshaller]]"));
		t3.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-marshaller]]"));
		t4.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-client]]","[[e2<-marshaller]]"));
		marshaller1.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-marshaller1]]"));
		marshaller2.getSemantics().getRuntimeBehaviour().relabel(Arrays.asList("[[e1<-marshaller2]]"));

		// attachments
		conf.connect(requestor,t1,marshaller1);
		conf.connect(requestor,t2,crh);

		conf.connect(srh,t3,invoker);
		conf.connect(invoker, t4, marshaller2);
		
		return conf;
	}
}