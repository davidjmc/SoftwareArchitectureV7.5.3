package adaptation;

import utils.Utils;

//import java.util.Random;

public class Analyser {

	public Analyser() {
	}

	public AnalysisStatus check() { // TODO
		AnalysisStatus status = new AnalysisStatus();
//		Random random = new Random();

//		if (random.nextBoolean()) {
			status.setAdaptationNecessary(true);
			status.setCode(Utils.REPLACE_COMPONENT); // replace component 
//		}
		return status;
	}
}
