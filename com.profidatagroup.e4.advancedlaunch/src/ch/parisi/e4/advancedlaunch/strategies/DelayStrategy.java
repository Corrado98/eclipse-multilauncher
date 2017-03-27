package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for a specified amount of time before launching the next launch.
 */
public class DelayStrategy extends AbstractLaunchStrategy {

	private int waitingTimeInSeconds;

	private volatile boolean aborted = false;

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
			if (aborted) {
				return;
			}

			waitDelay(1);
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
		aborted = true;
	}

}
