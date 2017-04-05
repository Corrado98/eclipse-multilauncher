package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

/**
 * A wait strategy describes how to wait after the launch of a {@link ILaunch}.
 */
public interface WaitStrategy {

	/**
	 * Waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * 
	 * @param launch the {@link ILaunch} to wait for
	 * @return whether waiting was successful
	 */
	boolean waitForLaunch(ILaunch launch);

	/**
	 * This method gets called when a launch terminates with its exit code. 
	 * 
	 * @param exitCode the exit code of the terminated launch
	 */
	void launchTerminated(int exitCode);

}
