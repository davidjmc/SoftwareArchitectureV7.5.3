package container.csp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import container.FDRAdapter;
import framework.basic.Element;
import framework.component.Component;
import framework.configuration.Configuration;
import framework.connector.Connector;
import utils.Utils;

public class CSPSpecification {
	private String dataTypeExp;
	private String typedChannelsExp;
	private String untypedChannelsExp;
	private String processesExp;
	private String compositeExp;
	private String assertionsExp;

	public CSPSpecification() {
		this.dataTypeExp = new String("datatype PROCNAMES = ");
		this.typedChannelsExp = new String("channel ");
		this.untypedChannelsExp = new String("channel ");
		this.processesExp = new String("");
		this.compositeExp = new String("P1 = ");
		this.assertionsExp = new String(Utils.DEADLOCK_ASSERTION);
	}

	public void create(Configuration conf) {
		String[] processAlphabet;
		String processName;
		String processBehaviour;
		HashMap<String, String> processes = new HashMap<String, String>();
		Set<String> typedChannelSet = new TreeSet<String>();
		Set<String> untypedChannelSet = new TreeSet<String>();
		Set<String> dataTypeSet = new TreeSet<String>();
		Set<String> components = new TreeSet<String>();
		Set<String> connectors = new TreeSet<String>();

		for (Element element : conf.getBehaviour().vertexSet()) {
			processName = element.getIdentification().getName().toUpperCase();

			if (element instanceof Component)
				components.add(processName);
			if (element instanceof Connector)
				connectors.add(processName);

			// datatypes
			if (!dataTypeSet.contains(processName))
				dataTypeSet.add(processName.toLowerCase());

			// processes
			processBehaviour = element.getSemantics().getRuntimeBehaviour().getActions();
			processes.put(processName, processBehaviour);

			// channels
			processAlphabet = processBehaviour.split(Utils.PREFIX_ACTION);
			for (String event : processAlphabet) {
				event = event.trim();
				if (!event.contains("i_")) {
					event = event.substring(0, event.indexOf("."));
					if (!typedChannelSet.contains(event))
						typedChannelSet.add(event);
				} else if (!untypedChannelSet.contains(event))
					untypedChannelSet.add(event);
			}
		}

		// data set expression
		Iterator<String> itDataType = dataTypeSet.iterator();
		while (itDataType.hasNext()) {
			this.dataTypeExp = this.dataTypeExp + itDataType.next() + " | ";
		}
		this.dataTypeExp = this.dataTypeExp.substring(0, this.dataTypeExp.lastIndexOf("|"));

		// channel expressions
		// -- untyped channels
		Iterator<String> itUntypedChannels = untypedChannelSet.iterator();
		while (itUntypedChannels.hasNext()) {
			String channelName = itUntypedChannels.next();
			this.untypedChannelsExp = this.untypedChannelsExp + channelName + ",";
		}
		this.untypedChannelsExp = this.untypedChannelsExp.substring(0, this.untypedChannelsExp.lastIndexOf(","));

		// -- typed channels
		Iterator<String> itTypedChannels = typedChannelSet.iterator();
		while (itTypedChannels.hasNext()) {
			String channelName = itTypedChannels.next();
			this.typedChannelsExp = this.typedChannelsExp + channelName + ",";
		}
		this.typedChannelsExp = this.typedChannelsExp.substring(0, this.typedChannelsExp.lastIndexOf(","));

		// process expressions
		for (String process : processes.keySet()) {
			this.processesExp = this.processesExp + process + " = " + processes.get(process) + " -> " + process;
			this.processesExp = this.processesExp + "\n";
		}

		// composite Exp
		// --- components
		Iterator<String> itComponents = components.iterator();
		this.compositeExp = this.compositeExp + "(";
		while (itComponents.hasNext())
			this.compositeExp = this.compositeExp + itComponents.next() + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ") \n";

		String syncEventExp = createSyncEventExp(typedChannelSet);
		this.compositeExp = this.compositeExp + syncEventExp + "\n";
		this.compositeExp = this.compositeExp + "(";

		// --- connectors
		String relabellingEvents = createRelabelling(typedChannelSet);
		Iterator<String> itConnectors = connectors.iterator();
		while (itConnectors.hasNext())
			this.compositeExp = this.compositeExp + itConnectors.next() + relabellingEvents + "|||";
		this.compositeExp = this.compositeExp.substring(0, this.compositeExp.lastIndexOf("|||")) + ")\n";
	}

	public void save(Configuration conf) {
		String cspFileName = conf.getConfName() + ".csp";
		String cspFullFileName = Utils.CSP_DIR + "/" + cspFileName;

		PrintWriter writer;
		try {
			writer = new PrintWriter(cspFullFileName, "UTF-8");

			writer.println(this.dataTypeExp);
			writer.println("");
			writer.println(this.untypedChannelsExp);
			writer.println("");
			writer.println(this.typedChannelsExp + " : PROCNAMES");
			writer.println("");
			writer.println(this.processesExp);
			writer.println("");
			writer.println(this.compositeExp);
			writer.println("");
			writer.print(this.assertionsExp);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createSyncEventExp(Set<String> typedChannels) {
		String r = new String();
		Iterator<String> itTypedChannels = typedChannels.iterator();

		r = "[|{|";
		while (itTypedChannels.hasNext())
			r = r + itTypedChannels.next() + ",";

		r = r.substring(0, r.lastIndexOf(",")) + "|}|]";

		return r;

	}

	public String createRelabelling(Set<String> typedChannels) {
		String r = new String();
		String typedChannel;
		Iterator<String> itTypedChannels = typedChannels.iterator();

		r = "[[";
		while (itTypedChannels.hasNext()) {
			typedChannel = itTypedChannels.next();
			switch (typedChannel) {
			case "invP":
				r = r + typedChannel + "<-invR" + ",";
				break;
			case "invR":
				r = r + typedChannel + "<-invP" + ",";
				break;
			case "terP":
				r = r + typedChannel + "<-terR" + ",";
				break;
			case "terR":
				r = r + typedChannel + "<-terP" + ",";
				break;
			}
		}

		r = r.substring(0, r.lastIndexOf(",")) + "]]";
		return r;
	}

	public void check(Configuration conf) {
		FDRAdapter fdr = new FDRAdapter();

		// invoke FDR
		fdr.check(conf.getConfName());

		return;
	}

	public String getProcessesExp() {
		return processesExp;
	}

	public void setProcessesExp(String processesExp) {
		this.processesExp = processesExp;
	}

	public String getAssertionsExp() {
		return assertionsExp;
	}

	public void setAssertionsExp(String assertionsExp) {
		this.assertionsExp = assertionsExp;
	}

	public String getTypedChannelsExp() {
		return typedChannelsExp;
	}

	public void setTypedChannelsExp(String typedChannelsExp) {
		this.typedChannelsExp = typedChannelsExp;
	}

	public String getUntypedChannelsExp() {
		return untypedChannelsExp;
	}

	public void setUntypedChannelsExp(String untypedChannelsExp) {
		this.untypedChannelsExp = untypedChannelsExp;
	}

	public String getCompositeExp() {
		return compositeExp;
	}

	public void setCompositeExp(String compositeExp) {
		this.compositeExp = compositeExp;
	}
}
