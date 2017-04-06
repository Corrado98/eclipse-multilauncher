package ch.parisi.e4.advancedlaunch.utils;

/**
 * The {@link DatabindingProperties}.
 */
public class DatabindingProperties {
	/**
	 * The databinding property name for a launch mode.
	 */
	public static final String MODE_PROPERTY = "mode";

	/**
	 * The databinding property name for a launch post launch action. 
	 */
	public static final String POST_LAUNCH_ACTION_PROPERTY = "postLaunchAction";

	/**
	 * The databinding property name for a launch runtime parameter. 
	 */
	public static final String PARAM_PROPERTY = "param";

	/**
	 * The databinding property name for the abort launch on error flag. 
	 */
	public static final String ABORT_LAUNCH_ON_ERROR_PROPERTY = "abortLaunchOnError";

	/**
	 * The databinding property name for the active flag. 
	 */
	public static final String ACTIVE_PROPERTY = "active";

	private DatabindingProperties() {
		// prevent instantiation
	}
}
