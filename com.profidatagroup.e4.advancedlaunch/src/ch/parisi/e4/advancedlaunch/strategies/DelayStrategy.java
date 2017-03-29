package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for a specified amount of time before launching the next launch.
 */
public class DelayStrategy implements WaitStrategy {

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
	public void waitForLaunch(ILaunch launch) {
		for (int second = 0; second < waitingTimeInSeconds; second++) {
			if (terminated) {
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
	public void launchTerminated(int exitCode) {
		System.out.println("Launch with delay terminated " + exitCode);
		terminated = true;
	}

}
