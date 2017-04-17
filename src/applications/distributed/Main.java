package applications.distributed;

import applications.distributed.ClientMarshallerConf;
import container.ExecutionEnvironment;
import framework.configuration.Configuration;

public class Main {

	public static void main(String[] args) {
		
		Configuration conf = new ClientMarshallerConf().configure();
		//Configuration conf = new MiddlewareConf().configure();
		ExecutionEnvironment env = new ExecutionEnvironment();		
		
		env.deploy(conf);
	}
}
