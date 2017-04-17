package container;

import framework.basic.CheckingStatus;
import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Session;
import utils.MyError;
import utils.Utils;

public class FDRAdapter {

	public void check(String confName) {
		CheckingStatus r = new CheckingStatus();
		Session session = new Session();

		session.loadFile(Utils.CSP_DIR + "/" + confName + ".csp");

		try {
			for (Assertion assertion : session.assertions()) {
				assertion.execute(null);
				r.setBehaviourStatus(assertion.passed());
				if (!r.getBehaviourStatus())
					new MyError("Deadlock",Utils.FATAL_ERROR);
			}
		} catch (InputFileError error) {
			System.out.println("Could not compile: " + error.toString());
		}
		
		return;
	}
}
