package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

/**
 * Waits for a specified amount of time before launching the next launch.
 */
public class DelayStrategy extends AbstractLaunchStrategy {

	private int waitingTimeInSeconds;

	private volatile boolean terminated = false;

	/**
	 * Constructs a {@link DelayStrategy}.
	 * 
	 * @param delayInSeconds the delay in seconds
	 */
	public DelayStrategy(int delayInSeconds) {
		this.waitingTimeInSeconds = delayInSeconds;
	}

	@Override
	protected void waitForLaunch(ILaunch launch) {
		for (int second = 0; second < waitingTimeInSeconds; second++) {
			if (terminated) {

				IProcess[] processes = launch.getProcesses();
				for (int process = 0; process < processes.length; process++) {
					System.out.println("multilaunch process" + processes[process].getLabel() + " terminated");
				}

				return;
			}

			waitDelay(1);

			IProcess[] processes = launch.getProcesses();
			int timeLeft = waitingTimeInSeconds - second;
			timeLeft--;
			for (int process = 0; process < processes.length; process++) {
				System.out.println("Waiting for " + timeLeft + " seconds");
			}
		}
	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void launchTerminated(int theExitCode) {
		System.out.println("Launch with delay terminated " + theExitCode);
		terminated = true;
	}

}
