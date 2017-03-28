package ch.parisi.e4.advancedlaunch.utils;

/**
 * The {@link MultilauncherConfigurationAttributes}.
 * 
 * This class holds the attribute names of every multilaunch configuration file.
 */
public class MultilauncherConfigurationAttributes {

	/**
	 * Multilaunch configuration attribute containing all childlaunch names.
	 */
	public static final String CHILDLAUNCH_NAMES_ATTRIBUTE = "names";

	/**
	 * Multilaunch configuration attribute containing all childlaunch launch modes.
	 */
	public static final String CHILDLAUNCH_MODES_ATTRIBUTE = "modes";

	/**
	 * Multilaunch configuration attribute containing all childlaunch post launch actions.
	 */
	public static final String CHILDLAUNCH_POST_LAUNCH_ACTIONS_ATTRIBUTE = "postLaunchActions";

	/**
	 * Multilaunch configuration attribute containing all childlaunch runtime parameters.
	 */
	public static final String CHILDLAUNCH_PARAMS_ATTRIBUTE = "params";

	/**
	 * Multilaunch configuration attribute containing all childlaunch abort launch on error flags.
	 */
	public static final String CHILDLAUNCH_ABORT_LAUNCHES_ON_ERROR_ATTRIBUTE = "abortLaunchesOnError";

	/**
	 * Multilaunch configuration attribute holding the flag whether to open a confirmation dialog before launching a multilaunch.
	 */
	public static final String PROMPT_BEFORE_LAUNCH_ATTRIBUTE = "promptBeforeLaunch";
}
