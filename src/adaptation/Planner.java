package adaptation;

import container.ExecutionEnvironment;
import utils.Utils;

public class Planner {

	public Planner() {
	}

	public AdaptationPlan selectPlan(ExecutionEnvironment env, AnalysisStatus status) {
		AdaptationPlan plan = null;

		switch (status.getCode()) {
		case 000: // no adaptation needed
			break;
		case Utils.REPLACE_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("replace");
			plan.getElements().add("server");
			plan.getElements().add("server1");
			plan.getElements().add("CEchoServer1");
			break;
		case Utils.ADD_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("add");
			plan.getElements().add("client");
			plan.getElements().add("t0");
			plan.getElements().add("server2");
			plan.getElements().add("CEchoServer2");
			break;
		case Utils.REPLACE_BEHAVIOUR: // TODO
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("updateBehaviour");
			plan.getElements().add("server1");
			break;
		case Utils.REMOVE_COMPONENT:
			// configure adaptation plan
			plan = new AdaptationPlan();
			plan.getActions().add("remove");
			plan.getElements().add("sender1");
			break;
		}
		return plan;
	}
}
