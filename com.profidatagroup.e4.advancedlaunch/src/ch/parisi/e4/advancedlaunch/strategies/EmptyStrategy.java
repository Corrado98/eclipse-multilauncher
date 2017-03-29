package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Does not wait for a launch.
 */
public class EmptyStrategy implements WaitStrategy {

	@Override
	public void waitForLaunch(ILaunch launch) {
		// nothing to do
	}

	@Override
	public void launchTerminated(int exitCode) {
		// ignore termination
	}

}
