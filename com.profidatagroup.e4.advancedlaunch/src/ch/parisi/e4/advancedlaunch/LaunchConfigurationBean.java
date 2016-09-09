package ch.parisi.e4.advancedlaunch;

public class LaunchConfigurationBean {
	private String name;
	private String mode;
	private String postLaunchAction;
	private String param;
	
	public LaunchConfigurationBean() {
	}
	
	public LaunchConfigurationBean(String name, String mode, String postLaunchAction, String param) {
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
	public String getPostLaunchAction() {
		return postLaunchAction;
	}
	public void setPostLaunchAction(String postLaunchAction) {
		this.postLaunchAction = postLaunchAction;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	
}
