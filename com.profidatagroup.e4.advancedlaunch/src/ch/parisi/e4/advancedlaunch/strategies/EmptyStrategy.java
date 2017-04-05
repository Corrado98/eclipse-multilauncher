package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Does not wait for a launch.
 */
public class EmptyStrategy implements WaitStrategy {

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		return true;
	}

	@Override
	public void launchTerminated(int exitCode) {
		// ignore termination
	}

}
