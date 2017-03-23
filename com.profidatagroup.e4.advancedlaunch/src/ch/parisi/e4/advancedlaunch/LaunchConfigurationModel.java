package ch.parisi.e4.advancedlaunch;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

/**
 * Class which contains user-defined information 
 * of one launch, within a custom-configuration.
 */
public class LaunchConfigurationModel {
	private String name;
	private String mode;
	private String param;
	private boolean abortLaunchOnException;

	/**
	 * {@link PostLaunchAction}.
	 */
	private PostLaunchAction postLaunchAction;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Constructs a {@code LaunchConfigurationModel} with the required arguments.
	 * 
	 * @param name the launch-name of the selected launch
	 * @param mode the chosen launch-mode
	 * @param postLaunchAction the chosen postLaunchAction
	 * @param param the chosen runtime-parameter
	 * @param abortLaunchOnException whether to abort a launch on exception
	 */
	public LaunchConfigurationModel(String name, String mode, PostLaunchAction postLaunchAction, String param, boolean abortLaunchOnException) {
		this.name = name;
		this.mode = mode;
		this.postLaunchAction = postLaunchAction;
		this.param = param;
		this.abortLaunchOnException = abortLaunchOnException;
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
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
		propertyChangeSupport.firePropertyChange("mode", this.mode, this.mode = mode);
	}

	public PostLaunchAction getPostLaunchAction() {
		return postLaunchAction;
	}

	public void setPostLaunchAction(PostLaunchAction postLaunchAction) {
		propertyChangeSupport.firePropertyChange("postLaunchAction", this.postLaunchAction, this.postLaunchAction = postLaunchAction);
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		propertyChangeSupport.firePropertyChange("param", this.param, this.param = param);
	}

	public boolean isAbortLaunchOnError() {
		return abortLaunchOnException;
	}

	public void setAbortLaunchOnException(boolean abortLaunchOnException) {
		propertyChangeSupport.firePropertyChange("abortLaunchOnException", this.abortLaunchOnException, this.abortLaunchOnException = abortLaunchOnException);
	}
}
