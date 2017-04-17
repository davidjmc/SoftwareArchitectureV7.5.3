package adaptation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import container.ExecutionEnvironment;
import utils.Utils;

public class AdaptationManager {
	private ExecutionEnvironment env;

	public AdaptationManager() {
	}

	public void execute(ExecutionEnvironment env) {
		ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

		// configure execution manager
		this.env = env;

		ScheduledFuture<?> scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(new ThreadMAPE(),
				Utils.DELAY_FIRST_CHECKING, Utils.DELAY_BETWEEN_CHECKING, TimeUnit.MILLISECONDS);

		if (scheduledFuture.isDone())
			scheduledExecutor.shutdown();
	}

	private class ThreadMAPE implements Runnable {

		public ThreadMAPE() {
		}

		public void run() {
			Monitor monitor = new Monitor();
			Analyser analyser = new Analyser();
			Planner planner = new Planner();
			Executor executor = new Executor();
			AdaptationPlan plan = new AdaptationPlan();
			AnalysisStatus analysisStatus = new AnalysisStatus();

			monitor.execute();
			analysisStatus = analyser.check();
			plan = planner.selectPlan(env, analysisStatus);
			executor.execute(env, plan);
		}
	}
}
