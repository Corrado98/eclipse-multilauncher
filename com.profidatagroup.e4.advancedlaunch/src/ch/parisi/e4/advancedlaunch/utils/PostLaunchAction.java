package ch.parisi.e4.advancedlaunch.utils;

/**
 * The strategies each launch has to wait for before starting the next one.
 * 
 * @see PostLaunchActionUtils
 */
public enum PostLaunchAction {
	/**
	 * Do nothing.
	 */
	NONE,

	/**
	 * Wait until the launch has terminated.
	 */
	WAIT_FOR_TERMINATION,

	/**
	 * Wait for a specified time.
	 */
	DELAY,

	/**
	 * Wait until a specified regular expression appears in the console of the launch.
	 */
	WAIT_FOR_CONSOLESTRING
}