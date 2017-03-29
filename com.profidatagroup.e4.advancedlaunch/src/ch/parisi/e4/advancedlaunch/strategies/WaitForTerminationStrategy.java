package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for launch to be terminated. 
 */
public class WaitForTerminationStrategy extends AbstractLaunchStrategy {

	private volatile boolean terminated = false;

	@Override
	protected void waitForLaunch(ILaunch launch) {
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
	protected void launchTerminated(int exitCode) {
		terminated = true;
	}
}
