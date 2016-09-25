package ch.parisi.e4.advancedlaunch;

import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

/**
 * Class which contains user-defined information 
 * of one launch, within a custom-configuration.
 */
public class LaunchConfigurationModel {
	private String name;
	private String mode;
	private String param;
	
	/**
	 * {@link PostLaunchAction}.
	 */
	private PostLaunchAction postLaunchAction;
	
	/**
	 * Constructs a {@code LaunchConfigurationModel} with the required arguments.
	 * 
	 * @param name the launch-name of the selected launch
	 * @param mode the chosen launch-mode
	 * @param postLaunchAction the chosen postLaunchAction
	 * @param param the chosen runtime-parameter
	 */
	public LaunchConfigurationModel(String name, String mode, PostLaunchAction postLaunchAction, String param) {
		this.name = name;
		this.mode = mode;
		this.postLaunchAction = postLaunchAction;
		this.param = param;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public PostLaunchAction getPostLaunchAction() {
		return postLaunchAction;
	}
	public void setPostLaunchAction(PostLaunchAction postLaunchAction) {
		this.postLaunchAction = postLaunchAction;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}
