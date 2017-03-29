package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for launch to be terminated. 
 */
public class WaitForTerminationStrategy implements WaitStrategy {

	private volatile boolean terminated = false;

	@Override
	public void waitForLaunch(ILaunch launch) {
		while (!terminated) {
			sleep(launch);
		}
	}

	private void sleep(ILaunch launch) {
		try {
			Thread.sleep(1000);
			System.out.println("Still waiting for process: " + launch);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void launchTerminated(int exitCode) {
		terminated = true;
	}
}
