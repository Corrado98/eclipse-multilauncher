package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * Does not wait for a launch.
 */
public class EmptyStrategy extends AbstractLaunchStrategy {

	@Override
	protected void waitForLaunch(ILaunch launch) {
		//nothing to do
	}

	@Override
	protected void launchTerminated(int exitCode) {
		// ignore termination
	}

}
